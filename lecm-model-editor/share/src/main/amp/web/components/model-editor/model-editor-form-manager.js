/* global YAHOO, Alfresco, IT */

if (typeof LogicECM == 'undefined' || !LogicECM) {
	LogicECM = {};
}

LogicECM.module = LogicECM.module || {};
LogicECM.module.ModelEditor = LogicECM.module.ModelEditor || {};

(function () {
	LogicECM.module.ModelEditor.FormManager = function (containerId, options, messages) {
		LogicECM.module.ModelEditor.FormManager.superclass.constructor.call(this, containerId);
		this.name = 'LogicECM.module.ModelEditor.FormManager';
		Alfresco.util.ComponentManager.reregister(this);
		this.options = YAHOO.lang.merge(this.options, LogicECM.module.ModelEditor.FormManager.superclass.options);
		this.setOptions(options);
		this.setMessages(messages);

		YAHOO.Bubbling.on('afterFormRuntimeInit', this.onAfterFormRuntimeInit, this);

		return this;
	};

	YAHOO.extend(LogicECM.module.ModelEditor.FormManager, Alfresco.component.FormManager, {

		component: null,
		runtime: null,

		_initFields: function (obj) {
			var form = YAHOO.util.Dom.get(this.runtime.formId);
			if (obj.model.prop_model_name) {
				form.elements['cm_lecmModelName'].value = obj.model.prop_model_name;
			}
			if (obj.model.prop_namespace_name) {
				form.elements['cm_lecmModelNamespace'].value = obj.model.prop_namespace_name;
			}
			if (obj.model.prop_type_name) {
				form.elements['cm_lecmTypeName'].value = obj.model.prop_type_name;
			}
			if (obj.model.model_description) {
				form.elements['cm_lecmModelDescription'].value = obj.model.model_description;
			}
			if (obj.model.typeTitle) {
				form.elements['cm_lecmTypeTitle'].value = obj.model.typeTitle;
			}
			if (obj.model.parentRef) {
				form.elements['cm_lecmParentRef'].value = obj.model.parentRef;
			}
			if (obj.model.presentString) {
				form.elements['cm_lecmPresentString'].value = obj.model.presentString;
			}
			if (obj.model.armUrl) {
				form.elements['cm_lecmArmUrl'].value = obj.model.armUrl;
			}
			if (obj.model.createUrl) {
				form.elements['cm_lecmCreateUrl'].value = obj.model.createUrl;
			}
			if (obj.model.viewUrl) {
				form.elements['cm_lecmViewUrl'].value = obj.model.viewUrl;
			}
			if (obj.model.authorProperty) {
				form.elements['cm_lecmAuthorProperty'].value = obj.model.authorProperty;
			}
			if (obj.model.regNumbersProperties) {
				form.elements['cm_lecmRegNumbersProperties'].value = obj.model.regNumbersProperties;
			}
			if (obj.model.rating) {
				form.elements['cm_lecmRating'].value = obj.model.rating;
			}
			if (obj.model.signed) {
				form.elements['cm_lecmSigned'].value = obj.model.signed;
			}
		},

		_createModelXml: function (obj) {
			var form = YAHOO.util.Dom.get(this.runtime.formId);
			var associations = Alfresco.util.ComponentManager.findFirst('LogicECM.module.ModelEditor.AssociationsDatatable');
			var attributes = Alfresco.util.ComponentManager.findFirst('LogicECM.module.ModelEditor.AttributesDatatable');
			var categories = Alfresco.util.ComponentManager.findFirst('LogicECM.module.ModelEditor.CategoriesDatatable');
			var tables = Alfresco.util.ComponentManager.findFirst('LogicECM.module.ModelEditor.TablesDatatable');
			var month = ['01', '02', '03', '04', '05', '06', '07', '08', '09', '10', '11', '12'];
			var cmName = form.elements['prop_cm_name'].value;
			var modelName = form.elements['cm_lecmModelName'].value ? form.elements['cm_lecmModelName'].value : cmName + 'Model';
			var namespace = form.elements['cm_lecmModelNamespace'].value ? form.elements['cm_lecmModelNamespace'].value : cmName + 'NS';
			var modelDescription = form.elements['cm_lecmModelDescription'].value;
			var typeName = form.elements['cm_lecmTypeName'].value ? form.elements['cm_lecmTypeName'].value : cmName;
			var typeTitle = form.elements['cm_lecmTypeTitle'].value;
			var parentRef = form.elements['cm_lecmParentRef'].value;
			var userName = Alfresco.constants.USERNAME;
			var modelPublished = new Date();
			var model = {
				_xmlns: 'http://www.alfresco.org/model/dictionary/1.0',
				_name: namespace + ':' + modelName,
				description: modelDescription,
				author: userName,
				published: modelPublished.getFullYear() + '-' + month[modelPublished.getMonth()] + '-' + (modelPublished.getDate()<10 ? '0' : '') + modelPublished.getDate(),
				version: '1.0',
				imports: {
					"import": [
						{ _uri: 'http://www.alfresco.org/model/dictionary/1.0', _prefix: 'd' },
						{ _uri: 'http://www.alfresco.org/model/content/1.0', _prefix: 'cm' },
						{ _uri: 'http://www.it.ru/logicECM/document/1.0', _prefix: 'lecm-document' },
						{ _uri: 'http://www.it.ru/logicECM/eds-document/1.0', _prefix: 'lecm-eds-document' }
					]
				},
				namespaces: {
					namespace: {
						_uri: null,
						_prefix: null
					}
				},
				constraints: {
					constraint: []
				},
				types: {
					type: {
						_name: namespace + ':' + typeName,
						title: typeTitle,
						parent: parentRef,
						properties: {
							property: []
						},
						associations: {
							association: []
						},
						'mandatory-aspects': {
							aspect: []
						}
					}
				}
			};
			var uri = 'http://www.it.ru/lecm/' + typeName + '/1.0';
			var records, i, j, clazz, NS, values, prop, assoc;

			for (i in obj.namespaces) {
				if (obj.namespaces[i].prefix === namespace) {
					uri = obj.namespaces[i].uri;
					break;
				}
			}
			model.namespaces.namespace._uri = uri;
			model.namespaces.namespace._prefix = namespace;

			if (associations && associations.widgets.datatable) {
				records = associations.widgets.datatable.getRecordSet().getRecords();
				for (i in records) {
					clazz = records[i].getData('class');
					NS = clazz.substr(0, clazz.indexOf(':'));
					if (NS !== namespace) {
						for (j in obj.namespaces) {
							if (obj.namespaces[j].prefix == NS && !IT.Utils.containsUri(model.imports["import"], { _uri: obj.namespaces[j].uri, _prefix: NS })) {
								model.imports["import"].push({ _uri: obj.namespaces[j].uri, _prefix: NS });
							}
						}
					}
				}
			}

			if (tables && tables.widgets.datatable) {
				records = tables.widgets.datatable.getRecordSet().getRecords();
				for (i in records) {
					clazz = records[i].getData('table');
					NS = clazz.substr(0, clazz.indexOf(':'));
					for (j in obj.namespaces) {
						if (obj.namespaces[j].prefix == NS && !IT.Utils.containsUri(model.imports["import"], { _uri: obj.namespaces[j].uri, _prefix: NS })) {
							model.imports["import"].push({ _uri: obj.namespaces[j].uri, _prefix: NS });
						}
					}
				}
			}

			if (categories && categories.widgets.datatable) {
				values = [];
				records = categories.widgets.datatable.getRecordSet().getRecords();
				for (i in records) {
					values.push(records[i].getData('name') || '');
				}
				if (values.length) {
					model.constraints.constraint.push({
						_name: namespace + ':attachment-categories',
						_type: 'LIST',
						parameter: {
							_name: 'allowedValues',
							list: {
								value: values
							}
						}
					});
				}
			}

			if (form.elements['cm_lecmPresentString'].value) {
				model.constraints.constraint.push({
					_name: namespace + ':present-string-constraint',
					_type: 'ru.it.lecm.documents.constraints.PresentStringConstraint',
					parameter: {
						_name: 'presentString',
						value: {
							'#cdata': form.elements['cm_lecmPresentString'].value
						}
					}
				});
			}

			if (form.elements['cm_lecmArmUrl'].value) {
				model.constraints.constraint.push({
					_name: namespace + ':arm-url-constraint',
					_type: 'ru.it.lecm.documents.constraints.ArmUrlConstraint',
					parameter: {
						_name: 'armUrl',
						value: form.elements['cm_lecmArmUrl'].value
					}
				});
			}

			if (form.elements['cm_lecmCreateUrl'].value || form.elements['cm_lecmViewUrl'].value) {
				values = [];
				if (form.elements['cm_lecmCreateUrl'].value) {
					values.push({
						_name: 'createUrl',
						value: form.elements['cm_lecmCreateUrl'].value
					});
				}
				if (form.elements['cm_lecmViewUrl'].value) {
					values.push({
						_name: 'viewUrl',
						value: form.elements['cm_lecmViewUrl'].value
					});
				}
				model.constraints.constraint.push({
					_name: namespace + ':document-url-constraint',
					_type: 'ru.it.lecm.documents.constraints.DocumentUrlConstraint',
					parameter: values
				});
			}

			if (form.elements['cm_lecmAuthorProperty'].value) {
				model.constraints.constraint.push({
					_name: namespace + ':author-property-constraint',
					_type: 'ru.it.lecm.documents.constraints.AuthorPropertyConstraint',
					parameter: {
						_name: 'authorProperty',
						value: form.elements['cm_lecmAuthorProperty'].value
					}
				});
			}

			if (form.elements['cm_lecmRegNumbersProperties'].value) {
				model.constraints.constraint.push({
					_name: namespace + ':reg-number-properties-constraint',
					_type: 'ru.it.lecm.documents.constraints.RegNumberPropertiesConstraint',
					parameter: {
						_name: 'regNumbersProperties',
						value: form.elements['cm_lecmRegNumbersProperties'].value
					}
				});
			}

			if (Object.getOwnPropertyNames(model.constraints.constraint).length === 0 || model.constraints.constraint.length === 0) {
				delete model.constraints;
			}

			if (attributes && attributes.widgets.datatable) {
				records = attributes.widgets.datatable.getRecordSet().getRecords();
				for (i in records) {
					prop = {
						_name: namespace + ':' + (records[i].getData('_name') || ''),
						title: records[i].getData('title') || '',
						type: records[i].getData('type') || '',
						mandatory: records[i].getData('mandatory') || 'false'
					};
					if (records[i].getData('default')) {
						prop['default'] = records[i].getData('default');
					}
					if (records[i].getData('_enabled') === 'true') {
						prop.index = {
							_enabled: records[i].getData('_enabled'),
							atomic: 'true',
							stored: 'false',
							tokenised: records[i].getData('tokenised') || 'both'
						};
					} else {
						prop.index = {
							_enabled: 'false'
						};
					}
					model.types.type.properties.property.push(prop);
				}
			}

			if (associations && associations.widgets.datatable) {
				records = associations.widgets.datatable.getRecordSet().getRecords();
				for (i in records) {
					model.types.type.associations.association.push({
						_name: namespace + ':' + (records[i].getData('_name') || '').replace(':', '_'),
						title: records[i].getData('title') || '',
						source: {
							mandatory: 'false',
							many: 'true'
						},
						target: {
							'class': records[i].getData('class') || '',
							mandatory: records[i].getData('mandatory') || '',
							many: records[i].getData('many') || ''
						}
					});
				}
			}

			if (form.elements['cm_lecmRating'].value === 'true') {
				if (!IT.Utils.containsUri(model.imports["import"], { _uri: 'http://www.it.ru/lecm/document/aspects/1.0', _prefix: 'lecm-document-aspects' })) {
					model.imports["import"].push({ _uri: 'http://www.it.ru/lecm/document/aspects/1.0', _prefix: 'lecm-document-aspects' });
				}
				if (!IT.Utils.contains(model.types.type['mandatory-aspects'].aspect, 'lecm-document-aspects:rateable')) {
					model.types.type['mandatory-aspects'].aspect.push('lecm-document-aspects:rateable');
				}
			} else {
				if (IT.Utils.contains(model.types.type['mandatory-aspects'].aspect, 'lecm-document-aspects:rateable')) {
					for (i = model.types.type['mandatory-aspects'].aspect.length - 1; i >= 0; i--) {
						if (model.types.type['mandatory-aspects'].aspect[i] === 'lecm-document-aspects:rateable') {
							model.types.type['mandatory-aspects'].aspect.splice(i, 1);
						}
					}
				}
			}

			if (form.elements['cm_lecmSigned'].value === 'true') {
				if (!IT.Utils.containsUri(model.imports["import"], { _uri: 'http://www.it.ru/lecm/model/signed-docflow/1.0', _prefix: 'lecm-signed-docflow' })) {
					model.imports["import"].push({ _uri: 'http://www.it.ru/lecm/model/signed-docflow/1.0', _prefix: 'lecm-signed-docflow' });
				}
				if (!IT.Utils.contains(model.types.type['mandatory-aspects'].aspect, 'lecm-signed-docflow:docflowable')) {
					model.types.type['mandatory-aspects'].aspect.push('lecm-signed-docflow:docflowable');
				}
			} else {
				if (IT.Utils.contains(model.types.type['mandatory-aspects'].aspect, 'lecm-signed-docflow:docflowable')) {
					for (i = model.types.type['mandatory-aspects'].aspect.length - 1; i >= 0; i--) {
						if (model.types.type['mandatory-aspects'].aspect[i] === 'lecm-signed-docflow:docflowable') {
							model.types.type['mandatory-aspects'].aspect.splice(i, 1);
						}
					}
				}
			}

			if (tables && tables.widgets.datatable) {
				records = tables.widgets.datatable.getRecordSet().getRecords();
				for (i in records) {
					if (!IT.Utils.contains(model.types.type['mandatory-aspects'].aspect, records[i].getData('table'))) {
						model.types.type['mandatory-aspects'].aspect.push(records[i].getData('table') || '');
					}
				}
			}

			if (obj.model.mandatoryAspects && obj.model.mandatoryAspects.length) {
				obj.model.mandatoryAspects.forEach(function (aspect) {
					var ns = aspect.substr(0, aspect.indexOf(':'));

					for (j in obj.namespaces) {
						if (obj.namespaces[j].prefix == ns && !IT.Utils.containsUri(model.imports["import"], { _uri: obj.namespaces[j].uri, _prefix: ns })) {
							model.imports["import"].push({ _uri: obj.namespaces[j].uri, _prefix: ns });
						}
					}

					model.types.type['mandatory-aspects'].aspect.push(aspect);
				}, this);
			}

			if (Object.getOwnPropertyNames(model.types.type['mandatory-aspects'].aspect).length === 0 || model.types.type['mandatory-aspects'].aspect.length === 0) {
				delete model.types.type['mandatory-aspects'];
			}

			form.elements['prop_cm_content'].value = IT.Utils.json2xml({ model: model }, '');
		},

		onBeforeFormRuntimeInit: function (layer, args) {
			LogicECM.module.ModelEditor.FormManager.superclass.onBeforeFormRuntimeInit.call(this, layer, args);
			args[1].runtime.doBeforeFormSubmit = {
				scope: this,
				obj: null,
				fn: this.onBeforeFormSubmit
			};
		},

		onAfterFormRuntimeInit: function (layer, args) {
			this.component = args[1].component;
			this.runtime = args[1].runtime;
			// здесь мы получим XML, или поднимем из него json, или куда-нибудь передадим
			LogicECM.module.ModelEditor.ModelPromise.then(this._initFields, this);
		},

		onBeforeFormSubmit: function (form, obj) {
			LogicECM.module.ModelEditor.ModelPromise.then(this._createModelXml, this);
		}
	}, true);
})();
