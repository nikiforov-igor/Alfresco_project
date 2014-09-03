if (typeof IT == "undefined" || !IT) {
	var IT = {};
}

IT.component = IT.component || {};

(function() {
	var Dom = YAHOO.util.Dom, Bubbling = YAHOO.Bubbling, Button = YAHOO.widget.Button, DataTable = YAHOO.widget.DataTable, Lang = YAHOO.lang;
	
	IT.component.ModelEditor = function ModelEditor_constructor(htmlId, components) {
		IT.component.ModelEditor.superclass.constructor.call(this, "IT.component.ModelEditor", htmlId, components);
		
		return this;
	};

	YAHOO.extend(IT.component.ModelEditor, Alfresco.component.Base, 
	{
		//Свойства
		storage : null,
		
		modelObject : {},
		categoryArray : [],		
		attributesArray : [],
		associationsArray : [],
		tablesArray : [],
		namespaces : [],
		initSuccess : false,
		                       
		options : {
			nodeRef : null,
			currentUser : null
		},
		//Методы
		//Обработчик внешнего события от контролов ввода inputChangeEvent
		//TODO:Подумать как не обрабатывать события от полей диалога (Вариант 1: добавить свойство "посылать событие"
		onChangeValue : function(event, args) {
			var target = args[1].target;
			this[target.name] = ""+target.value;
		},
		//Обработчик события <ScopeID>showEditDialog
		onShowCategoryDialog : function() {
			this.categoryDialog.show();
		},//onShowCategoryDialog
		//Обработчик события <ScopeID>hideEditDialog
		onHideCategoryDialog : function(layer, args) {
			var validation = args[1], data = {};
			for(var i in validation.args) { data[validation.args[i].name] = validation.args[i].value; }
			this.widgets.categoriesDataTable.addRow(data, 0);
			this.categoryDialog.hide();
			Bubbling.fire("mandatoryControlValueUpdated", this);
		},//onHideCategoryDialog
		//Обработчик события <ScopeID>showEditDialog
		onShowAttributesDialog : function() {
			this.attributesDialog.show();
		},//onShowAttributesDialog
		//Обработчик события <ScopeID>hideEditDialog
		onHideAttributesDialog : function(layer, args) {
			var validation = args[1], data = {};
			for(var i in validation.args) { data[validation.args[i].name] = validation.args[i].value; }
			this.widgets.attributesDataTable.addRow(data, 0);
			this.attributesDialog.hide();
			Bubbling.fire("mandatoryControlValueUpdated", this);
		},//onHideAttributesDialog
		//Обработчик события <ScopeID>showEditDialog
		onShowAssociationsDialog : function() {
			this.associationsDialog.show();
		},//onShowAssociationsDialog
		//Обработчик события <ScopeID>hideEditDialog
		onHideAssociationsDialog : function(layer, args) {
			var validation = args[1], data = {};
			for(var i in validation.args) { data[validation.args[i].name] = validation.args[i].value; }
			this.widgets.associationsDataTable.addRow(data, 0);
			this.associationsDialog.hide();
			Bubbling.fire("mandatoryControlValueUpdated", this);
		},//onHideAssociationsDialog
		onShowTablesDialog : function() {
			this.tablesDialog.show();
		},//onShowTablesDialog
		//Обработчик события <ScopeID>hideEditDialog
		onHideTablesDialog : function(layer, args) {
			var validation = args[1], data = {};
			for(var i in validation.args) { data[validation.args[i].name] = validation.args[i].value; }
			this.widgets.tablesDataTable.addRow(data, 0);
			this.tablesDialog.hide();
			Bubbling.fire("mandatoryControlValueUpdated", this);
		},//onHideTablesDialog
		//Обработчик системного события onContentReady (ждет появления контента на странице)
		onReady : function ModelEditor_onReady() {
			Alfresco.logger.debug("Model-editor: ID - " + this.id + ", NodeRef - " + this.options.nodeRef + ", Form ID - " + this.options.formId + ", User - "+this.options.currentUser);
			//storage
			this.storage = YAHOO.util.StorageManager.get("html5", "LOCATION_LOCAL", {force: false});
			//_populateContent
			this._populateContent( { successCallback : { fn : this._populateAssoc, scope : this } } );
			//registerValidationHandler
			Bubbling.fire("registerValidationHandler",  { fieldId: this.id, handler: this._validate, args: this });
			Bubbling.on("inputChangeEvent", this.onChangeValue, this);
			//перехват изменения cm_name для генерации события inputChangeEvent
			Bubbling.fire("registerValidationHandler", {
				fieldId: this.id.replace("cm_content","cm_name"),
				handler: function(field, args, event, form, silent, message) {
					Bubbling.fire("inputChangeEvent", {target:field});
					return true;
				},
				when: "keyup"
		    }); 
			
		},//onReady
		//Инициализация внутренних объектов на основе полученного контента
		_initObjects: function() {
			if(YAHOO.lang.isValue(this.modelObject.model._name)) {
				this.prop_cm_name = (this.modelObject.model._name.substr(this.modelObject.model._name.indexOf(":")+1,this.modelObject.model._name.length)).replace("Model","");
				this.prop_model_name = (this.modelObject.model._name.substr(this.modelObject.model._name.indexOf(":")+1,this.modelObject.model._name.length));
				
				this.prop_namespace_name = (this.modelObject.model._name.substr(0,this.modelObject.model._name.indexOf(":")));
				
				this.model_description = this.modelObject.model.description;
				if(YAHOO.lang.isObject(this.modelObject.model.types)) {
					if(YAHOO.lang.isArray(this.modelObject.model.types.type)) {
						this.typeTitle = this.modelObject.model.types.type[0].title
						this.prop_type_name = (this.modelObject.model.types.type[0]._name.substr(this.modelObject.model.types.type[0]._name.indexOf(":")+1,this.modelObject.model.types.type[0]._name.length));

						this.parentRef = this.modelObject.model.types.type[0].parent;

						if(YAHOO.lang.isObject(this.modelObject.model.types.type[0]["mandatory-aspects"])) {
							if(YAHOO.lang.isArray(this.modelObject.model.types.type[0]["mandatory-aspects"].aspect)) {
								for(var v in this.modelObject.model.types.type[0]["mandatory-aspects"].aspect) {
									if(this.modelObject.model.types.type[0]["mandatory-aspects"].aspect[v]==="lecm-document-aspects:rateable") {
										this.rating = "true";
									} else if(this.modelObject.model.types.type[0]["mandatory-aspects"].aspect[v]==="lecm-signed-docflow:docflowable") {
										this.signed = "true";
									}
								}
							} else if(this.modelObject.model.types.type[0]["mandatory-aspects"].aspect==="lecm-document-aspects:rateable") {
								this.rating = "true";
							} else if(this.modelObject.model.types.type[0]["mandatory-aspects"].aspect==="lecm-signed-docflow:docflowable") {
								this.signed = "true";
							}
						}
					} else if(YAHOO.lang.isObject(this.modelObject.model.types.type)) {
						this.typeTitle = this.modelObject.model.types.type.title;
						this.prop_type_name = (this.modelObject.model.types.type._name.substr(this.modelObject.model.types.type._name.indexOf(":")+1,this.modelObject.model.types.type._name.length));

						this.parentRef = this.modelObject.model.types.type.parent;

						if(YAHOO.lang.isObject(this.modelObject.model.types.type["mandatory-aspects"])) {
							if(YAHOO.lang.isArray(this.modelObject.model.types.type["mandatory-aspects"].aspect)) {
								for(var v in this.modelObject.model.types.type["mandatory-aspects"].aspect) {
									if(this.modelObject.model.types.type["mandatory-aspects"].aspect[v]==="lecm-document-aspects:rateable") {
										this.rating = "true";
									} else if(this.modelObject.model.types.type["mandatory-aspects"].aspect[v]==="lecm-signed-docflow:docflowable") {
										this.signed = "true";
									}
								}
							} else if(this.modelObject.model.types.type["mandatory-aspects"].aspect==="lecm-document-aspects:rateable") {
								this.rating = "true";
							} else if(this.modelObject.model.types.type["mandatory-aspects"].aspect==="lecm-signed-docflow:docflowable") {
								this.signed = "true";
							}
						}
					}
				}
			}
			//Constraints
			var tmpCategoryArray = [];
			if(YAHOO.lang.isObject(this.modelObject.model.constraints)) {
				if(YAHOO.lang.isArray(this.modelObject.model.constraints.constraint)) {
					var c = this.modelObject.model.constraints.constraint;
					if (c instanceof Array) {
						for ( var i = 0, n = c.length; i < n; i++) {
							//Шаблон строки представления
							if(c[i]._name.indexOf(":present-string-constraint")!==-1) {
								if(YAHOO.lang.isArray(c[i].parameter)) {
									for(var p in c[i].parameter) {
										if(c[i].parameter[p]._name==="presentString") {
											if(YAHOO.lang.isString(c[i].parameter[p].value)){
												this.presentString = c[i].parameter[p].value;
											} else if(YAHOO.lang.isObject(c[i].parameter[p].value)){
												this.presentString = c[i].parameter[p].value["#cdata"];
											}
										}
									}
								}
								if(YAHOO.lang.isObject(c[i].parameter)) {
									if(c[i].parameter._name==="presentString") {
										if(YAHOO.lang.isString(c[i].parameter.value)){
											this.presentString = c[i].parameter.value;
										} else if(YAHOO.lang.isObject(c[i].parameter.value)){
											this.presentString = c[i].parameter.value["#cdata"];
										}
									}
								}
							}
							//АРМ
							if(c[i]._name.indexOf(":arm-url-constraint")!==-1) {
								if(YAHOO.lang.isArray(c[i].parameter)) {
									for(var p in c[i].parameter) {
										if(c[i].parameter[p]._name==="armUrl") {
											if(YAHOO.lang.isString(c[i].parameter[p].value)){
												this.armUrl = c[i].parameter[p].value;
											}
										}
									}
								}
								if(YAHOO.lang.isObject(c[i].parameter)) {
									if(c[i].parameter._name==="armUrl") {
										if(YAHOO.lang.isString(c[i].parameter.value)){
											this.armUrl = c[i].parameter.value;
										}
									}
								}
							}
							//Автор
							if(c[i]._name.indexOf(":author-property-constraint")!==-1) {
								if(YAHOO.lang.isArray(c[i].parameter)) {
									for(var p in c[i].parameter) {
										if(c[i].parameter[p]._name==="authorProperty") {
											if(YAHOO.lang.isString(c[i].parameter[p].value)){
												this.authorProperty = c[i].parameter[p].value;
											}
										}
									}
								}
								if(YAHOO.lang.isObject(c[i].parameter)) {
									if(c[i].parameter._name==="authorProperty") {
										if(YAHOO.lang.isString(c[i].parameter.value)){
											this.authorProperty = c[i].parameter.value;
										}
									}
								}
							}
							//Рег номера
							if(c[i]._name.indexOf(":reg-number-properties-constraint")!==-1) {
								if(YAHOO.lang.isArray(c[i].parameter)) {
									for(var p in c[i].parameter) {
										if(c[i].parameter[p]._name==="regNumbersProperties") {
											if(YAHOO.lang.isString(c[i].parameter[p].value)){
												this.regNumbersProperties = c[i].parameter[p].value;
											}
										}
									}
								}
								if(YAHOO.lang.isObject(c[i].parameter)) {
									if(c[i].parameter._name==="regNumbersProperties") {
										if(YAHOO.lang.isString(c[i].parameter.value)){
											this.regNumbersProperties = c[i].parameter.value;
										}
									}
								}
							}
							//Категории вложений
							if(c[i]._name.indexOf(":attachment-categories")!==-1) {
								if(YAHOO.lang.isArray(c[i].parameter)) {
									for(var p in c[i].parameter) {
										if(c[i].parameter[p]._name==="allowedValues") {
											if(YAHOO.lang.isObject(c[i].parameter[p].list)){
												if(YAHOO.lang.isArray(c[i].parameter[p].list.value)){
													for(var v in c[i].parameter[p].list.value) {
														tmpCategoryArray.push({"name":c[i].parameter[p].list.value[v]});
													}
												}
												if(YAHOO.lang.isString(c[i].parameter[p].list.value)){
													tmpCategoryArray.push({"name":c[i].parameter[p].list.value});
												}
											}
										}
									}
								}
								if(YAHOO.lang.isObject(c[i].parameter)) {
									if(c[i].parameter._name==="allowedValues") {
										if(YAHOO.lang.isObject(c[i].parameter.list)){
											if(YAHOO.lang.isArray(c[i].parameter.list.value)){
												for(var v in c[i].parameter.list.value) {
													tmpCategoryArray.push({"name":c[i].parameter.list.value[v]});
												}
											}
											if(YAHOO.lang.isString(c[i].parameter.list.value)){
												tmpCategoryArray.push({"name":c[i].parameter.list.value});
											}
										}
									}
								}
							}
						}
					}
				}
				else if(YAHOO.lang.isObject(this.modelObject.model.constraints.constraint)) {
					var c = this.modelObject.model.constraints.constraint;
					//Шаблон строки представления
					if(c._name.indexOf(":present-string-constraint")!==-1) {
						if(YAHOO.lang.isArray(c.parameter)) {
							for(var p in c.parameter) {
								if(c.parameter[p]._name==="presentString") {
									if(YAHOO.lang.isString(c.parameter[p].value)){
										this.presentString = c.parameter[p].value;
									}
								}
							}
						}
						if(YAHOO.lang.isObject(c.parameter)) {
							if(c.parameter._name==="presentString") {
								if(YAHOO.lang.isString(c.parameter.value)){
									this.presentString = c.parameter.value;
								}
							}
						}
					}
					//АРМ
					if(c._name.indexOf(":arm-url-constraint")!==-1) {
						if(YAHOO.lang.isArray(c.parameter)) {
							for(var p in c.parameter) {
								if(c.parameter[p]._name==="armUrl") {
									if(YAHOO.lang.isString(c.parameter[p].value)){
										this.armUrl = c.parameter[p].value;
									}
								}
							}
						}
						if(YAHOO.lang.isObject(c.parameter)) {
							if(c.parameter._name==="armUrl") {
								if(YAHOO.lang.isString(c.parameter.value)){
									this.armUrl = c.parameter.value;
								}
							}
						}
					}
					//Автор
					if(c._name.indexOf(":author-property-constraint")!==-1) {
						if(YAHOO.lang.isArray(c.parameter)) {
							for(var p in c.parameter) {
								if(c.parameter[p]._name==="authorProperty") {
									if(YAHOO.lang.isString(c.parameter[p].value)){
										this.authorProperty = c.parameter[p].value;
									}
								}
							}
						}
						if(YAHOO.lang.isObject(c.parameter)) {
							if(c.parameter._name==="authorProperty") {
								if(YAHOO.lang.isString(c.parameter.value)){
									this.authorProperty = c.parameter.value;
								}
							}
						}
					}
					//Рег номера
					if(c._name.indexOf(":reg-number-properties-constraint")!==-1) {
						if(YAHOO.lang.isArray(c.parameter)) {
							for(var p in c.parameter) {
								if(c.parameter[p]._name==="regNumbersProperties") {
									if(YAHOO.lang.isString(c.parameter[p].value)){
										this.regNumbersProperties = c.parameter[p].value;
									}
								}
							}
						}
						if(YAHOO.lang.isObject(c.parameter)) {
							if(c.parameter._name==="regNumbersProperties") {
								if(YAHOO.lang.isString(c.parameter.value)){
									this.regNumbersProperties = c.parameter.value;
								}
							}
						}
					}
					//Категории вложения
					if(c._name.indexOf(":attachment-categories")!==-1) {
						if(YAHOO.lang.isArray(c.parameter)) {
							for(var p in c.parameter) {
								if(c.parameter[p]._name==="allowedValues") {
									if(YAHOO.lang.isObject(c.parameter[p].list)){
										if(YAHOO.lang.isArray(c.parameter[p].list.value)){
											for(var v in c.parameter[p].list.value) {
												tmpCategoryArray.push( {"name":c.parameter[p].list.value[v]} );
											}
										}
										if(YAHOO.lang.isString(c.parameter[p].list.value)){
											tmpCategoryArray.push( {"name":c.parameter[p].list.value} );
										}
									}
								}
							}
						}
						if(YAHOO.lang.isObject(c.parameter)) {
							if(c.parameter._name==="allowedValues") {
								if(YAHOO.lang.isObject(c.parameter.list)){
									if(YAHOO.lang.isArray(c.parameter.list.value)){
										for(var v in c.parameter.list.value) {
											tmpCategoryArray.push({"name":c.parameter.list.value[v]});
										}
									}
									if(YAHOO.lang.isString(c.parameter.list.value)){
										tmpCategoryArray.push({"name":c.parameter.list.value});
									}
								}
							}
						}
					}
				}
			}
			this.categoryArray = tmpCategoryArray;
			
			var tmpAttributesArray = [];
			var tmpAssociationsArray = [];
			if(YAHOO.lang.isObject(this.modelObject.model.types)) {
				if(YAHOO.lang.isArray(this.modelObject.model.types.type)) {
					//properties
					if(YAHOO.lang.isObject(this.modelObject.model.types.type[0].properties)) {
						var p = this.modelObject.model.types.type[0].properties.property;
						if (p instanceof Array) {
							for ( var i = 0, n = p.length; i < n; i++) {
								var index = (YAHOO.lang.isObject(p[i].index)?p[i].index._enabled:null);
								tmpAttributesArray[i] = {"_name":(p[i]._name.substr(p[i]._name.indexOf(":")+1,p[i]._name.length)), "title":p[i].title, "type":p[i].type, "mandatory":p[i].mandatory, "_enabled":index};//,  "validator":p[i].constraints.constraint.ref};
							}
						} else {
							var index = (YAHOO.lang.isObject(p.index)?p.index._enabled:null);
							tmpAttributesArray.push({"_name":(p._name.substr(p._name.indexOf(":")+1,p._name.length)), "title":p.title, "type":p.type, "mandatory":p.mandatory, "_enabled":index});
						}
					}
					//associations
					if(YAHOO.lang.isObject(this.modelObject.model.types.type[0].associations)) {
						var a = this.modelObject.model.types.type[0].associations.association;
						if (a instanceof Array) {
							for (var i = 0, n = a.length; i < n; i++) {
								tmpAssociationsArray[i] = {"_name":(a[i]._name.substr(a[i]._name.indexOf(":")+1,a[i]._name.length)), "title":a[i].title, 
										"class":a[i].target["class"], "mandatory":a[i].target.mandatory, "many":a[i].target.many};
							}
						} else {
							tmpAssociationsArray.push({"_name":(a._name.substr(a._name.indexOf(":")+1,a._name.length)), "title":a.title, 
									"class":a.target["class"], "mandatory":a.target.mandatory, "many":a.target.many});
						}
					}
				}
				if(YAHOO.lang.isObject(this.modelObject.model.types.type)) {
					//properties
					if(YAHOO.lang.isObject(this.modelObject.model.types.type.properties)) {
						var p = this.modelObject.model.types.type.properties.property;
						if (p instanceof Array) {
							for ( var i = 0, n = p.length; i < n; i++) {
								var index = (YAHOO.lang.isObject(p[i].index)?p[i].index._enabled:null);
								tmpAttributesArray[i] = {"_name":(p[i]._name.substr(p[i]._name.indexOf(":")+1,p[i]._name.length)), "title":p[i].title, "type":p[i].type, "mandatory":p[i].mandatory, "_enabled":index};//,  "validator":p[i].constraints.constraint.ref};
							}
						} else {
							var index = (YAHOO.lang.isObject(p.index)?p.index._enabled:null);
							tmpAttributesArray.push({"_name":(p._name.substr(p._name.indexOf(":")+1,p._name.length)), "title":p.title, "type":p.type, "mandatory":p.mandatory, "_enabled":index});//,  "validator":p[i].constraints.constraint.ref});
						}
					}
					//associations
					if(YAHOO.lang.isObject(this.modelObject.model.types.type.associations)) {
						var a = this.modelObject.model.types.type.associations.association;
						if (a instanceof Array) {
							for (var i = 0, n = a.length; i < n; i++) {
								tmpAssociationsArray[i] = {"_name":(a[i]._name.substr(a[i]._name.indexOf(":")+1,a[i]._name.length)), "title":a[i].title, 
										"class":a[i].target["class"], "mandatory":a[i].target.mandatory, "many":a[i].target.many};
							}
						} else {
							tmpAssociationsArray.push({"_name":(a._name.substr(a._name.indexOf(":")+1,a._name.length)), "title":a.title, 
									"class":a.target["class"], "mandatory":a.target.mandatory, "many":a.target.many});
						}
					}
				}
			}
			this.attributesArray = tmpAttributesArray;
			this.associationsArray = tmpAssociationsArray;
			//Tables
			var tmpTablesArray = [];
			if(YAHOO.lang.isObject(this.modelObject.model.types)) {
				if(YAHOO.lang.isArray(this.modelObject.model.types.type)) {
					if(YAHOO.lang.isObject(this.modelObject.model.types.type[0]["mandatory-aspects"].aspect)) {
						var a = this.modelObject.model.types.type[0]["mandatory-aspects"].aspect;
						if (a instanceof Array) {
							for (var i = 0, n = a.length; i < n; i++) {
								if(a[i]&&a[i].indexOf("table")!==-1) {
									tmpTablesArray.push({"table":a[i]});
								}
							}
						} else {
							if(a&&a.indexOf("table")!==-1) {
								tmpTablesArray.push({"table":a});
							}
						}
					}
				}
				if(YAHOO.lang.isObject(this.modelObject.model.types.type)) {
					//associations
					if(YAHOO.lang.isObject(this.modelObject.model.types.type["mandatory-aspects"])) {
						var a = this.modelObject.model.types.type["mandatory-aspects"].aspect;
						if (a instanceof Array) {
							for (var i = 0, n = a.length; i < n; i++) {
								if(a[i]&&a[i].indexOf("table")!==-1) {
									tmpTablesArray.push({"table":a[i]});
								}
							}
						} else {
							if(a&&a.indexOf("table")!==-1) {
								tmpTablesArray.push({"table":a});
							}
						}
					}
				}
			}
			
			this.tablesArray = tmpTablesArray;
						
		},//_initObjects
		//Обработчик событий валидации формы (заполняет скрытое поле для отправки контента вместе с формой)
		_validate:  function validate_model(field, args, event, form, silent, message) {
			if(args.initSuccess==false) return true;
			var month=new Array();
			month[0]="01";month[1]="02";month[2]="03";month[3]="04";month[4]="05";month[5]="06";month[6]="07";month[7]="08";month[8]="09";month[9]="10";month[10]="11";month[11]="12";
			var namespace = (YAHOO.lang.isString(args.prop_namespace_name) ? args.prop_namespace_name : args.prop_cm_name+"NS");
			var modelName = (YAHOO.lang.isString(args.prop_model_name) ? args.prop_model_name : args.prop_cm_name+"Model");
			var modelDescription = args.model_description;
			var typeName = (YAHOO.lang.isString(args.prop_type_name) ? args.prop_type_name : args.prop_cm_name);
			var typeTitle = args.typeTitle;
			var parentRef = args.parentRef;
			var userName = args.options.currentUser;
			var modelPublished = new Date();
			//modelObject.model.types.type[0].properties.property
			if(!YAHOO.lang.isObject(args.modelObject.model)) {
				args.modelObject.model = {};
			}
					
			if(!YAHOO.lang.isValue(args.modelObject.model._name)||(args.modelObject.model._name!=""+namespace+":"+modelName)) {
				args.modelObject.model = {
					"_xmlns": "http://www.alfresco.org/model/dictionary/1.0",
					"_name":""+namespace+":"+modelName,
					"description":""+modelDescription,
					"author":""+userName,
					"published":""+modelPublished.getFullYear()+ 
								"-"+month[modelPublished.getMonth()]+
								"-"+(modelPublished.getDate()<10 ? "0" : "")+modelPublished.getDate(),
					"version":"1.0"
				};
				args.modelObject.model.imports = {"import" : []};
				args.modelObject.model.imports["import"].push({"_uri":"http://www.alfresco.org/model/dictionary/1.0","_prefix":"d"});
				args.modelObject.model.imports["import"].push({"_uri":"http://www.alfresco.org/model/content/1.0","_prefix":"cm"});
				args.modelObject.model.imports["import"].push({"_uri":"http://www.alfresco.org/model/system/1.0","_prefix":"sys"});
				args.modelObject.model.imports["import"].push({"_uri":"http://www.it.ru/logicECM/document/1.0","_prefix":"lecm-document"});
				args.modelObject.model.imports["import"].push({"_uri":"http://www.it.ru/logicECM/eds-document/1.0","_prefix":"lecm-eds-document"});
				args.modelObject.model.imports["import"].push({"_uri":"http://www.it.ru/lecm/document/aspects/1.0","_prefix":"lecm-document-aspects"});
				args.modelObject.model.imports["import"].push({"_uri":"http://www.it.ru/lecm/model/signed-docflow/1.0","_prefix":"lecm-signed-docflow"});
				var records = args.widgets.associationsDataTable.getRecordSet().getRecords();
				for(var i in records) {
					var rec = records[i];
					var clazz = rec.getData("class");
					var NS = clazz.substr(0,clazz.indexOf(":"));
					//TODO: Как по NS определять его URL?
					for(var n in args.namespaces) {
						if(args.namespaces[n].prefix==NS)
							args.modelObject.model.imports["import"].push({"_uri":args.namespaces[n].uri,"_prefix":NS});
					}
				}
				var records = args.widgets.tablesDataTable.getRecordSet().getRecords();
				for(var i in records) {
					var rec = records[i];
					var clazz = rec.getData("table");
					var NS = clazz.substr(0,clazz.indexOf(":"));
					//TODO: Как по NS определять его URL?
					for(var n in args.namespaces) {
						if(args.namespaces[n].prefix==NS)
							args.modelObject.model.imports["import"].push({"_uri":args.namespaces[n].uri,"_prefix":NS});
					}
				}
				var _uri = "http://www.it.ru/lecm/"+typeName+"/1.0";
				for(var n in args.namespaces) {
					if(args.namespaces[n].prefix==namespace) {
						_uri = args.namespaces[n].uri;
					}
				}
				args.modelObject.model.namespaces = {"namespace":{"_uri":_uri,"_prefix":namespace}};
			}
			args.modelObject.model.description = modelDescription;
			//constraints
			if(!YAHOO.lang.isObject(args.modelObject.model.constraints)) {
				args.modelObject.model.constraints = {}
			}
			//if(!YAHOO.lang.isObject(args.modelObject.model.constraints.constraint)) {
				args.modelObject.model.constraints.constraint = [];
			//}
			//attachment-categories
			var _val = [];
			var _cat = args.widgets.categoriesDataTable.getRecordSet().getRecords();
			for(var c in _cat) {
				_val.push((_cat[c].getData("name")||""));
			}
			if(YAHOO.lang.isArray(args.modelObject.model.constraints.constraint)) {
				if(_val.length>0) {
					args.modelObject.model.constraints.constraint.push({
						_name:namespace+":attachment-categories",
						_type:"LIST",
						parameter: {
							_name:"allowedValues",
							list:{
								value:_val
							}
						}
					});
				}
				if(args.presentString && args.presentString.length>0) {
					args.modelObject.model.constraints.constraint.push({
						_name:namespace+":present-string-constraint",
						_type:"ru.it.lecm.documents.constraints.PresentStringConstraint",
						parameter: {
							_name:"presentString",
							value: {
								"#cdata":args.presentString
							}
						}
					});
				}
				if(args.armUrl && args.armUrl.length>0) {
					args.modelObject.model.constraints.constraint.push({
						_name:namespace+":arm-url-constraint",
						_type:"ru.it.lecm.documents.constraints.ArmUrlConstraint",
						parameter: {
							_name:"armUrl",
							value:args.armUrl
						}
					});
				}
				if(args.authorProperty && args.authorProperty.length>0) {
					args.modelObject.model.constraints.constraint.push({
						_name:namespace+":author-property-constraint",
						_type:"ru.it.lecm.documents.constraints.AuthorPropertyConstraint",
						parameter: {
							_name:"authorProperty",
							value:args.authorProperty
						}
					});
				}
				if(args.regNumbersProperties && args.regNumbersProperties.length>0) {
					args.modelObject.model.constraints.constraint.push({
						_name:namespace+":reg-number-properties-constraint",
						_type:"ru.it.lecm.documents.constraints.RegNumberPropertiesConstraint",
						parameter: {
							_name:"regNumbersProperties",
							value:args.regNumbersProperties
						}
					});
				}
			}
			//types
			if(!YAHOO.lang.isObject(args.modelObject.model.types)) {
				args.modelObject.model.types = {}
			}
			if(!YAHOO.lang.isObject(args.modelObject.model.types.type)) {
				args.modelObject.model.types.type = {
						"_name":namespace+":"+typeName,
						"title":(typeTitle||""),
						"parent":(parentRef||"lecm-document:base"),
				};
			}
			//properties
			if(!YAHOO.lang.isObject(args.modelObject.model.types.type.properties)) {
				args.modelObject.model.types.type.properties = {};
			}
			if(!YAHOO.lang.isObject(args.modelObject.model.types.type.properties.property)) {
				args.modelObject.model.types.type.properties.property = [];
			}
			if(YAHOO.lang.isObject(args.modelObject.model.types.type.properties.property)) {
					var prop = null;
					var properties = [];
					var records = args.widgets.attributesDataTable.getRecordSet().getRecords();
					for(var i in records) {
						var rec = records[i];
						prop = {};
						prop._name = namespace+":"+(rec.getData("_name")||"");
						prop.title = (rec.getData("title")||"");
						prop.type = (rec.getData("type")||"");
						prop.mandatory = (rec.getData("mandatory")||"false");
						if(rec.getData("_enabled")==="true") {
							prop.index = {
								"_enabled":rec.getData("_enabled"),
								"atomic":"true",
								"stored":"false",
								"tokenised":"both"
							};
						}
						properties.push(prop);
					}
				args.modelObject.model.types.type.properties.property = properties;
			}
			//associations
			if(!YAHOO.lang.isObject(args.modelObject.model.types.type.associations)) {
				args.modelObject.model.types.type.associations = {};
			}
			if(!YAHOO.lang.isObject(args.modelObject.model.types.type.associations.association)) {
				args.modelObject.model.types.type.associations.association = [];
			}
			if(YAHOO.lang.isObject(args.modelObject.model.types.type.associations.association)) {
				var assoc = null;
				var associations = [];
				var records = args.widgets.associationsDataTable.getRecordSet().getRecords();
				for(var i in records) {
					var rec = records[i];
					assoc = {};
					assoc._name = namespace+":"+(rec.getData("_name")||"").replace(":","_");
					assoc.title = (rec.getData("title")||"");
					assoc.source = {
						"mandatory":"false",
						"many":"true"
					};
					assoc.target = {
						"class":(rec.getData("class")||""),
						"mandatory":(rec.getData("mandatory")||""),
						"many":(rec.getData("many")||"")
					};
					associations.push(assoc);
				}
				args.modelObject.model.types.type.associations.association = associations;
			}
			
			//rating
			if(!YAHOO.lang.isObject(args.modelObject.model.types.type["mandatory-aspects"])) {
				args.modelObject.model.types.type["mandatory-aspects"] = {};
			}
			if(!YAHOO.lang.isObject(args.modelObject.model.types.type["mandatory-aspects"].aspect)) {
				args.modelObject.model.types.type["mandatory-aspects"].aspect = [];
			}
			if(args.rating==="true") {
				if(YAHOO.lang.isObject(args.modelObject.model.types.type["mandatory-aspects"])){
					args.modelObject.model.types.type["mandatory-aspects"].aspect.push("lecm-document-aspects:rateable");
				}
			}
			if(args.signed==="true") {
				if(YAHOO.lang.isObject(args.modelObject.model.types.type["mandatory-aspects"])){
					args.modelObject.model.types.type["mandatory-aspects"].aspect.push("lecm-signed-docflow:docflowable");
				}
			}
			var records = args.widgets.tablesDataTable.getRecordSet().getRecords();
			for(var i in records) {
				var rec = records[i];
				args.modelObject.model.types.type["mandatory-aspects"].aspect.push((rec.getData("table")||""));
			}
			
			//json2xml
			var xval = json2xml(args.modelObject,"");
			//set field hidden input
			Dom.get(field.id).value = xval;
			//validation conditions
			//if(Dom.get(field.id).value.length > 0) return true;
			
			//Alfresco.util.Ajax.request({
			//	url : Alfresco.constants.URL_SERVICECONTEXT + "config",
			//	method : "POST",
			//	requestContentType : "application/json",
			//	dataObj: args.modelObject,
			//	successCallback : { fn : function GenericFormTool_onLoad_onFormLoaded(response)
	        //       {
	        //       } , scope : this },
			//	failureCallback : { fn : function GenericFormTool_onLoad_onFormLoaded(response)
	        //       {
	        //       }, scope : this }
			//});// request
			
			if(args.widgets.categoriesDataTable.getRecordSet().getLength()>0) return true;
			if(args.widgets.attributesDataTable.getRecordSet().getLength()>0) return true;
			if(args.widgets.associationsDataTable.getRecordSet().getLength()>0) return true;
			if(args.widgets.tablesDataTable.getRecordSet().getLength()>0) return true;
			return false;
		},//_validate
		//Получение списка типов для ассоциаций
		_populateTables : function ContentControl__populateTables(callback) {
			var onSuccessTables = function ContentControl__populateTables_onSuccess(response) {
				var r = response.json;
				var dTables = [""];
				for(j in r) {
					dTables.push({label:r[j].title+" - "+r[j].name,value:r[j].name});
				}
				
				//Ассоциации
				this.tablesDialogEl = [
		                            { name: "table", label: "Таблица", type:"select", options: dTables, showdefault: false }
		                        ];

				this.tablesColumnDefs = [ 
		                            { className: "viewmode-label", key:"table", label:"Таблица", dropdownOptions : dTables, formatter: "dropdown", width : 737, maxAutoWidth : 737 },
		                            { key : "delete", label : "", formatter:this._formatActions, width : 15, maxAutoWidth : 15 } 
		                        ];
				this.tablesResponseSchema = { fields : [{key : "table"}] };
				
				if (callback && callback.successCallback) {
					Dom.get(this.id+"_loading").setAttribute("style", "display:none");
					Dom.get(this.id+"_props").setAttribute("style", "display:block");
					callback.successCallback.fn.call(callback.successCallback.scope);
				}
			}
			var onFailureTables = function ContentControl__populateTables_onFailure(response) {
				
			};// onFailure
			Alfresco.util.Ajax.request({
				url : Alfresco.constants.PROXY_URI + "api/classes/lecm-document_tableDataAspect/subclasses?r=false",//"api/dictionary",
				method : "GET",
				successCallback : { fn : onSuccessTables, scope : this },
				failureCallback : { fn : onFailureTables, scope : this }
			});// request
		},
		_populateAssoc : function ContentControl__populateAssoc(callback) {
			var onSuccessAssoc = function ContentControl__populateAssoc_onSuccess(response) {
				var r = response.json;
				var dAssociations = [""];
				var dTypes = ["",{label:"Любой",value:"d:any"},{label:"Текст",value:"d:text"},{label:"Контент",value:"d:content"},{label:"Целое",value:"d:int"},{label:"Дл.целое",value:"d:long"},{label:"Дробное",value:"d:float"},{label:"Дл.дробное",value:"d:double"},{label:"Дата",value:"d:date"},{label:"Дата/время",value:"d:datetime"},{label:"Булево",value:"d:boolean"},{label:"Имя типа",value:"d:qname"},{label:"Ссылка",value:"d:noderef"},{label:"Категория",value:"d:category"}];
				//Категории
				this.categoryDialogEl = {"name":{name:"name",label:"Категория",type:"input",value:""}};
				this.categoryColDefs = 	[
		                       	 	{ className: "viewmode-label", key:"name", label:"Категория", formatter: this._formatText, width: 360, maxAutoWidth: 360 },
		                       	 	{ key : "delete", label : "", formatter:this._formatActions, width: 15, maxAutoWidth: 15} 
								];
				this.categoryResponseSchema = { fields : [{key : "name"}] };
				//Атрибуты
				this.attributesDialogEl = {
									"_name": { name: "_name", label: "Имя", type:"input", value: "" },
									"title": { name: "title", label: "Заголовок", type:"input", value: "" },
									"type": { name: "type", label: "Тип", type:"select", options: dTypes, showdefault: false },
									"mandatory": { name: "mandatory", label: "Обязательный", type:"select", options: [{label:"Да",value:"true"},{label:"Нет",value:"false"}], value: "false", showdefault: false },
									"_enabled": { name: "_enabled", label: "Индексировать", type:"select", options: [{label:"Да",value:"true"},{label:"Нет",value:"false"}], value: "false", showdefault: false }
									//"validator": { name: "validator", label: "Валидатор", type:"select", options: [""], showdefault: false },
								};
				this.attributesColumnDefs = [ 
		                            { className: "viewmode-label", key:"_name", label:"Имя", formatter : this._formatText, width : 170, maxAutoWidth : 170 }, 
		                            { className: "viewmode-label", key : "title", label : "Заголовок", formatter : this._formatText, width : 170, maxAutoWidth : 170 }, 
		                            { className: "viewmode-label", key : "type", label : "Тип", dropdownOptions : dTypes, formatter : "dropdown", width : 100, maxAutoWidth : 100 }, 
		                            { className: "viewmode-label", key : "mandatory", label : "Обязательный", formatter : this._formatBoolean, width : 100, maxAutoWidth : 100 },
		                            { className: "viewmode-label", key : "_enabled", label : "Индексировать", formatter : this._formatBoolean, width : 100, maxAutoWidth : 100 },
		                            //{ key : "validator", label : "Валидатор", width : 70, maxAutoWidth : 70, dropdownOptions: [""], formatter:  "dropdown" },
		                            { key : "delete", label : "", formatter : this._formatActions, width : 15, maxAutoWidth : 15} 
		                        ];
				this.attribyteResponseSchema = { fields : [{key : "_id"}, {key : "_name"}, {key : "title"}, {key : "type"}, {key : "mandatory"}, {key : "_enabled"}, {key : "validator"}] };
				//Ассоциации
				this.associationsDialogEl = [
				                    { name: "_name", label: "Имя", type:"input" },
		                            { name: "title", label: "Заголовок", type:"input" },
		                            { name: "class", label: "Тип", type:"select", options: dAssociations, showdefault: false },
		                            { name: "mandatory", label: "Обязательная", type:"select", options: [{label:"Да",value:"true"}, {label:"Нет",value:"false"}], value: "false", showdefault: false },
		                            { name: "many", label: "Множественная", type:"select", options: [{label:"Да",value:"true"}, {label:"Нет",value:"false"}], value: "false", showdefault: false },
		                        ];
				for(j in r) {
					dAssociations.push({label:r[j].title+" - "+r[j].name,value:r[j].name});
				}
				this.associationsColumnDefs = [
				                    { className: "viewmode-label", key:"_name", label:"Имя", formatter: this._formatText, width : 170, maxAutoWidth : 170 },
		                            { className: "viewmode-label", key:"title", label:"Заголовок", formatter: this._formatText, width : 170, maxAutoWidth : 170 },
		                            { className: "viewmode-label", key:"class", label:"Тип", dropdownOptions : dAssociations, formatter: "dropdown", width : 100, maxAutoWidth : 100 },
		                            { className: "viewmode-label", key:"mandatory", label:"Обязательная", formatter: this._formatBoolean, width : 100, maxAutoWidth : 100 }, 
		                            { className: "viewmode-label", key:"many", label:"Множественная", formatter: this._formatBoolean, width : 100, maxAutoWidth : 100 }, 
		                            { key : "delete", label : "", formatter:this._formatActions, width : 15, maxAutoWidth : 15 } 
		                        ];
				this.associationResponseSchema = { fields : [{key : "_name"}, {key : "class"}, {key : "title"}, {key : "mandatory"}, {key : "many"}] };
				
				if (callback && callback.successCallback) {
					if (Alfresco.logger.isDebugEnabled()) Alfresco.logger.debug("Model-editor: calling callback");
					//Dom.get(this.id+"_loading").setAttribute("style", "display:none");
					//Dom.get(this.id+"_props").setAttribute("style", "display:block");
					callback.successCallback.fn.call(callback.successCallback.scope, { successCallback : { fn : this._renderEditor, scope : this } });
				}
			};// onSuccess
			var onFailureAssoc = function ContentControl_populateContent_onFailure(response) {
				
			};// onFailure
			//api/classes/cm_cmobject/subclasses?r=true
			//api/classes/cm_content/subclasses?r=false
			Alfresco.util.Ajax.request({
				url : Alfresco.constants.PROXY_URI + "lecm/api/classes/cm_cmobject/subclasses?r=true",//"api/dictionary",
				method : "GET",
				successCallback : { fn : onSuccessAssoc, scope : this },
				failureCallback : { fn : onFailureAssoc, scope : this }
			});// request
		},
		//Получение контента из repo
		_populateContent : function ContentControl__populateContent(callback) {
			if (this.options.nodeRef !== null && this.options.nodeRef.length > 0) {
				var onSuccess = function ContentControl_populateContent_onSuccess(response) {
					//prepare model
					var responseXML = response.serverResponse.responseXML;
					if(responseXML==null||responseXML.documentElement==null)
						responseXML = parseXML(response.serverResponse.responseText);
					
					this.modelObject = YAHOO.lang.JSON.parse(xml2json(responseXML,""));
					this._initObjects();
					//render form
					if (callback && callback.successCallback) {
						if (Alfresco.logger.isDebugEnabled()) Alfresco.logger.debug("Model-editor: calling callback");
						callback.successCallback.fn.call(callback.successCallback.scope, { successCallback : { fn : this._populateTables, scope : this } } );
					}
				};// onSuccess
				// Failure handler
				var onFailure = function ContentControl_populateContent_onFailure(response) {
					var elText = document.createTextNode("Ошибка получения данных");
					Dom.get(this.id+"_base").appendChild(elText);
					if (Alfresco.logger.isDebugEnabled()) Alfresco.logger.debug("Model-editor: failure");
				};// onFailure
				var nodeRefUrl = this.options.nodeRef.replace("://", "/");
				//Get content node
				Alfresco.util.Ajax.request({
					url : Alfresco.constants.PROXY_URI + "api/node/content/" + nodeRefUrl,
					method : "GET",
					successCallback : { fn : onSuccess, scope : this },
					failureCallback : { fn : onFailure, scope : this }
				});// request
			} else {
				this.modelObject = { model:{} };
				this._initObjects();
				
				//Dom.get(this.id+"_loading").setAttribute("style", "display:none");
				//Dom.get(this.id+"_props").setAttribute("style", "display:block");
				//render form
				if (callback && callback.successCallback) {
					if (Alfresco.logger.isDebugEnabled()) Alfresco.logger.debug("Model-editor: calling callback");
					callback.successCallback.fn.call(callback.successCallback.scope, { successCallback : { fn : this._populateTables, scope : this } } );
				}
			}// if
			
			var onSuccessNS = function ContentControl_populateContent_onSuccess(response) {
				var r = response.json;
				this.namespaces = r;
			};// onSuccess
			var onFailureNS = function ContentControl_populateContent_onFailure(response) {
			};// onFailure
			Alfresco.util.Ajax.request({
				url : Alfresco.constants.PROXY_URI + "namespaces/",
				method : "GET",
				successCallback : { fn : onSuccessNS, scope : this },
				failureCallback : { fn : onFailureNS, scope : this }
			});// request
		},//_populateContent
		//Форматер для чекбоксов в таблице
	    _formatBoolean : function(el, oRecord, oColumn, oData, oDataTable) {
	        var oDT = oDataTable || this;
	    	var bChecked = oData;
	        bChecked = (bChecked==="true") ? " checked=\"checked\"" : "";
	        el.innerHTML = "<input type=\"checkbox\"" + bChecked +
	                " class=\"" + YAHOO.widget.DataTable.CLASS_CHECKBOX + "\" />";
	        
	        YAHOO.util.Event.addListener(el,"change",function(e, oSelf) {
		    	var elTarget = YAHOO.util.Event.getTarget(e);
		        oSelf.fireEvent("valueChangeEvent", {event:e, target:elTarget});
		    },oDT);
	    },//_formatBoolean	    
	    //Форматер для полей ввода в таблице
	    _formatText : function(el, oRecord, oColumn, oData, oDataTable) {
	    	var oDT = oDataTable || this;
	        var value = (Lang.isValue(oData)) ? Lang.escapeHTML(oData.toString()) : "",
	            markup = "<input style=\"width:"+oColumn.width+"px;\" type=\"text\" value=\"" + value + "\" title=\"" + value + "\" />";
	        el.innerHTML = markup;
	        
	        YAHOO.util.Event.addListener(el,"keyup",function(e, oSelf) {
		    	var elTarget = YAHOO.util.Event.getTarget(e);
		        oSelf.fireEvent("valueChangeEvent", {event:e, target:elTarget});
		    },oDT);
	    },//_formatText
		//Форматер для поля с действиями
		_formatActions: function formaterRenderActions(el, oRecord, oColumn, oData, oDataTable) {
			var oDT = oDataTable || this;
			var deleteLink = document.createElement("a");
			//deleteLink.id = Dom.generateId();
			Dom.addClass(deleteLink, "delete");
			deleteLink.innerHTML = "&nbsp;";
			el.appendChild(deleteLink);
		},//_formatActions
		//Обработчик клика по кнопке столбцу удаления
		_deleteRow: function(oArgs) {
		    var target = oArgs.target;
		    var column = this.getColumn(target);
		    if (column.key == 'delete') {
				if (confirm('Вы действительно хотите удалить строку?')) {
					this.deleteRow(target);
					Bubbling.fire("mandatoryControlValueUpdated", this);
				}
		    } else {
		    	//this.onEventShowCellEditor(oArgs);
		    }
		},//_deleteRow
		//Отрисовка редактора
		_renderEditor : function RichTextControl__renderEditor() {
			Alfresco.logger.debug("Model-editor: render editor");
			//categoryDialog
			this.categoryDialogId = this.id+"_categoryDlg";
			Bubbling.on(this.categoryDialogId+"showEditDialog", this.onShowCategoryDialog, this);
			Bubbling.on(this.categoryDialogId+"hideEditDialog", this.onHideCategoryDialog, this);
			this.categoryDialog = new IT.widget.Dialog(this.categoryDialogId,
				{width: "300px", modal:true, fixedcenter: true, constraintoviewport: true, underlay: "shadow", close: true, visible: false, draggable: false,
				 elements: this.categoryDialogEl } );
			this.categoryDialog.render();
			//attributesDialog
			this.attributesDialogId = this.id+"_attributesDlg";
			Bubbling.on(this.attributesDialogId+"showEditDialog", this.onShowAttributesDialog, this);
			Bubbling.on(this.attributesDialogId+"hideEditDialog", this.onHideAttributesDialog, this);
			this.attributesDialog = new IT.widget.Dialog(this.attributesDialogId,
				{width: "300px", modal:true, fixedcenter: true, constraintoviewport: true, underlay: "shadow", close: true, visible: false,
				 draggable: false, elements: this.attributesDialogEl });
			this.attributesDialog.render();
			//associationsDialog
			this.associationsDialogId = this.id+"_associationsDlg";
			Bubbling.on(this.associationsDialogId+"showEditDialog", this.onShowAssociationsDialog, this);
			Bubbling.on(this.associationsDialogId+"hideEditDialog", this.onHideAssociationsDialog, this);
		    this.associationsDialog = new IT.widget.Dialog(this.associationsDialogId,
		    	{width: "300px", modal:true, fixedcenter: true, constraintoviewport: true,
            	underlay: "shadow", close: true, visible: false, draggable: false,
	            elements: this.associationsDialogEl });
		    this.associationsDialog.render();
		    //tablesDialog
			this.tablesDialogId = this.id+"_tablesDlg";
			Bubbling.on(this.tablesDialogId+"showEditDialog", this.onShowTablesDialog, this);
			Bubbling.on(this.tablesDialogId+"hideEditDialog", this.onHideTablesDialog, this);
		    this.tablesDialog = new IT.widget.Dialog(this.tablesDialogId,
		    	{width: "300px", modal:true, fixedcenter: true, constraintoviewport: true,
            	underlay: "shadow", close: true, visible: false, draggable: false,
	            elements: this.tablesDialogEl });
		    this.tablesDialog.render();
			//Описание
			var oSpan = document.createElement("span");
			var input = new IT.widget.Input({ name: "model_description", label: "<b>Описание модели</b>", value: (this.model_description||""), help:"Описание модели"} );
			input.render(oSpan);
			Dom.get(this.id+"_title").appendChild(oSpan);
			//Заголовок
			var oSpan = document.createElement("span");
			var input = new IT.widget.Input({ name: "typeTitle", label: "<b>Описание документа</b>", value: (this.typeTitle||""), help:"Описание документа" } );
			input.render(oSpan);
			Dom.get(this.id+"_title").appendChild(oSpan);
			//Родительский документ
			var oSpan = document.createElement("span");
			var input = new IT.widget.Select({ name: "parentRef", label: "<b>Родительский документ</b>", help:"Родительский документ", options: [{label:"LECM BASE",value:"lecm-document:base"},{label:"LECM EDS BASE",value:"lecm-eds-document:base"}], value: (this.parentRef||"lecm-document:base"), showdefault: false });
			input.render(oSpan);
			Dom.get(this.id+"_title").appendChild(oSpan);
			//Шаблон строки представления
			var oSpan = document.createElement("span");
			var input = new IT.widget.Input({ name: "presentString", label: "<b>Шаблон строки представления</b>", value: (this.presentString||""), help:"Шаблон строки представления" });
			input.render(oSpan);
			Dom.get(this.id+"_title").appendChild(oSpan);
			//АРМ
			var oSpan = document.createElement("span");
			var input = new IT.widget.Input({ name: "armUrl", label: "<b>Страница списка документов</b>", value: (this.armUrl||""), help:"Страница списка документов" });
			input.render(oSpan);
			Dom.get(this.id+"_title").appendChild(oSpan);
			//Автор
			var oSpan = document.createElement("span");
			var input = new IT.widget.Input({ name: "authorProperty", label: "<b>Автор</b>", value: (this.authorProperty||""), help:"Автор" });
			input.render(oSpan);
			Dom.get(this.id+"_title").appendChild(oSpan);
			//Рег номера
			var oSpan = document.createElement("span");
			var input = new IT.widget.Input({ name: "regNumbersProperties", label: "<b>Рег номера</b>", value: (this.regNumbersProperties||""), help:"Рег номера" });
			input.render(oSpan);
			Dom.get(this.id+"_title").appendChild(oSpan);
			
			//Шаблон строки представления для списка
			//var oSpan = document.createElement("span");
			//var input = new IT.widget.Input({ name: "listPresentString", label: "Шаблон строки представления для списка", value: (this.listPresentString||"") });
			//input.render(oSpan);
			//Dom.get(this.id+"_title").appendChild(oSpan);
			
			//Рейтингуемый
			var oSpan = document.createElement("span");
			var input = new IT.widget.Select({ name: "rating", label: "<b>Рейтингуемый</b>", help:"Рейтингуемый", options: [{label:"Да",value:"true"},{label:"Нет",value:"false"}], value: (this.rating||"false"), showdefault: false });
			input.render(oSpan);
			Dom.get(this.id+"_title").appendChild(oSpan);
			//ЮЗД
			var oSpan = document.createElement("span");
			var input = new IT.widget.Select({ name: "signed", label: "<b>Участвует в ЮЗД</b>", help:"Участвует в ЮЗД", options: [{label:"Да",value:"true"},{label:"Нет",value:"false"}], value: (this.signed||"false"), showdefault: false });
			input.render(oSpan);
			Dom.get(this.id+"_title").appendChild(oSpan);
			//Категории вложений
			this.widgets.categoriesDataSource = new YAHOO.util.DataSource(this.categoryArray,{responseSchema:this.categoryResponseSchema});
			this.widgets.categoriesDataTable = new YAHOO.widget.DataTable(this.id+"_categories", this.categoryColDefs, this.widgets.categoriesDataSource);
			this.widgets.categoriesDataTable.subscribe('cellClickEvent',this._deleteRow);
			this.widgets.categoriesDataTable.on("valueChangeEvent", function(args) {
				var e = args.event, t = args.target, r = this.getRecord(t), c = this.getColumn(this.getCellIndex(t.parentNode));
				r.setData(c.key, t.value);
			});
			var categoryAddSpan = document.createElement("span");
			if (Button) {
                var oYUIButton = new Button({ label: "Добавить", type: "button" });
                oYUIButton.appendTo(categoryAddSpan);
                oYUIButton.set("onclick", { fn: function(evt, obj) { Bubbling.fire(obj.categoryDialogId+"showEditDialog"); }, obj: this, scope: this });
			}
            Dom.get(this.id+"_categories").appendChild(categoryAddSpan);
			//Атрибуты
			this.widgets.attributesDataSource = new YAHOO.util.DataSource(this.attributesArray,{responseSchema:this.attribyteResponseSchema});
			this.widgets.attributesDataTable = new YAHOO.widget.DataTable(this.id+"_attributes", this.attributesColumnDefs, this.widgets.attributesDataSource);
			this.widgets.attributesDataTable.subscribe('cellClickEvent',this._deleteRow);
			this.widgets.attributesDataTable.on("valueChangeEvent", function(args) {
				var e = args.event, t = args.target, r = this.getRecord(t), c = this.getColumn(this.getCellIndex(t.parentNode));
				if(t.type === "checkbox") { r.setData(c.key, ""+t.checked); } 
				else { r.setData(c.key, ""+t.value); }
			});
			this.widgets.attributesDataTable.on("dropdownChangeEvent", function(args) {
				var e = args.event, t = args.target, r = this.getRecord(t), c = this.getColumn(this.getCellIndex(t.parentNode));
				r.setData(c.key, t.value);
			});
			var attributesAddSpan = document.createElement("span");
			if (Button) {
                var oYUIButton = new Button({ label: "Добавить", type: "button" });
                oYUIButton.appendTo(attributesAddSpan);
                oYUIButton.set("onclick", { fn: function(evt, obj) { Bubbling.fire(obj.attributesDialogId+"showEditDialog"); }, obj: this, scope: this });
			}
            Dom.get(this.id+"_attributes").appendChild(attributesAddSpan);
			//Ассоциации
			this.widgets.associationsDataSource = new YAHOO.util.DataSource(this.associationsArray,{responseSchema:this.associationResponseSchema});
			this.widgets.associationsDataTable = new YAHOO.widget.DataTable(this.id+"_associations", this.associationsColumnDefs, this.widgets.associationsDataSource);
			this.widgets.associationsDataTable.subscribe('cellClickEvent',this._deleteRow);
			this.widgets.associationsDataTable.on("valueChangeEvent", function(args) {
				var e = args.event, t = args.target, r = this.getRecord(t), c = this.getColumn(this.getCellIndex(t.parentNode));
				if(t.type === "checkbox") { r.setData(c.key, ""+t.checked); } 
				else { r.setData(c.key, ""+t.value); }
			});
            var associationsAddSpan = document.createElement("span");
			if (Button) {
                oYUIButton = new Button({ label: "Добавить", type: "button" });
                oYUIButton.appendTo(associationsAddSpan);
                oYUIButton.set("onclick", { fn: function(evt, obj) { Bubbling.fire(obj.associationsDialogId+"showEditDialog"); }, obj: this, scope: this });
			}
            Dom.get(this.id+"_associations").appendChild(associationsAddSpan);
            //Таблицы
			this.widgets.tablesDataSource = new YAHOO.util.DataSource(this.tablesArray,{responseSchema:this.tablesResponseSchema});
			this.widgets.tablesDataTable = new YAHOO.widget.DataTable(this.id+"_tables", this.tablesColumnDefs, this.widgets.tablesDataSource);
			this.widgets.tablesDataTable.subscribe('cellClickEvent',this._deleteRow);
			this.widgets.tablesDataTable.on("dropdownChangeEvent", function(args) {
				var e = args.event, t = args.target, r = this.getRecord(t), c = this.getColumn(this.getCellIndex(t.parentNode));
				r.setData(c.key, t.value);
			});
            var tablesAddSpan = document.createElement("span");
			if (Button) {
                oYUIButton = new Button({ label: "Добавить", type: "button" });
                oYUIButton.appendTo(tablesAddSpan);
                oYUIButton.set("onclick", { fn: function(evt, obj) { Bubbling.fire(obj.tablesDialogId+"showEditDialog"); }, obj: this, scope: this });
			}
            Dom.get(this.id+"_tables").appendChild(tablesAddSpan);
//            ////////////////////////////////////// Debug /////////////////////////////////////////////////////////
//			this.widgets.button = Alfresco.util.createYUIButton(this,null,
//				function(n, v) {
//					var name = n, value = v;
//					return function() {
//						Dom.get(this.id + "_console").innerHTML = "";
//						//Dom.get(this.id + "_console").innerHTML += Dom.get(this.id).value+"<br/>";
//						Dom.get(this.id + "_console").innerHTML += YAHOO.lang.JSON.stringify(this.widgets.categoriesDataTable.getRecordSet().getRecords())+"<br/>";
//						Dom.get(this.id + "_console").innerHTML += YAHOO.lang.JSON.stringify(this.widgets.attributesDataTable.getRecordSet().getRecords())+"<br/>";
//						Dom.get(this.id + "_console").innerHTML += YAHOO.lang.JSON.stringify(this.widgets.associationsDataTable.getRecordSet().getRecords())+"<br/>";
//					};
//				}("name", "value"), {},
//			this.id + "_btn");
			this.initSuccess = true;
		}//_renderEditor
	});
	//Служебные функции
	//TODO: Вынести в отдельный файл
	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	function parseXML( data , xml , tmp ) {
		if ( window.DOMParser ) { // Standard
			tmp = new DOMParser();
			xml = tmp.parseFromString( data , "text/xml" );
		} else { // IE
			xml = new ActiveXObject( "Microsoft.XMLDOM" );
			xml.async = "false";
			xml.loadXML( data );
		}
		tmp = xml.documentElement;
		if ( ! tmp || ! tmp.nodeName || tmp.nodeName === "parsererror" ) {
			jQuery.error( "Invalid XML: " + data );
		}
		return xml;
	};
	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	function json2xml(o, tab) {
		var toXml = function(v, name, ind) {
			var xml = "";
			if (v instanceof Array) {
				for ( var i = 0, n = v.length; i < n; i++)
					xml += ind + toXml(v[i], name, ind + "\t") + "\n";
			} else if (typeof (v) == "object") {
				var hasChild = false;
				xml += ind + "<" + name;
				for ( var m in v) {
					if (m.charAt(0) == "_")
						xml += " " + m.substr(1) + "=\"" + v[m].toString() + "\"";
					else
						hasChild = true;
				}
				xml += hasChild ? ">" : "/>";
				if (hasChild) {
					for ( var m in v) {
						if (m == "#text")
							xml += v[m];
						else if (m == "#cdata")
							xml += "<![CDATA[" + v[m] + "]]>";
						else if (m.charAt(0) != "_")
							xml += toXml(v[m], m, ind + "\t");
					}
					xml += (xml.charAt(xml.length - 1) == "\n" ? ind : "") + "</"
							+ name + ">";
				}
			} else {
				xml += ind + "<" + name + ">" + v.toString() + "</" + name + ">";
			}
			YAHOO.log(xml);
			return xml;
		}, xml = "";
		for ( var m in o)
			xml += toXml(o[m], m, "");
		return tab ? xml.replace(/\t/g, tab) : xml.replace(/\t|\n/g, "");
	};
	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	function xml2json(xml, tab) {
		var X = {
			toObj : function(xml) {
				var o = {};
				if (xml.nodeType == 1) { // element node ..
					if (xml.attributes.length) // element with attributes ..
						for ( var i = 0; i < xml.attributes.length; i++)
							o["_" + xml.attributes[i].nodeName] = (xml.attributes[i].nodeValue || "")
									.toString();
					if (xml.firstChild) { // element has child nodes ..
						var textChild = 0, cdataChild = 0, hasElementChild = false;
						for ( var n = xml.firstChild; n; n = n.nextSibling) {
							if (n.nodeType == 1)
								hasElementChild = true;
							else if (n.nodeType == 3
									&& n.nodeValue.match(/[^ \f\n\r\t\v]/))
								textChild++; // non-whitespace text
							else if (n.nodeType == 4)
								cdataChild++; // cdata section node
						}
						if (hasElementChild) {
							if (textChild < 2 && cdataChild < 2) { // structured
																	// element with
																	// evtl. a
																	// single text
																	// or/and cdata
																	// node ..
								X.removeWhite(xml);
								for ( var n = xml.firstChild; n; n = n.nextSibling) {
									if (n.nodeType == 3) // text node
										o["#text"] = X.escape(n.nodeValue);
									else if (n.nodeType == 4) // cdata node
										o["#cdata"] = X.escape(n.nodeValue);
									else if (n.nodeType == 8) {// 
										// comment node
									} else if (o[n.nodeName]) { // multiple
																// occurence of
																// element ..
										if (o[n.nodeName] instanceof Array)
											o[n.nodeName][o[n.nodeName].length] = X
													.toObj(n);
										else
											o[n.nodeName] = [ o[n.nodeName],
													X.toObj(n) ];
									} else
										// first occurence of element..
										o[n.nodeName] = X.toObj(n);
								}
							} else { // mixed content
								if (!xml.attributes.length)
									o = X.escape(X.innerXml(xml));
								else
									o["#text"] = X.escape(X.innerXml(xml));
							}
						} else if (textChild) { // pure text
							if (!xml.attributes.length)
								o = X.escape(X.innerXml(xml));
							else
								o["#text"] = X.escape(X.innerXml(xml));
						} else if (cdataChild) { // cdata
							if (cdataChild > 1)
								o = X.escape(X.innerXml(xml));
							else
								for ( var n = xml.firstChild; n; n = n.nextSibling)
									o["#cdata"] = X.escape(n.nodeValue);
						}
					}
					if (!xml.attributes.length && !xml.firstChild)
						o = null;
				} else if (xml.nodeType == 9) { // document.node
					o = X.toObj(xml.documentElement);
				} else {
					// alert("unhandled node type: " + xml.nodeType);
				}
				return o;
			},
			toJson : function(o, name, ind) {
				var json = name ? ("\"" + name + "\"") : "";
				if (o instanceof Array) {
					for ( var i = 0, n = o.length; i < n; i++)
						o[i] = X.toJson(o[i], "", ind + "\t");
					json += (name ? ":[" : "[")
							+ (o.length > 1 ? ("\n" + ind + "\t"
									+ o.join(",\n" + ind + "\t") + "\n" + ind) : o
									.join("")) + "]";
				} else if (o == null)
					json += (name && ":") + "null";
				else if (typeof (o) == "object") {
					var arr = [];
					for ( var m in o)
						arr[arr.length] = X.toJson(o[m], m, ind + "\t");
					json += (name ? ":{" : "{")
							+ (arr.length > 1 ? ("\n" + ind + "\t"
									+ arr.join(",\n" + ind + "\t") + "\n" + ind)
									: arr.join("")) + "}";
				} else if (typeof (o) == "string")
					json += (name && ":") + "\"" + o.toString() + "\"";
				else
					json += (name && ":") + o.toString();
				return json;
			},
			innerXml : function(node) {
				var s = ""
				if ("innerHTML" in node)
					s = node.innerHTML;
				else {
					var asXml = function(n) {
						var s = "";
						if (n.nodeType == 1) {
							s += "<" + n.nodeName;
							for ( var i = 0; i < n.attributes.length; i++)
								s += " "
										+ n.attributes[i].nodeName
										+ "=\""
										+ (n.attributes[i].nodeValue || "")
												.toString() + "\"";
							if (n.firstChild) {
								s += ">";
								for ( var c = n.firstChild; c; c = c.nextSibling)
									s += asXml(c);
								s += "</" + n.nodeName + ">";
							} else
								s += "/>";
						} else if (n.nodeType == 3)
							s += n.nodeValue;
						else if (n.nodeType == 4)
							s += "<![CDATA[" + n.nodeValue + "]]>";
						return s;
					};
					for ( var c = node.firstChild; c; c = c.nextSibling)
						s += asXml(c);
				}
				return s;
			},
			escape : function(txt) {
				return txt.replace(/[\\]/g, "\\\\").replace(/[\"]/g, '\\"')
						.replace(/[\n]/g, '\\n').replace(/[\r]/g, '\\r');
			},
			removeWhite : function(e) {
				e.normalize();
				for ( var n = e.firstChild; n;) {
					if (n.nodeType == 3) { // text node
						if (!n.nodeValue.match(/[^ \f\n\r\t\v]/)) { // pure
																	// whitespace
																	// text node
							var nxt = n.nextSibling;
							e.removeChild(n);
							n = nxt;
						} else
							n = n.nextSibling;
					} else if (n.nodeType == 1) { // element node
						X.removeWhite(n);
						n = n.nextSibling;
					} else
						// any other node
						n = n.nextSibling;
				}
				return e;
			}
		};
		if (xml.nodeType == 9) // document node
			xml = xml.documentElement;
		var json = X.toJson(X.toObj(X.removeWhite(xml)), xml.nodeName, "\t");
		return "{\n" + tab
				+ (tab ? json.replace(/\t/g, tab) : json.replace(/\t|\n/g, ""))
				+ "\n}";
	};
	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////
})();