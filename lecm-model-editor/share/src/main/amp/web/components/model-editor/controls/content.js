/* global YAHOO, Alfresco, IT */

if (typeof LogicECM == 'undefined' || !LogicECM) {
	LogicECM = {};
}

LogicECM.module = LogicECM.module || {};
LogicECM.module.ModelEditor = LogicECM.module.ModelEditor || {};

(function () {
	LogicECM.module.ModelEditor.ContentControl = function (containerId, options, messages) {
		LogicECM.module.ModelEditor.ContentControl.superclass.constructor.call(this, 'LogicECM.module.ModelEditor.ContentControl', containerId);
		this.setOptions(options);
		this.setMessages(messages);
		return this;
	};

	YAHOO.extend(LogicECM.module.ModelEditor.ContentControl, Alfresco.component.Base, {

		options: {
			itemKind: null,
			itemId: null,
			doctype: null
		},

		deferredInit: null,
		model: null,
		namespaces: null,
		tables: null,
		aspects: null,
		associations: null,

		_initModel: function(model) {
			var typeIndex = 0;
			if(YAHOO.lang.isObject(model.types)) {
				if(YAHOO.lang.isArray(model.types.type)) {
					for(var v in model.types.type) {
						if(this.options.doctype==model.types.type[v]._name) {
							typeIndex = v;
						}
					}
				} 
			}
			var modelObject = {};
			if(YAHOO.lang.isValue(model._name)) {
				modelObject.prop_cm_name = (model._name.substr(model._name.indexOf(':')+1,model._name.length)).replace('Model','');
				modelObject.prop_model_name = (model._name.substr(model._name.indexOf(':')+1,model._name.length));

				modelObject.prop_namespace_name = (model._name.substr(0,model._name.indexOf(':')));

				modelObject.model_description = model.description;
				modelObject.rating = 'false';
				modelObject.signed = 'false';
				if(YAHOO.lang.isObject(model.types)) {
					if(YAHOO.lang.isArray(model.types.type)) {
						modelObject.typeTitle = model.types.type[typeIndex].title;
						modelObject.prop_type_name = (model.types.type[typeIndex]._name.substr(model.types.type[typeIndex]._name.indexOf(':')+1,model.types.type[typeIndex]._name.length));

						modelObject.parentRef = model.types.type[typeIndex].parent;

						if(YAHOO.lang.isObject(model.types.type[typeIndex]['mandatory-aspects'])) {
							if(YAHOO.lang.isArray(model.types.type[typeIndex]['mandatory-aspects'].aspect)) {
								for(var v in model.types.type[typeIndex]['mandatory-aspects'].aspect) {
									if(model.types.type[typeIndex]['mandatory-aspects'].aspect[v]==='lecm-document-aspects:rateable') {
										modelObject.rating = 'true';
									} else if(model.types.type[typeIndex]['mandatory-aspects'].aspect[v]==='lecm-signed-docflow:docflowable') {
										modelObject.signed = 'true';
									}
								}
							} else if(model.types.type[typeIndex]['mandatory-aspects'].aspect==='lecm-document-aspects:rateable') {
								modelObject.rating = 'true';
							} else if(model.types.type[typeIndex]['mandatory-aspects'].aspect==='lecm-signed-docflow:docflowable') {
								modelObject.signed = 'true';
							}
						}
					} else if(YAHOO.lang.isObject(model.types.type)) {
						modelObject.typeTitle = model.types.type.title;
						modelObject.prop_type_name = (model.types.type._name.substr(model.types.type._name.indexOf(':')+1,model.types.type._name.length));

						modelObject.parentRef = model.types.type.parent;

						if(YAHOO.lang.isObject(model.types.type['mandatory-aspects'])) {
							if(YAHOO.lang.isArray(model.types.type['mandatory-aspects'].aspect)) {
								for(var v in model.types.type['mandatory-aspects'].aspect) {
									if(model.types.type['mandatory-aspects'].aspect[v]==='lecm-document-aspects:rateable') {
										modelObject.rating = 'true';
									} else if(model.types.type['mandatory-aspects'].aspect[v]==='lecm-signed-docflow:docflowable') {
										modelObject.signed = 'true';
									}
								}
							} else if(model.types.type['mandatory-aspects'].aspect==='lecm-document-aspects:rateable') {
								modelObject.rating = 'true';
							} else if(model.types.type['mandatory-aspects'].aspect==='lecm-signed-docflow:docflowable') {
								modelObject.signed = 'true';
							}
						}
					}
				}
			}
			//Constraints
			var tmpCategoryArray = [];
			if(YAHOO.lang.isObject(model.constraints)) {
				if(YAHOO.lang.isArray(model.constraints.constraint)) {
					var c = model.constraints.constraint;
					if (c instanceof Array) {
						for ( var i = 0, n = c.length; i < n; i++) {
							//Шаблон строки представления
							if(c[i]._name.indexOf(':present-string-constraint')!==-1) {
								if(YAHOO.lang.isArray(c[i].parameter)) {
									for(var p in c[i].parameter) {
										if(c[i].parameter[p]._name==='presentString') {
											if(YAHOO.lang.isString(c[i].parameter[p].value)){
												modelObject.presentString = c[i].parameter[p].value;
											} else if(YAHOO.lang.isObject(c[i].parameter[p].value)){
												modelObject.presentString = c[i].parameter[p].value['#cdata'];
											}
										}
									}
								}
								if(YAHOO.lang.isObject(c[i].parameter)) {
									if(c[i].parameter._name==='presentString') {
										if(YAHOO.lang.isString(c[i].parameter.value)){
											modelObject.presentString = c[i].parameter.value;
										} else if(YAHOO.lang.isObject(c[i].parameter.value)){
											modelObject.presentString = c[i].parameter.value['#cdata'];
										}
									}
								}
							}
							//АРМ
							if(c[i]._name.indexOf(':arm-url-constraint')!==-1) {
								if(YAHOO.lang.isArray(c[i].parameter)) {
									for(var p in c[i].parameter) {
										if(c[i].parameter[p]._name==='armUrl') {
											if(YAHOO.lang.isString(c[i].parameter[p].value)){
												modelObject.armUrl = c[i].parameter[p].value;
											}
										}
									}
								}
								if(YAHOO.lang.isObject(c[i].parameter)) {
									if(c[i].parameter._name==='armUrl') {
										if(YAHOO.lang.isString(c[i].parameter.value)){
											modelObject.armUrl = c[i].parameter.value;
										}
									}
								}
							}
							//Document Url
							if(c[i]._name.indexOf(':document-url-constraint')!==-1) {
								if(YAHOO.lang.isArray(c[i].parameter)) {
									for(var p in c[i].parameter) {
										if(c[i].parameter[p]._name==='createUrl') {
											if(YAHOO.lang.isString(c[i].parameter[p].value)){
												modelObject.createUrl = c[i].parameter[p].value;
											}
										}
										if(c[i].parameter[p]._name==='viewUrl') {
											if(YAHOO.lang.isString(c[i].parameter[p].value)){
												modelObject.viewUrl = c[i].parameter[p].value;
											}
										}
									}
								}
								if(YAHOO.lang.isObject(c[i].parameter)) {
									if(c[i].parameter._name==='createUrl') {
										if(YAHOO.lang.isString(c[i].parameter.value)){
											modelObject.createUrl = c[i].parameter.value;
										}
									}
									if(c[i].parameter._name==='viewUrl') {
										if(YAHOO.lang.isString(c[i].parameter.value)){
											modelObject.viewUrl = c[i].parameter.value;
										}
									}
								}
							}
							//Автор
							if(c[i]._name.indexOf(':author-property-constraint')!==-1) {
								if(YAHOO.lang.isArray(c[i].parameter)) {
									for(var p in c[i].parameter) {
										if(c[i].parameter[p]._name==='authorProperty') {
											if(YAHOO.lang.isString(c[i].parameter[p].value)){
												modelObject.authorProperty = c[i].parameter[p].value;
											}
										}
									}
								}
								if(YAHOO.lang.isObject(c[i].parameter)) {
									if(c[i].parameter._name==='authorProperty') {
										if(YAHOO.lang.isString(c[i].parameter.value)){
											modelObject.authorProperty = c[i].parameter.value;
										}
									}
								}
							}
							//Рег номера
							if(c[i]._name.indexOf(':reg-number-properties-constraint')!==-1) {
								if(YAHOO.lang.isArray(c[i].parameter)) {
									for(var p in c[i].parameter) {
										if(c[i].parameter[p]._name==='regNumbersProperties') {
											if(YAHOO.lang.isString(c[i].parameter[p].value)){
												modelObject.regNumbersProperties = c[i].parameter[p].value;
											}
										}
									}
								}
								if(YAHOO.lang.isObject(c[i].parameter)) {
									if(c[i].parameter._name==='regNumbersProperties') {
										if(YAHOO.lang.isString(c[i].parameter.value)){
											modelObject.regNumbersProperties = c[i].parameter.value;
										}
									}
								}
							}
							//Категории вложений
							if(c[i]._name.indexOf(':attachment-categories')!==-1) {
								if(YAHOO.lang.isArray(c[i].parameter)) {
									for(var p in c[i].parameter) {
										if(c[i].parameter[p]._name==='allowedValues') {
											if(YAHOO.lang.isObject(c[i].parameter[p].list)){
												if(YAHOO.lang.isArray(c[i].parameter[p].list.value)){
													for(var v in c[i].parameter[p].list.value) {
														tmpCategoryArray.push({'name':c[i].parameter[p].list.value[v]});
													}
												}
												if(YAHOO.lang.isString(c[i].parameter[p].list.value)){
													tmpCategoryArray.push({'name':c[i].parameter[p].list.value});
												}
											}
										}
									}
								}
								if(YAHOO.lang.isObject(c[i].parameter)) {
									if(c[i].parameter._name==='allowedValues') {
										if(YAHOO.lang.isObject(c[i].parameter.list)){
											if(YAHOO.lang.isArray(c[i].parameter.list.value)){
												for(var v in c[i].parameter.list.value) {
													tmpCategoryArray.push({'name':c[i].parameter.list.value[v]});
												}
											}
											if(YAHOO.lang.isString(c[i].parameter.list.value)){
												tmpCategoryArray.push({'name':c[i].parameter.list.value});
											}
										}
									}
								}
							}
						}
					}
				}
				else if(YAHOO.lang.isObject(model.constraints.constraint)) {
					var c = model.constraints.constraint;
					//Шаблон строки представления
					if(c._name.indexOf(':present-string-constraint')!==-1) {
						if(YAHOO.lang.isArray(c.parameter)) {
							for(var p in c.parameter) {
								if(c.parameter[p]._name==='presentString') {
									if(YAHOO.lang.isString(c.parameter[p].value)){
										modelObject.presentString = c.parameter[p].value;
									} else if(YAHOO.lang.isObject(c.parameter[p].value)){
										modelObject.presentString = c.parameter[p].value['#cdata'];
									}
								}
							}
						}
						if(YAHOO.lang.isObject(c.parameter)) {
							if(c.parameter._name==='presentString') {
								if(YAHOO.lang.isString(c.parameter.value)){
									modelObject.presentString = c.parameter.value;
								} else if(YAHOO.lang.isObject(c.parameter.value)){
									modelObject.presentString = c.parameter.value['#cdata'];
								}
							}
						}
					}
					//АРМ
					if(c._name.indexOf(':arm-url-constraint')!==-1) {
						if(YAHOO.lang.isArray(c.parameter)) {
							for(var p in c.parameter) {
								if(c.parameter[p]._name==='armUrl') {
									if(YAHOO.lang.isString(c.parameter[p].value)){
										modelObject.armUrl = c.parameter[p].value;
									}
								}
							}
						}
						if(YAHOO.lang.isObject(c.parameter)) {
							if(c.parameter._name==='armUrl') {
								if(YAHOO.lang.isString(c.parameter.value)){
									modelObject.armUrl = c.parameter.value;
								}
							}
						}
					}
					//Document Url
					if(c._name.indexOf(':document-url-constraint')!==-1) {
						if(YAHOO.lang.isArray(c.parameter)) {
							for(var p in c.parameter) {
								if(c.parameter[p]._name==='createUrl') {
									if(YAHOO.lang.isString(c.parameter[p].value)){
										modelObject.createUrl = c.parameter[p].value;
									}
								}
								if(c.parameter[p]._name==='viewUrl') {
									if(YAHOO.lang.isString(c.parameter[p].value)){
										modelObject.viewUrl = c.parameter[p].value;
									}
								}
							}
						}
						if(YAHOO.lang.isObject(c.parameter)) {
							if(c.parameter._name==='createUrl') {
								if(YAHOO.lang.isString(c.parameter.value)){
									modelObject.createUrl = c.parameter.value;
								}
							}
							if(c.parameter._name==='viewUrl') {
								if(YAHOO.lang.isString(c.parameter.value)){
									modelObject.viewUrl = c.parameter.value;
								}
							}
						}
					}
					//Автор
					if(c._name.indexOf(':author-property-constraint')!==-1) {
						if(YAHOO.lang.isArray(c.parameter)) {
							for(var p in c.parameter) {
								if(c.parameter[p]._name==='authorProperty') {
									if(YAHOO.lang.isString(c.parameter[p].value)){
										modelObject.authorProperty = c.parameter[p].value;
									}
								}
							}
						}
						if(YAHOO.lang.isObject(c.parameter)) {
							if(c.parameter._name==='authorProperty') {
								if(YAHOO.lang.isString(c.parameter.value)){
									modelObject.authorProperty = c.parameter.value;
								}
							}
						}
					}
					//Рег номера
					if(c._name.indexOf(':reg-number-properties-constraint')!==-1) {
						if(YAHOO.lang.isArray(c.parameter)) {
							for(var p in c.parameter) {
								if(c.parameter[p]._name==='regNumbersProperties') {
									if(YAHOO.lang.isString(c.parameter[p].value)){
										modelObject.regNumbersProperties = c.parameter[p].value;
									}
								}
							}
						}
						if(YAHOO.lang.isObject(c.parameter)) {
							if(c.parameter._name==='regNumbersProperties') {
								if(YAHOO.lang.isString(c.parameter.value)){
									modelObject.regNumbersProperties = c.parameter.value;
								}
							}
						}
					}
					//Категории вложения
					if(c._name.indexOf(':attachment-categories')!==-1) {
						if(YAHOO.lang.isArray(c.parameter)) {
							for(var p in c.parameter) {
								if(c.parameter[p]._name==='allowedValues') {
									if(YAHOO.lang.isObject(c.parameter[p].list)){
										if(YAHOO.lang.isArray(c.parameter[p].list.value)){
											for(var v in c.parameter[p].list.value) {
												tmpCategoryArray.push( {'name':c.parameter[p].list.value[v]} );
											}
										}
										if(YAHOO.lang.isString(c.parameter[p].list.value)){
											tmpCategoryArray.push( {'name':c.parameter[p].list.value} );
										}
									}
								}
							}
						}
						if(YAHOO.lang.isObject(c.parameter)) {
							if(c.parameter._name==='allowedValues') {
								if(YAHOO.lang.isObject(c.parameter.list)){
									if(YAHOO.lang.isArray(c.parameter.list.value)){
										for(var v in c.parameter.list.value) {
											tmpCategoryArray.push({'name':c.parameter.list.value[v]});
										}
									}
									if(YAHOO.lang.isString(c.parameter.list.value)){
										tmpCategoryArray.push({'name':c.parameter.list.value});
									}
								}
							}
						}
					}
				}
			}
			modelObject.categoryArray = tmpCategoryArray;

			var tmpAttributesArray = [];
			var tmpAssociationsArray = [];
			if(YAHOO.lang.isObject(model.types)) {
				if(YAHOO.lang.isArray(model.types.type)) {
					//properties
					if(YAHOO.lang.isObject(model.types.type[typeIndex].properties)) {
						var p = model.types.type[typeIndex].properties.property;
						if (p instanceof Array) {
							for ( var i = 0, n = p.length; i < n; i++) {
								var index = (YAHOO.lang.isObject(p[i].index)?p[i].index._enabled:'true');
								var tokenised = (YAHOO.lang.isObject(p[i].index)?p[i].index.tokenised:'true');
								tmpAttributesArray[i] = {'_name':(p[i]._name.substr(p[i]._name.indexOf(':')+1,p[i]._name.length)), 'title':p[i].title, 'type':p[i].type, 'default':p[i]['default'], 'mandatory':p[i].mandatory, '_enabled':index, 'tokenised':tokenised};//,  'validator':p[i].constraints.constraint.ref};
							}
						} else {
							var index = (YAHOO.lang.isObject(p.index)?p.index._enabled:'true');
							var tokenised = (YAHOO.lang.isObject(p.index)?p.index.tokenised:'true');
							tmpAttributesArray.push({'_name':(p._name.substr(p._name.indexOf(':')+1,p._name.length)), 'title':p.title, 'type':p.type, 'default':p['default'], 'mandatory':p.mandatory, '_enabled':index, 'tokenised':tokenised});
						}
					}
					//associations
					if(YAHOO.lang.isObject(model.types.type[typeIndex].associations)) {
						var a = model.types.type[typeIndex].associations.association;
						if (a instanceof Array) {
							for (var i = 0, n = a.length; i < n; i++) {
								tmpAssociationsArray[i] = {'_name':(a[i]._name.substr(a[i]._name.indexOf(':')+1,a[i]._name.length)), 'title':a[i].title,
									'class':a[i].target['class'], 'mandatory':a[i].target.mandatory, 'many':a[i].target.many};
							}
						} else {
							tmpAssociationsArray.push({'_name':(a._name.substr(a._name.indexOf(':')+1,a._name.length)), 'title':a.title,
								'class':a.target['class'], 'mandatory':a.target.mandatory, 'many':a.target.many});
						}
					}
				}
				if(YAHOO.lang.isObject(model.types.type)) {
					//properties
					if(YAHOO.lang.isObject(model.types.type.properties)) {
						var p = model.types.type.properties.property;
						if (p instanceof Array) {
							for ( var i = 0, n = p.length; i < n; i++) {
								var index = (YAHOO.lang.isObject(p[i].index)?p[i].index._enabled:'true');
								var tokenised = (YAHOO.lang.isObject(p[i].index)?p[i].index.tokenised:'true');
								tmpAttributesArray[i] = {'_name':(p[i]._name.substr(p[i]._name.indexOf(':')+1,p[i]._name.length)), 'title':p[i].title, 'type':p[i].type, 'default':p[i]['default'], 'mandatory':p[i].mandatory, '_enabled':index, 'tokenised':tokenised};//,  'validator':p[i].constraints.constraint.ref};
							}
						} else {
							var index = (YAHOO.lang.isObject(p.index)?p.index._enabled:'true');
							var tokenised = (YAHOO.lang.isObject(p.index)?p.index.tokenised:'true');
							tmpAttributesArray.push({'_name':(p._name.substr(p._name.indexOf(':')+1,p._name.length)), 'title':p.title, 'type':p.type, 'default':p['default'], 'mandatory':p.mandatory, '_enabled':index, 'tokenised':tokenised});//,  'validator':p[i].constraints.constraint.ref});
						}
					}
					//associations
					if(YAHOO.lang.isObject(model.types.type.associations)) {
						var a = model.types.type.associations.association;
						if (a instanceof Array) {
							for (var i = 0, n = a.length; i < n; i++) {
								tmpAssociationsArray[i] = {'_name':(a[i]._name.substr(a[i]._name.indexOf(':')+1,a[i]._name.length)), 'title':a[i].title,
									'class':a[i].target['class'], 'mandatory':a[i].target.mandatory, 'many':a[i].target.many};
							}
						} else {
							tmpAssociationsArray.push({'_name':(a._name.substr(a._name.indexOf(':')+1,a._name.length)), 'title':a.title,
								'class':a.target['class'], 'mandatory':a.target.mandatory, 'many':a.target.many});
						}
					}
				}
			}
			modelObject.attributesArray = tmpAttributesArray;
			modelObject.associationsArray = tmpAssociationsArray;
			//Tables
			var tmpTablesArray = [],
				tmpAspectsArray = [],
				mandatoryAspects = [];

			function isTable(element, index, array) {
				return this==element.value;
			}

			function isRepeatableOrDocflowable(aspect) {
				return aspect == 'lecm-signed-docflow:docflowable' || aspect == 'lecm-document-aspects:rateable';
			}

			if(YAHOO.lang.isObject(model.types)) {
				if(YAHOO.lang.isArray(model.types.type)) {
					if(YAHOO.lang.isObject(model.types.type[typeIndex]['mandatory-aspects'])) {
						var a = model.types.type[typeIndex]['mandatory-aspects'].aspect;
						if (a instanceof Array) {
							for (var i = 0, n = a.length; i < n; i++) {
								if(a[i] && this.tables.some(isTable, a[i])) {
									tmpTablesArray.push({'table':a[i]});
								} else if (!isRepeatableOrDocflowable(a[i])) {
									//mandatoryAspects.push(a[i]);
									tmpAspectsArray.push({'aspect':a[i]});
								}
							}
						} else {
							if(a && this.tables.some(isTable, a)) {
								tmpTablesArray.push({'table':a});
							} else if (!isRepeatableOrDocflowable(a)) {
								//mandatoryAspects.push(a);
								tmpAspectsArray.push({'aspect':a});
							}
						}
					}
				}
				if(YAHOO.lang.isObject(model.types.type)) {
					//aspects
					if(YAHOO.lang.isObject(model.types.type['mandatory-aspects'])) {
						var a = model.types.type['mandatory-aspects'].aspect;
						if (a instanceof Array) {
							for (var i = 0, n = a.length; i < n; i++) {
								if(a[i] && this.tables.some(isTable, a[i])) {
									tmpTablesArray.push({'table':a[i]});
								} else if (!isRepeatableOrDocflowable(a[i])) {
									//mandatoryAspects.push(a[i]);
									tmpAspectsArray.push({'aspect':a[i]});
								}
							}
						} else {
							if(a && this.tables.some(isTable, a)) {
								tmpTablesArray.push({'table':a});
							} else if (!isRepeatableOrDocflowable(a)) {
								//mandatoryAspects.push(a);
								tmpAspectsArray.push({'aspect':a});
							}
						}
					}
				}
			}

			modelObject.tablesArray = tmpTablesArray;
			modelObject.aspectsArray = tmpAspectsArray;
			modelObject.mandatoryAspects = mandatoryAspects;

			return modelObject;
		},

		_initModelContent: function () {
			var nodeRef;
			if (this.options.itemKind === 'node' && this.options.itemId) {
				nodeRef = new Alfresco.util.NodeRef(this.options.itemId);
				Alfresco.util.Ajax.request({
					url: Alfresco.constants.PROXY_URI_RELATIVE + 'api/node/content/' + nodeRef.uri,
					successCallback: {
						scope: this,
						fn: function (successResponse) {
							var model;
							var responseXML = successResponse.serverResponse.responseXML;
							if (responseXML==null || responseXML.documentElement==null) {
								responseXML = IT.Utils.parseXML(successResponse.serverResponse.responseText);
							}
							model = YAHOO.lang.JSON.parse(IT.Utils.xml2json(responseXML,'')).model;
							this.model = this._initModel(model);
							//this.deferredInit.fulfil('initModelContent');
							this._deferredInit();
						}
					},
					failureMessage: this.msg('Не удалось получить модель')
				});
			} else {
				this.model = this._initModel({});
				//this.deferredInit.fulfil('initModelContent');
				this._deferredInit();
			}
		},

		_initAssociations: function () {
			Alfresco.util.Ajax.jsonGet({
				url: Alfresco.constants.PROXY_URI_RELATIVE + 'lecm/api/classes/sys_base/subclasses',
				dataObj: {
					r: true
				},
				successCallback: {
					scope: this,
					fn: function (successResponse) {
						this.associations = successResponse.json.map(function (assoc) {
							return {
								label: assoc.title + ' - ' + assoc.name,
								value: assoc.name
							};
						});
						this.associations.splice(0, 0, '');
						this.deferredInit.fulfil('initAssociations');
					}
				},
				failureMessage: this.msg('Не удалось получить ассоциации модели')
			});
		},

		_initTables: function () {
			//api/classes/lecm-document_tableDataAspect/subclasses
			Alfresco.util.Ajax.jsonGet({
				url: Alfresco.constants.PROXY_URI_RELATIVE + 'lecm/type/tables?doctype=lecm-document:tableDataAspect',
				dataObj: {
					r: false
				},
				successCallback: {
					scope: this,
					fn: function (successResponse) {
						this.tables = successResponse.json.data.map(function(table) {
							return {
								label: (table.aspectTitle?table.aspectTitle+" - "+table.aspectName:table.aspectName),
								value: table.aspectName,
								props: table.table.props,
								assocs: table.table.assocs
							};
						});
						this.tables.splice(0, 0, '');
						this.deferredInit.fulfil('initTables');
					}
				},
				failureMessage: this.msg('Не удалось получить табличные данные модели')
			});
		},
		
		_initAspects: function () {
			//api/classes/lecm-document_tableDataAspect/subclasses
			Alfresco.util.Ajax.jsonGet({
				url: Alfresco.constants.PROXY_URI_RELATIVE + 'lecm/type/aspects',
				dataObj: {
					r: false
				},
				successCallback: {
					scope: this,
					fn: function (successResponse) {
						this.aspects = successResponse.json.data.map(function(aspect) {
							return {
								label: (aspect.aspectTitle?aspect.aspectTitle+" - "+aspect.aspectName:aspect.aspectName),
								value: aspect.aspectName,
								props: aspect.aspect.props,
								assocs: aspect.aspect.assocs
							};
						});
						this.aspects.splice(0, 0, '');
						this.deferredInit.fulfil('initAspects');
					}
				},
				failureMessage: this.msg('Не удалось получить табличные данные модели')
			});
		},

		_initNamespaces: function () {
			Alfresco.util.Ajax.jsonGet({
				url: Alfresco.constants.PROXY_URI_RELATIVE + 'namespaces',
				successCallback: {
					scope: this,
					fn: function (successResponse) {
						this.namespaces = successResponse.json;
						this.deferredInit.fulfil('initNamespaces');
					}
				},
				failureMessage: this.msg('Не удалось получить пространства имен модели')
			});
		},

		_deferredInit: function () {
			LogicECM.module.ModelEditor.ModelPromise.done({
				model: this.model,
				namespaces: this.namespaces,
				tables: this.tables,
				aspects: this.aspects,
				associations: this.associations
			});
		},

		onReady: function () {
			this.deferredInit = new Alfresco.util.Deferred(['initAssociations', 'initTables', 'initAspects', 'initNamespaces'], {
				scope: this,
				fn: this._initModelContent
			});
			this._initAssociations();
			this._initTables();
			this._initAspects();
			this._initNamespaces();
		}
	}, true);
})();
