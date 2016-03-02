/* global YAHOO, Alfresco */

if (typeof LogicECM == 'undefined' || !LogicECM) {
	LogicECM = {};
}

LogicECM.module = LogicECM.module || {};
LogicECM.module.DocumentsTemplates = LogicECM.module.DocumentsTemplates || {};

(function () {
	var Dom = YAHOO.util.Dom,
		Event = YAHOO.util.Event,
		Bubbling = YAHOO.Bubbling;

	LogicECM.module.DocumentsTemplates.Attributes = function (containerId, options, messages) {
		LogicECM.module.DocumentsTemplates.Attributes.superclass.constructor.call(this, 'LogicECM.module.DocumentsTemplates.Attributes', containerId);
		this.setOptions(options);
		this.setMessages(messages);
		this._init();
		Bubbling.on('addTemplateAttribute', this.onAddTemplateAttribute, this);
		Bubbling.on('clearTemplateAttributes', this.onClearTemplateAttributes, this);
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

		templates: {},

		_hasEventInterest: function (obj) {
			if (!this.options.bubblingLabel || !obj || !obj.bubblingLabel) {
				return true;
			} else {
				return this.options.bubblingLabel === obj.bubblingLabel;
			}
		},

		_init: function () {
			var form = Dom.get(this.options.formId);
			this.templateDocType = YAHOO.util.Selector.query('input[name="prop_lecm-template_doc-type"]', form, true).value;
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
				label: 'Атрибут',
				className: 'attribute-td',
				sortable: false,
				// width: 350,
				// maxAutoWidth: 350,
				formatter: this._attributeFormatter
			}, {
				key: 'value',
				label: 'Значение',
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
			debugger;
			elCell.innerHTML = YAHOO.lang.substitute(this.owner.templates.deleteTemplate, {
				id: record.getId(),
				title: 'Удалить условие'
			});
		},

		_attributeFormatter: function (elCell, record, column, data) {
			/* this == this.widgets.datatable */
			debugger;

			function onSelectAvailable(obj) {
				/* this == elSelect */
				debugger;
				var eventObj = {
					elSelect: this,
					elCell: obj.elCell,
					record: obj.record
				};
				Event.on(this, 'change', obj.owner.onChangeAttribute, eventObj, obj.owner);
				obj.owner.onChangeAttribute.call(obj.owner, null, eventObj);
			}

			Event.onAvailable(record.getId() + '-attribute', onSelectAvailable, {
				owner: this.owner,
				elCell: elCell,
				record: record
			});

			this.owner.getFields().then(function (fields) {
				var options = fields.reduce(function (prev, curr) {
					return YAHOO.lang.substitute('{prev}<option data-attribute="{attribute}" value="{value}">{label}</option>', {
						prev: prev,
						attribute: JSON.stringify(curr).replace(/"/g, '&quot;'),
						value: curr.name,
						label: curr.label
					});
				}, '');

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
			if (!this._fieldsPromise.isDone() && this._fields && this._fields.length) {
				this._fieldsPromise.done(this._fields);
			}
			return this._fieldsPromise;
		},

		initDatasource: function (initialRequest, datasource, callback) {
			debugger;
		},

		onDataReturn: function () {
			debugger;
		},

		onDatatableRendered: function () {
			var hasRecords = this.widgets.datatable.getRecordSet().getLength();
			if (hasRecords) {
				Dom.removeClass(this.id + '-datatable', 'hidden');
			} else {
				Dom.addClass(this.id + '-datatable', 'hidden');
			}
		},

		onChangeAttribute: function (event, obj) {
			/* event can be null */
			debugger;
			var selectedOption = obj.elSelect.options[obj.elSelect.selectedIndex],
				dataStr = (selectedOption.dataset) ? selectedOption.dataset.attribute : selectedOption.getAttribute('data-attribute'),
				field = JSON.parse(dataStr),
				fieldType = (this.DICTIONARY_TYPES.indexOf(field.dataType) > -1) ? 'd:' + field.dataType : field.dataType,
				fieldParams = (field.control.params && field.control.params.length) ? field.control.params : [],
				params = fieldParams.reduce(function(prev, curr) {
					var param = {};
					param[curr.name] = curr.value;
					return YAHOO.lang.merge(prev, param);
				}, this.defaultParams);
			obj.record.setData('attribute', field);
			Alfresco.util.Ajax.request({
				url: Alfresco.constants.URL_SERVICECONTEXT + 'lecm/components/control',
				dataObj: {
					fieldId: field.name.replace(/:/g, '_'),
					labelId: field.label,
					type: fieldType,
					template: field.control.template,
					htmlid: obj.record.getId() + '-value-ctrl',
					params: params
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

		onAddTemplateAttributeRow: function (obj) {
			var record = obj.record,
				data = obj.data;
		},

		onAddTemplateAttribute: function (layer, args) {
			//добавление строки в датагрид
			//получение списка неиспользованных атрибутов
			//построение контрола по выбранному атрибуту
			var obj = args[1];
			if (this._hasEventInterest(obj)) {
				this.widgets.datatable.addRow({
					'delete': null,
					'attribute': null,
					'value': null
				});
			}
		},

		onDeleteTemplateAttribute: function (event) {
			var target = event.target;
			var column = this.widgets.datatable.getColumn(event.target);
			var record = this.widgets.datatable.getRecord(event.target);
			if ('delete' === column.key) {
				//удаление записи из датагрида
				return false;
			}
		},

		onClearTemplateAttributes: function (layer, args) {

		},

		onReady: function () {
			/*
			 * скорее всего контрол будет предствлять собой YAHOO.widget.Datatable
			 * каждая строка будет состоять из 3х колонок: действие "удалить", "выпадашка с атрибутами", "поле для контрола"
			*/
			console.log(this.name + '[' + this.id + '] is ready');
			this.widgets.hiddenValue = Dom.get(this.id + '-value');
			this.templates.deleteTemplate = Dom.get(this.id  + '-delete-template').innerHTML;
			this.templates.attributeTemplate = Dom.get(this.id  + '-attribute-template').innerHTML;
			this.templates.valueTemplate = Dom.get(this.id + '-value-template').innerHTML;
			this.widgets.datasource = new YAHOO.util.FunctionDataSource(this.initDatasource, {
				scope: this
			});
			this.widgets.datatable = new YAHOO.widget.DataTable(this.id + '-datatable', this.columnDefinitions, this.widgets.datasource);
			this.widgets.datatable.owner = this;
			this.widgets.datatable.on('dataReturnEvent', this.onDataReturn, null, this);
			this.widgets.datatable.on('renderEvent', this.onDatatableRendered, null, this);
			this.widgets.datatable.on('cellClickEvent', this.onDeleteTemplateAttribute, null, this);
			this.widgets.datatable.getRecordSet().subscribe('recordAddEvent', this.onAddTemplateAttributeRow, null, this);
		}
}, true);
})();
