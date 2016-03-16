/* global YAHOO, Alfresco */

if (typeof LogicECM == 'undefined' || !LogicECM) {
	LogicECM = {};
}

LogicECM.module = LogicECM.module || {};
LogicECM.module.DocumentsTemplates = LogicECM.module.DocumentsTemplates || {};

(function () {
	var Dom = YAHOO.util.Dom,
		Event = YAHOO.util.Event,
		Selector = YAHOO.util.Selector,
		Bubbling = YAHOO.Bubbling;

	LogicECM.module.DocumentsTemplates.Attributes = function (containerId, options, messages) {
		LogicECM.module.DocumentsTemplates.Attributes.superclass.constructor.call(this, 'LogicECM.module.DocumentsTemplates.Attributes', containerId);
		this.setOptions(options);
		this.setMessages(messages);
		this._init();
		Bubbling.on('addTemplateAttribute', this.onAddTemplateAttribute, this);
		Bubbling.on('clearTemplateAttributes', this.onClearTemplateAttributes, this);
		Bubbling.on('beforeSubmitTemplate', this.onBeforeSubmitTemplate, this);
		return this;
	};

	YAHOO.lang.extend(LogicECM.module.DocumentsTemplates.Attributes, Alfresco.component.Base, {

		DICTIONARY_TYPES: [ 'any', 'encrypted', 'text', 'mltext', 'content', 'int', 'long', 'float', 'double', 'date', 'datetime', 'boolean', 'qname', 'noderef', 'childassocref', 'assocref', 'path', 'category', 'locale', 'version', 'period' ],

		_fields: null,

		_fieldsPromise: null,

		templateDocType: null,

		defaultParams: null,

		columnDefinitions: null,

		options: {
			formId: null,
			bubblingLabel: null
		},

		templates: null,

		selectedFields: null,

		_hasEventInterest: function (obj) {
			/* this === LogicECM.module.DocumentsTemplates.Attributes */
			if (!this.options.bubblingLabel || !obj || !obj.bubblingLabel) {
				return true;
			} else {
				return this.options.bubblingLabel === obj.bubblingLabel;
			}
		},

		_updateDisabledOptions: function () {
			/* this === LogicECM.module.DocumentsTemplates.Attributes */
			var options = Selector.query('.attribute-option', this.id + '-datatable');
			options.forEach(function(option) {
				if (this.selectedFields.hasOwnProperty(option.value) && !option.selected) {
					option.disabled = 'disabled';
				} else {
					option.removeAttribute('disabled');
				}
			}, this);
		},

		_init: function () {
			/* this === LogicECM.module.DocumentsTemplates.Attributes */
			var form = Dom.get(this.options.formId);
			this.templates = {};
			this.selectedFields = {};
			this.templateDocType = Selector.query('input[name="prop_lecm-template_doc-type"]', form, true).value;
			this.defaultParams = {
				defaultValue: '',
				docType: this.templateDocType,
				// endpointMany: true,
				showCreateNewButton: false,
				showCreateNewLink: false
			};
			this.columnDefinitions = [{
				key: 'delete',
				label: '',
				className: 'delete-td',
				sortable: false,
				width: 16,
				minWidth: 16,
				// maxAutoWidth: 16,
				formatter: this._deleteFormatter
			}, {
				key: 'attribute',
				label: this.msg('template-attributes-column-attribute.title'),
				className: 'attribute-td',
				sortable: false,
				// width: 350,
				// maxAutoWidth: 350,
				formatter: this._attributeFormatter
			}, {
				key: 'value',
				label: this.msg('template-attributes-column-value.title'),
				className: 'value-td',
				sortable: false,
				width: 400,
				minWidth: 400,
				// maxAutoWidth: 90000,
				formatter: this._valueFormatter
			}];
			this._fieldsPromise = new LogicECM.module.Base.SimplePromise();
			Alfresco.util.Ajax.jsonGet({
				url: Alfresco.constants.URL_SERVICECONTEXT + 'lecm/components/type/fields',
				dataObj: {
					formId: '',
					useDefaultForm: true,
					itemType: this.templateDocType
				},
				successCallback: {
					scope: this,
					fn: function (successResponse) {
						this._fields = successResponse.json.fields.sort(function (left, right) {
							if (left.label < right.label) {
								return -1;
							}
							if (left.label > right.label) {
								return 1;
							}
							return 0;
						});
						this._fieldsPromise.done(this._fields);
					}
				},
				failureMessage: this.msg('message.failure')
			});
		},

		_deleteFormatter: function (elCell, record, column, data) {
			/* this == this.widgets.datatable */
			elCell.innerHTML = YAHOO.lang.substitute(this.owner.templates.deleteTemplate, {
				id: record.getId(),
				title: this.owner.msg('template-attributes-column-delete.title')
			});
		},

		_attributeFormatter: function (elCell, record, column, data) {
			/* this == this.widgets.datatable */

			function onSelectAvailable(obj) {
				/* this == elSelect */
				var eventObj = {
					elSelect: this,
					record: obj.record
				};
				Event.on(this, 'change', obj.owner.onChangeAttribute, eventObj, obj.owner);
				obj.owner.onChangeAttribute.call(obj.owner, null, eventObj);
			}

			Event.onAvailable(record.getId() + '-attribute', onSelectAvailable, {
				owner: this.owner,
				record: record
			});

			this.owner.getFields().then(function (fields) {
				var options = fields.reduce(function (prev, curr) {
					return YAHOO.lang.substitute('{prev}<option class="attribute-option" data-attribute="{attribute}" {selected} {disabled} value="{value}">{label}</option>', {
						prev: prev,
						attribute: JSON.stringify(curr).replace(/"/g, '&quot;'),
						selected: this.initial.attribute === curr.name ? 'selected="selected"' : '',
						disabled: this.owner.selectedFields.hasOwnProperty(curr.name) ? 'disabled="disabled"' : '',
						value: curr.name,
						label: curr.label
					});
				}.bind({
					initial: this.record.getData('initial'),
					owner: this.owner
				}), '');

				this.elCell.innerHTML = YAHOO.lang.substitute(this.owner.templates.attributeTemplate, {
					id: this.record.getId(),
					options: options
				});
			}, {
				elCell: elCell,
				record: record,
				owner: this.owner
			});
		},

		_valueFormatter: function (elCell, record, column, data) {
			/* this == this.widgets.datatable */
			elCell.innerHTML = YAHOO.lang.substitute(this.owner.templates.valueTemplate, {
				id: record.getId()
			});
		},

		getFields: function () {
			/* this === LogicECM.module.DocumentsTemplates.Attributes */
			if (!this._fieldsPromise.isDone() && this._fields && this._fields.length) {
				this._fieldsPromise.done(this._fields);
			}
			return this._fieldsPromise;
		},

		initDatasource: function (initialRequest, datasource, callback) {
			/* this === LogicECM.module.DocumentsTemplates.Attributes */
			var dataStr = this.widgets.hiddenValue.value,
				templateData = dataStr ? JSON.parse(dataStr) : [];

			callback.scope.owner = this;

			return templateData;
		},

		// onDataReturn: function () {
		// 	/* this === LogicECM.module.DocumentsTemplates.Attributes */
		// },

		onDatatableRendered: function () {
			/* this === LogicECM.module.DocumentsTemplates.Attributes */
			var hasRecords = this.widgets.datatable.getRecordSet().getLength();
			if (hasRecords) {
				Dom.removeClass(this.id + '-datatable', 'hidden');
			} else {
				Dom.addClass(this.id + '-datatable', 'hidden');
			}
		},

		onChangeAttribute: function (event, obj) {
			/* event can be null */
			/* this === LogicECM.module.DocumentsTemplates.Attributes */
			var selectedOption = obj.elSelect.options[obj.elSelect.selectedIndex],
				dataStr = (selectedOption.dataset) ? selectedOption.dataset.attribute : selectedOption.getAttribute('data-attribute'),
				initial = obj.record.getData('initial'),
				prevField = obj.record.getData('attribute'),
				field = JSON.parse(dataStr),
				fieldType = (this.DICTIONARY_TYPES.indexOf(field.dataType) > -1) ? 'd:' + field.dataType : field.dataType,
				fieldParams = (field.control.params && field.control.params.length) ? field.control.params : [],
				params = fieldParams.reduce(function(prev, curr) {
					var param = {};
					param[curr.name] = curr.value;
					return YAHOO.lang.merge(prev, param);
				}, YAHOO.lang.merge(this.defaultParams, {
					endpointMany: field.endpointMany
				})),
				fieldId = field.name.replace(/:/g, '_'),
				htmlid = obj.record.getId() + '-value-ctrl';

			if (field.name === initial.attribute && initial.value) {
				params.defaultValue = initial.value;
			}

			if (prevField && this.selectedFields.hasOwnProperty(prevField.name)) {
				delete this.selectedFields[prevField.name];
			}
			this.selectedFields[field.name] = field;
			this._updateDisabledOptions();
			obj.record.setData('attribute', field);
			obj.record.setData('value', htmlid + '_' + fieldId);
			Alfresco.util.Ajax.request({
				url: Alfresco.constants.URL_SERVICECONTEXT + 'lecm/components/control',
				dataObj: {
					fieldId: fieldId,
					labelId: field.label,
					type: fieldType,
					template: field.control.template,
					htmlid: htmlid,
					params: JSON.stringify(params)
				},
				valueId: obj.record.getId() + '-value',
				successCallback: {
					scope: this,
					fn: function (successResponse) {
						var container = Dom.get(successResponse.config.valueId);
						container.innerHTML = successResponse.serverResponse.responseText;
					}
				},
				failureMessage: this.msg('message.failure'),
				execScripts: true
			});
		},

		//onAddTemplateAttributeRow: function (obj) {
		//	var record = obj.record,
		//		data = obj.data;
		//},

		onAddTemplateAttribute: function (layer, args) {
			/* this === LogicECM.module.DocumentsTemplates.Attributes */
			this.getFields().then(function (fields) {

				if (fields.some(function (field) {
						return !this.selectedFields.hasOwnProperty(field.name);
					}, this)) {

					var obj = args[1];
					if (this._hasEventInterest(obj)) {
						this.widgets.datatable.addRow({
							'initial': {
								dataType: null,
								formsName: null,
								attribute: null,
								type: null,
								value: null
							},
							'delete': null,
							'attribute': null,
							'value': null
						});
					}

				}

				var hasUnselectedFields = false,
					i = 0;
				while (!hasUnselectedFields && i < fields.length) {
					hasUnselectedFields = !this.selectedFields.hasOwnProperty(fields[i]);
					i++;
				}

				if (hasUnselectedFields) {

				}

			}, this);
		},

		onDeleteTemplateAttribute: function (event) {
			/* this === LogicECM.module.DocumentsTemplates.Attributes */
			var target = event.target,
				column = this.widgets.datatable.getColumn(event.target),
				record = this.widgets.datatable.getRecord(event.target),
				field = record.getData('attribute');
			if ('delete' === column.key) {
				if (field && this.selectedFields.hasOwnProperty(field.name)) {
					delete this.selectedFields[field.name];
					this._updateDisabledOptions();
				}
				this.widgets.datatable.deleteRow(event.target);
				return false;
			}
		},

		onClearTemplateAttributes: function (layer, args) {
			/* this === LogicECM.module.DocumentsTemplates.Attributes */

		},

		onBeforeSubmitTemplate: function (layer, args) {
			/* this === LogicECM.module.DocumentsTemplates.Attributes */
			var records = this.widgets.datatable.getRecordSet().getRecords();
			var templateData = records.reduce(function (prev, curr) {
				var obj = {
					initial: {
						dataType: curr.getData('attribute').dataType,
						formsName: curr.getData('attribute').formsName,
						attribute: curr.getData('attribute').name,
						type: curr.getData('attribute').type,
						value: Dom.get(curr.getData('value')).value
					}
				};
				curr.setData('initial', obj.initial);
				prev.push(obj);
				return prev;
			}, []);
			this.widgets.hiddenValue.value = JSON.stringify(templateData);
		},

		onReady: function () {
			console.log(this.name + '[' + this.id + '] is ready');
			this.widgets.hiddenValue = Dom.get(this.id + '-value');
			this.templates.deleteTemplate = Dom.get(this.id  + '-delete-template').innerHTML;
			this.templates.attributeTemplate = Dom.get(this.id  + '-attribute-template').innerHTML;
			this.templates.valueTemplate = Dom.get(this.id + '-value-template').innerHTML;
			this.widgets.datasource = new YAHOO.util.FunctionDataSource(this.initDatasource, {
				scope: this
			});
			this.widgets.datatable = new YAHOO.widget.DataTable(this.id + '-datatable', this.columnDefinitions, this.widgets.datasource);
			// this.widgets.datatable.on('dataReturnEvent', this.onDataReturn, null, this);
			this.widgets.datatable.on('renderEvent', this.onDatatableRendered, null, this);
			this.widgets.datatable.on('cellClickEvent', this.onDeleteTemplateAttribute, null, this);
			//this.widgets.datatable.getRecordSet().subscribe('recordAddEvent', this.onAddTemplateAttributeRow, null, this);
		}
}, true);
})();
