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
		Bubbling.on('templateOrganizationSelect', this.onTemplateOrganizationSelect, this);

		this.deferredListPopulation = new Alfresco.util.Deferred(["onReady", "onOrganizationSelect"],
			{
				fn: this.populateDataGrid,
				scope: this
			});
		return this;
	};

	YAHOO.lang.extend(LogicECM.module.DocumentsTemplates.Attributes, Alfresco.component.Base, {

		DICTIONARY_TYPES: [ 'any', 'encrypted', 'text', 'mltext', 'content', 'int', 'long', 'float', 'double', 'date', 'datetime', 'boolean', 'qname', 'noderef', 'childassocref', 'assocref', 'path', 'category', 'locale', 'version', 'period' ],

		DEFAULT_ORG_UNIT_PATH: '/app:company_home/cm:Business_x0020_platform/cm:LECM/cm:Сервис_x0020_Структура_x0020_организации_x0020_и_x0020_сотрудников/cm:Организация/cm:Структура/cm:Холдинг',

		_fields: null,

		_fieldsPromise: null,

		templateDocType: null,
		templateOrganization: null,

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

		_getPreviousComponents: function (idPrefix) {
			var components = Alfresco.util.ComponentManager.list();
			return components.filter(function (component) {
				return component.id.indexOf(this.idPrefix) === 0;
			}, {
				idPrefix: idPrefix
			});
		},

		_clearComponents: function (components) {
			components.forEach(function (component) {
				if (YAHOO.lang.isFunction(component.destroy)) {
					component.destroy();
				}
				Alfresco.util.ComponentManager.unregister(component);
			});
		},

		_init: function () {
			/* this === LogicECM.module.DocumentsTemplates.Attributes */
			var form = Dom.get(this.options.formId);
			this.templates = {};
			this.selectedFields = {};
			this.templateDocType = Selector.query('input[name="prop_lecm-template_doc-type"]', form, true).value;
			this.templateOrganization = {};
			this.templateOrganization.nodeRef = Selector.query('input[name="assoc_lecm-template_organizationAssoc"]', form, true).value;
			this.defaultParams = {
				defaultValue: '',
				docType: this.templateDocType,
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
				/* ALF-5513 */
				// width: 400,
				// minWidth: 400,
				// maxAutoWidth: 90000,
				/* ALF-5513 */
				formatter: this._valueFormatter
			}];
			this._fieldsPromise = new LogicECM.module.Base.SimplePromise();
			Alfresco.util.Ajax.jsonGet({
				url: Alfresco.constants.URL_SERVICECONTEXT + 'lecm/components/type/fields',
				dataObj: {
					formId: 'document-template-fields',
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
			var previousComponents = this._getPreviousComponents(obj.record.getId()),
				selectedOption = obj.elSelect.options[obj.elSelect.selectedIndex],
				dataStr = (selectedOption.dataset) ? selectedOption.dataset.attribute : selectedOption.getAttribute('data-attribute'),
				initial = obj.record.getData('initial'),
				prevField = obj.record.getData('attribute'),
				field = JSON.parse(dataStr),
				isAssoc = (this.DICTIONARY_TYPES.indexOf(field.dataType) == -1),
				fieldType = (this.DICTIONARY_TYPES.indexOf(field.dataType) > -1) ? 'd:' + field.dataType : field.dataType,
				fieldParams = (field.control.params && field.control.params.length) ? field.control.params : [],
				params = fieldParams.reduce(function (prev, curr) {
					var param = {};
					param[curr.name] = curr.value;
					return YAHOO.lang.merge(prev, param);
				}, YAHOO.lang.merge(this.defaultParams, {
					endpointMany: field.endpointMany
				})),
				fieldId = field.formsName,
				htmlid = obj.record.getId() + '-value-ctrl';

			/*Добавим фильтр по организации*/
			if (this.templateOrganization.nodeRef && isAssoc) {
				var filerAdded = false;
				var orgFilter, existsFilter, orgField;

				/*2 случая комплексных контролов*/
				for (var key in params) {
					if (params.hasOwnProperty(key)) {
						var prefix, index = key.indexOf("AdditionalFilter");
						if (index > 0) { // double-picker logic
							prefix = key.substring(0, index);
							orgField = params[prefix + 'Org_field'] && params[prefix + 'Org_field'].length > 0 ? params[prefix + 'Org_field'] : null;

							orgFilter =
								"{{IN_SAME_ORGANIZATION({strict:" + (params[prefix + 'UseStrictFilterByOrg'] ? params[prefix + 'UseStrictFilterByOrg'] : "false") +
								(orgField != null ? ", org_field:\\\"" + orgField + "\\\"" : "") +
								", organization:\\\"" + this.templateOrganization.nodeRef + "\\\"})}}";

							existsFilter = params[prefix + 'AdditionalFilter'];

							params[prefix + 'DoNotCheckAccess'] = true;
							params[prefix + 'AdditionalFilter'] = (existsFilter && existsFilter.length > 0 ? existsFilter + ' AND ' : '') + orgFilter;

							filerAdded = true;
						} else if (key.indexOf("additionalFilter") > 0) { // association-complex logic
							prefix = key.substring(0, key.indexOf("additionalFilter"));
							orgField = params[prefix + 'org_field'] && params[prefix + 'org_field'].length > 0 ? params[prefix + 'org_field'] : null;

							orgFilter =
								"{{IN_SAME_ORGANIZATION({strict:" + (params[prefix + 'useStrictFilterByOrg'] ? params[prefix + 'useStrictFilterByOrg'] : "false") +
								(orgField != null ? ", org_field:\\\"" + orgField + "\\\"" : "") +
								", organization:\\\"" + this.templateOrganization.nodeRef + "\\\"})}}";

							existsFilter = params[prefix + 'additionalFilter'];

							params[prefix + 'doNotCheckAccess'] = true;
							params[prefix + 'additionalFilter'] = (existsFilter && existsFilter.length > 0 ? existsFilter + ' AND ' : '') + orgFilter;

							filerAdded = true;
						}
						//подменяем XPATH
						var replacedPathProps = ["RootLocation", "_rootLocation", "rootLocation"];
						replacedPathProps.forEach(function (keyProp) {
							var indx = key.indexOf(keyProp);
							if (indx >= 0) {
								if (params[key] == "{organization}") {
									params[key] = (this.templateOrganization.orgUnitPath ? this.templateOrganization.orgUnitPath : this.DEFAULT_ORG_UNIT_PATH);
								}
							}
						}, this);
					}
				}
				if (!filerAdded) {/* по дефолту - добавляем*/
					orgField = params['org_field'] && params['org_field'].length > 0 ? params['org_field'] : null;
					orgFilter =
						"{{IN_SAME_ORGANIZATION({strict:" + (params['useStrictFilterByOrg'] ? params['useStrictFilterByOrg'] : "false") +
						(orgField != null ? ", org_field:\\\"" + orgField + "\\\"" : "") +
						", organization:\\\"" + this.templateOrganization.nodeRef + "\\\"})}}";

					params['doNotCheckAccess'] = true;
					params['additionalFilter'] = (params['additionalFilter'] && params['additionalFilter'].length > 0 ? params['additionalFilter'] + ' AND ' : '') + orgFilter;
				}
			}

			if (field.name === initial.attribute && initial.value) {
				params.defaultValue = initial.value;
			}

			if (prevField && this.selectedFields.hasOwnProperty(prevField.name)) {
				delete this.selectedFields[prevField.name];
				Bubbling.fire('validatorUnregister', {
					bubblingLabel: 'documentsTemplatesDetailsView',
					fieldId: htmlid + '_' + prevField.formsName
				});
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
				fieldConstraints: field.fieldConstraints,
				valueId: obj.record.getId() + '-value',
				previousComponents: previousComponents,
				successCallback: {
					scope: this,
					fn: function (successResponse) {
						var container = Dom.get(successResponse.config.valueId),
							markupAndScripts = Alfresco.util.Ajax.sanitizeMarkup(successResponse.serverResponse.responseText),
							markup = markupAndScripts[0],
							scripts = markupAndScripts[1];

						this._clearComponents(successResponse.config.previousComponents);
						container.innerHTML = markup;
						// Run the js code from the webscript's <script> elements
						setTimeout(scripts, 0);

						Bubbling.fire('validatorRegister', {
							bubblingLabel: 'documentsTemplatesDetailsView',
							fieldConstraints: successResponse.config.fieldConstraints,
							htmlId: successResponse.config.dataObj.htmlid
						});
					}
				},
				failureMessage: this.msg('message.failure'),
				execScripts: true
			});
		},

		onAddTemplateAttribute: function (layer, args) {
			/* this === LogicECM.module.DocumentsTemplates.Attributes */
			var obj = args[1];
			if (this._hasEventInterest(obj)) {
				this.getFields().then(function (fields) {
					var hasUnselectedFields = fields.some(function (field) {
						return !this.selectedFields.hasOwnProperty(field.name);
					}, this);

					if (hasUnselectedFields) {
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
					} else {
						Alfresco.util.PopupManager.displayMessage({text: this.msg("templare-no-attributes-to-add.message")});
					}
				}, this);
			}
		},

		onDeleteTemplateAttribute: function (event) {
			/* this === LogicECM.module.DocumentsTemplates.Attributes */
			var column = this.widgets.datatable.getColumn(event.target),
				record = this.widgets.datatable.getRecord(event.target),
				field = record.getData('attribute');
			if ('delete' === column.key) {
				if (field && this.selectedFields.hasOwnProperty(field.name)) {
					delete this.selectedFields[field.name];
					this._updateDisabledOptions();
				}
				this._clearComponents(this._getPreviousComponents(record.getId()));
				this.widgets.datatable.deleteRow(event.target);

				var htmlId = record.getId() + '-value-ctrl';
				Bubbling.fire('validatorUnregister', {
					bubblingLabel: 'documentsTemplatesDetailsView',
					fieldId: htmlId + '_' + field.formsName
				});

				return false;
			}
		},

		onClearTemplateAttributes: function (layer, args) {
			/* this === LogicECM.module.DocumentsTemplates.Attributes */
			var records = this.widgets.datatable.getRecordSet().getRecords();
			this.selectedFields = {};
			records.forEach(function (record) {
				this._clearComponents(this._getPreviousComponents(record.getId()));
			}, this);
			this.widgets.datatable.deleteRows(0, records.length);
			this.widgets.hiddenValue.value = '[]';
		},

		onUpdateUnitsField: function (organization, formId) {
			if (organization && organization.nodeRef && organization.nodeRef.length) {
				Alfresco.util.Ajax.jsonGet({
					url: Alfresco.constants.PROXY_URI + '/lecm/orgstructure/api/getUnitByOrg',
					dataObj: {
						nodeRef: organization.nodeRef
					},
					successCallback: {
						scope: this,
						fn: function (response) {
							var unit = new Alfresco.util.NodeRef(response.json.nodeRef);
							LogicECM.module.Base.Util.readonlyControl(formId, "lecm-template:unitAssoc", false);
							YAHOO.util.Event.onAvailable(LogicECM.module.Base.Util.getComponentReadyElementId(formId, "lecm-template:unitAssoc"), function() {
								YAHOO.Bubbling.fire("refreshItemList", {
									formId: formId,
									fieldId: "lecm-template:unitAssoc",
									additionalFilter: '@lecm\\-orgstr\\-aspects\\:linked\\-organization\\-assoc\\-ref:\"' + organization.nodeRef + '\" AND NOT(@sys\\:node\\-uuid:\"' + unit.id + '\")'
								});
							}, this);
						}

					},
					failureMessage: this.msg('message.failure'),
					scope: this
				});
			} else {
				LogicECM.module.Base.Util.reInitializeControl(formId, 'lecm-template:unitAssoc', {
					currentValue:""
				});
				LogicECM.module.Base.Util.readonlyControl(formId, "lecm-template:unitAssoc", true);
			}
		},

		onTemplateOrganizationSelect: function (layer, args) {
			var organization = null;

			var selectedItems = args[1].selectedItems;
			if (selectedItems != null) {
				var keys = Object.keys(selectedItems);
				for (var i = 0; i < keys.length; i++) {
					organization = selectedItems[keys[i]];
				}
			}

			if (this.templateOrganization.nodeRef && (!organization || organization.nodeRef != this.templateOrganization.nodeRef)) {
				this.onClearTemplateAttributes();
			}

			this.templateOrganization = organization ? organization : {};

			Bubbling.fire('updateButtonState', {
				disabledState: !this.templateOrganization.nodeRef
			});

			this.onUpdateUnitsField(this.templateOrganization, args[1].formId);
			this.deferredListPopulation.fulfil("onOrganizationSelect");
		},

		onBeforeSubmitTemplate: function (layer, args) {
			/* this === LogicECM.module.DocumentsTemplates.Attributes */
			var records = this.widgets.datatable.getRecordSet().getRecords();
			var templateData = records.map(function (record) {
				return {
					readonly: "true" === this.widgets.readonlyValue.value,
					initial: {
						dataType: record.getData('attribute').dataType,
						formsName: record.getData('attribute').formsName,
						attribute: record.getData('attribute').name,
						type: record.getData('attribute').type,
						value: Dom.get(record.getData('value')) ? Dom.get(record.getData('value')).value : null
					}
				};
			}, this);
			this.widgets.hiddenValue.value = JSON.stringify(templateData);
		},

		onReady: function () {
			this.widgets.hiddenValue = Dom.get(this.id + '-value');
			this.widgets.readonlyValue = Selector.query('input[name="prop_lecm-template_readonly"]', Dom.get(this.options.formId), true);
			this.templates.deleteTemplate = Dom.get(this.id  + '-delete-template').innerHTML;
			this.templates.attributeTemplate = Dom.get(this.id  + '-attribute-template').innerHTML;
			this.templates.valueTemplate = Dom.get(this.id + '-value-template').innerHTML;

			this.deferredListPopulation.fulfil("onReady");
		},

		populateDataGrid: function () {
			this.widgets.datasource = new YAHOO.util.FunctionDataSource(this.initDatasource, {
				scope: this
			});
			this.widgets.datatable = new YAHOO.widget.DataTable(this.id + '-datatable', this.columnDefinitions, this.widgets.datasource);
			this.widgets.datatable.on('renderEvent', this.onDatatableRendered, null, this);
			this.widgets.datatable.on('cellClickEvent', this.onDeleteTemplateAttribute, null, this);
		}
}, true);
})();
