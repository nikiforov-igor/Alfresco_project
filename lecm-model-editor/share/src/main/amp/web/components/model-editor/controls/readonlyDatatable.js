/* global YAHOO, Alfresco, IT */

if (typeof LogicECM == 'undefined' || !LogicECM) {
	LogicECM = {};
}

LogicECM.module = LogicECM.module || {};
LogicECM.module.ModelEditor = LogicECM.module.ModelEditor || {};

(function () {
	LogicECM.module.ModelEditor.RODatatableControl = function (name, containerId, options, messages) {
		LogicECM.module.ModelEditor.RODatatableControl.superclass.constructor.call(this, name, containerId);
		this.setOptions(options);
		this.setMessages(messages);
		return this;
	};

	YAHOO.extend(LogicECM.module.ModelEditor.RODatatableControl, Alfresco.component.Base, {

		options: {
			columnDefinitions: null,
//			dialogElements: null,
			responseSchema: null,
			url: null,
			data: null
		},
		
		formatText: function (el, oRecord, oColumn, oData, oDataTable) {
			if(oRecord.getData("tProps")!=null){
				el.id=oRecord.getId();
				var div11 = el.appendChild(document.createElement('div'));
				div11.innerHTML = "<b>Атрибуты</b>"
				var div12 = el.appendChild(document.createElement('div'));
				div12.id = oRecord.getId()+'props';
				var div21 = el.appendChild(document.createElement('div'));
				div21.innerHTML = "<b>Ассоциации</b>"
				var div22 = el.appendChild(document.createElement('div'));
				div22.id = oRecord.getId()+'assocs';
				var colDefProps = [
					{className:'viewmode-label',key:'_name',label:'Имя',width:170,maxAutoWidth:170},
					{className:'viewmode-label',key:'title',label:'Заголовок',width:170,maxAutoWidth:170},
					{className:'viewmode-label',key:'type',label:'По умолчанию',width:170,maxAutoWidth:170},
					{className:'viewmode-label',key:'default',label:'Тип',width:100,maxAutoWidth:100},
					{className:'viewmode-label',key:'mandatory',label:'Обязательный',width:100,maxAutoWidth:100},
					{className:'viewmode-label',key:'_enabled',label:'Индексировать',width:100,maxAutoWidth:100},
					{className:'viewmode-label',key:'tokenised',label:'Токенизация',width:80,maxAutoWidth:80}
				],
				DSProps = new YAHOO.util.DataSource(oRecord.getData("tProps"), {
					responseSchema:  {fields: [{key: '_name'},{key: 'title'},{key: 'type'},{key: 'default'},{key: 'mandatory'},{key: '_enabled'},{key: 'tokenised'}]}
				}),
				colDefAssocs = [
					{className:'viewmode-label',key:'_name',label:'Имя',width:170,maxAutoWidth:170},
					{className:'viewmode-label',key:'title',label:'Заголовок',width:170,maxAutoWidth:170},
					{className:'viewmode-label',key:'class',label:'Тип',width:291,maxAutoWidth:291},
					{className:'viewmode-label',key:'mandatory',label:'Обязательный',width:100,maxAutoWidth:100},
					{className:'viewmode-label',key:'many',label:'Множественная',width:203,maxAutoWidth:203}
				],
				DSPAssocs = new YAHOO.util.DataSource(oRecord.getData("tAssocs"), {
					responseSchema:  {fields: [{key: '_name'},{key: 'class'},{key: 'title'},{key: 'mandatory'},{key: 'many'}]}
				});
				datatable1 = new YAHOO.widget.DataTable(div12, colDefProps, DSProps);
				datatable2 = new YAHOO.widget.DataTable(div22, colDefAssocs, DSPAssocs);
			} else {
				el.id=oRecord.getId();
				el.innerHTML = oData;
			}
		},
		
		formatBoolean: function (el, oRecord, oColumn, oData, oDataTable) {
			if(oData) {
				var bChecked = (oData===true) ? ' checked="checked"' : '';
				el.innerHTML = '<input disabled="true" type="checkbox"' + bChecked + ' class="' + YAHOO.widget.DataTable.CLASS_CHECKBOX + '"/>';
			} else {
				el.innerHTML = '';
			}
		},
		
		formatDropdown: function (el, oRecord, oColumn, oData, oDataTable) {
			if(oData) {
				var select = new IT.widget.Select({ name: "parentRef", options: oColumn.dropdownOptions, value: oData, showdefault: true, disabled: true });
				select.render(el);
			} else {
				el.innerHTML = '';
			}
			
		},
		
		formatActions: function (el, oRecord, oColumn, oData, oDataTable) {			
			if (oRecord.getData("props")!=null || oRecord.getData("assocs")!=null || oRecord.getData("table")!=null) {
				el.innerHTML = "";

				if (oRecord.getData("expanded")) {
					var collapseLink = document.createElement("a");
					Dom.addClass(collapseLink, "collapse");
					collapseLink.innerHTML = "&nbsp;";
					el.appendChild(collapseLink);
				} else {
					var expandLink = document.createElement("a");
					Dom.addClass(expandLink, "expand");
					expandLink.innerHTML = "&nbsp;";
					el.appendChild(expandLink);
				}
			}
		},
		
		expandRow: function(oArgs) {
			var target = oArgs.target;
			var column = this.getColumn(target);
			var oRecord = this.getRecord(target);
			if (column.key == 'expand') {
				var props = oRecord.getData("props");
				if (props != null) {
					var expanded = oRecord.getData("expanded") != null && oRecord.getData("expanded");
					if (!expanded) {
						var typeRows = [];
						for (var i = 0; i < props.length; i++) {
							typeRows.push({
								_name: props[i]._name,
								title: props[i].title,
								'default': props[i]['default'],
								type: props[i].type,
								mandatory: props[i].mandatory,
								_enabled: props[i]._enabled,
								tokenised: props[i].tokenised
							});

						}
						this.addRows(typeRows, this.getTrIndex(oArgs.target) + 1);
					} else {
						var recordSet = this.getRecordSet();
						var findedRows = [];
						for (i = 0; i < recordSet.getLength(); i++) {
							var cN = recordSet.getRecord(i).getData("_name");
							var cT = recordSet.getRecord(i).getData("type");
							var pN = oRecord.getData("_name")
							if (cN.substr(0,cN.indexOf(':')) === pN.substr(0,pN.indexOf(':')) && cT) {
								findedRows.push(recordSet.getRecord(i));
							}
						}

						for (i = 0; i < findedRows.length; i++) {
							this.deleteRows(findedRows[i]);
						}
					}
					var itemData = oRecord.getData();
					itemData.expanded = !expanded;
					this.updateRow(oRecord, itemData);
				}
				
				var assocs = oRecord.getData("assocs");
				if (assocs != null) {
					var expanded = oRecord.getData("expanded") != null && oRecord.getData("expanded");
					if (!expanded) {
						var typeRows = [];
						for (var i = 0; i < assocs.length; i++) {
							typeRows.push({
								_name: assocs[i]._name,
								title: assocs[i].title,
								'class': assocs[i]['class'],
								mandatory: assocs[i].mandatory,
								many: assocs[i].many
							});

						}
						this.addRows(typeRows, this.getTrIndex(oArgs.target) + 1);
					} else {
						var recordSet = this.getRecordSet();
						var findedRows = [];
						for (i = 0; i < recordSet.getLength(); i++) {
							var cN = recordSet.getRecord(i).getData("_name");
							var cT = recordSet.getRecord(i).getData("class");
							var pN = oRecord.getData("_name")
							if (cN.substr(0,cN.indexOf(':')) === pN.substr(0,pN.indexOf(':')) && cT) {
								findedRows.push(recordSet.getRecord(i));
							}
						}

						for (i = 0; i < findedRows.length; i++) {
							this.deleteRows(findedRows[i]);
						}
					}
					var itemData = oRecord.getData();
					itemData.expanded = !expanded;
					this.updateRow(oRecord, itemData);
				}
				
				var table = oRecord.getData("table");
				if (table != null) {
					var expanded = oRecord.getData("expanded") != null && oRecord.getData("expanded");
					if (!expanded) {
						var typeRows = [];
						typeRows.push({
							name: table.name,
							tProps: table.props,
							tAssocs: table.assocs
						});
						this.addRows(typeRows, this.getTrIndex(oArgs.target) + 1);
					} else {
						var recordSet = this.getRecordSet();
						var findedRows = [];
						for (i = 0; i < recordSet.getLength(); i++) {
							var cN = recordSet.getRecord(i).getData("name");
							var cT = recordSet.getRecord(i).getData("table");
							var pN = oRecord.getData("name")
							if (cN=== pN && cT==null) {
								findedRows.push(recordSet.getRecord(i));
							}
						}

						for (i = 0; i < findedRows.length; i++) {
							this.deleteRows(findedRows[i]);
						}
					}
					var itemData = oRecord.getData();
					itemData.expanded = !expanded;
					this.updateRow(oRecord, itemData);
				}
			}
		},

		onReady: function () {
			Alfresco.util.Ajax.request({
				url: this.options.url,
				successCallback: {
					scope: this,
					fn: function (successResponse) {
						this.options.data = successResponse.json.data;
						this.widgets.datasource = new YAHOO.util.DataSource(this.options.data, {
							responseSchema: this.options.responseSchema
						});

						this.widgets.datatable = new YAHOO.widget.DataTable(this.id + '-datatable', this.options.columnDefinitions, this.widgets.datasource);
						this.widgets.datatable.subscribe('cellClickEvent', this.expandRow);
					}
				},
				failureMessage: this.msg('Не удалось получить данные')
			});
		}
	}, true);
})();
