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
			responseSchema: null,
			url: null,
			dTypes: null,
			dTokenised: null,
			associations: null,
			data: null
		},
		
		formatText: function (el, oRecord, oColumn, oData, oDataTable) {
			el.innerHTML = '';
			var expanded = oRecord.getData("expanded") != null && oRecord.getData("expanded");
			if (!expanded) {
				el.id=oRecord.getId()
				var div = el.appendChild(document.createElement('div'));
            	div.style.marginBottom = "3px";
            	div.innerHTML =  (oRecord.getData("aspectTitle")?oRecord.getData("aspectTitle")+' - ':'')+(oData?oData:'');
			} else {
				el.id=oRecord.getId();
				var div = el.appendChild(document.createElement('div'));
            	div.style.marginBottom = "3px";
				div.innerHTML =  (oRecord.getData("aspectTitle")?oRecord.getData("aspectTitle")+' - ':'')+(oData?oData:'');
				var type = oRecord.getData("type");
				if(type==null) type = oRecord.getData("aspect");
				if(type==null) type = oRecord.getData("table");
				if(type&&type.props&&type.props.length>0) {
					var div11 = el.appendChild(document.createElement('div'));
					div11.innerHTML = "<b>Атрибуты</b>"
					var div12 = el.appendChild(document.createElement('div'));
					div12.id = oRecord.getId()+'props';
					var colDefProps = [
						{className:'viewmode-label',key:'_name',label:'Имя',width:158,maxAutoWidth:158},
						{className:'viewmode-label',key:'title',label:'Заголовок',width:170,maxAutoWidth:170},
						{className:'viewmode-label',key:'default',label:'По умолчанию',width:170,maxAutoWidth:170},
						{className:'viewmode-label',key:'type',label:'Тип',width:100,maxAutoWidth:100,dropdownOptions:this.configs.dTypes,formatter:function (el, oRecord, oColumn, oData, oDataTable) {
							var select = new IT.widget.Select({ name: "parentRef", options: oColumn.dropdownOptions, value: oData, showdefault: true, disabled: true });
							select.render(el);
						}},
						{className:'viewmode-label',key:'mandatory',label:'Обязательный',width:100,maxAutoWidth:100,formatter:function (el, oRecord, oColumn, oData, oDataTable) {
							var bChecked = (oData===true) ? ' checked="checked"' : '';
							el.innerHTML = '<input disabled="true" type="checkbox"' + bChecked + ' class="' + YAHOO.widget.DataTable.CLASS_CHECKBOX + '"/>';
						}},
						{className:'viewmode-label',key:'_enabled',label:'Индексировать',width:100,maxAutoWidth:100,formatter:function (el, oRecord, oColumn, oData, oDataTable) {
							var bChecked = (oData===true) ? ' checked="checked"' : '';
							el.innerHTML = '<input disabled="true" type="checkbox"' + bChecked + ' class="' + YAHOO.widget.DataTable.CLASS_CHECKBOX + '"/>';
						}},
						{className:'viewmode-label',key:'tokenised',label:'Токенизация',width:80,maxAutoWidth:80,dropdownOptions:this.configs.dTokenised,formatter:function (el, oRecord, oColumn, oData, oDataTable) {
							var select = new IT.widget.Select({ name: "parentRef", options: oColumn.dropdownOptions, value: oData, showdefault: true, disabled: true });
							select.render(el);
						}}
					],
					DSProps = new YAHOO.util.DataSource(type.props, {
						responseSchema:  {fields: [{key: '_name'},{key: 'title'},{key: 'type'},{key: 'default'},{key: 'mandatory'},{key: '_enabled'},{key: 'tokenised'}]}
					});
					datatable1 = new YAHOO.widget.DataTable(div12, colDefProps, DSProps);
				}
				if(type&&type.assocs&&type.assocs.length>0) {
					var div21 = el.appendChild(document.createElement('div'));
					div21.innerHTML = "<b>Ассоциации</b>"
					var div22 = el.appendChild(document.createElement('div'));
					div22.id = oRecord.getId()+'assocs';
					var colDefAssocs = [
						{className:'viewmode-label',key:'_name',label:'Имя',width:158,maxAutoWidth:158},
						{className:'viewmode-label',key:'title',label:'Заголовок',width:170,maxAutoWidth:170},
						{className:'viewmode-label',key:'class',label:'Тип',width:291,maxAutoWidth:291,dropdownOptions:this.configs.associations,formatter:function (el, oRecord, oColumn, oData, oDataTable) {
							var select = new IT.widget.Select({ name: "parentRef", options: oColumn.dropdownOptions, value: oData, showdefault: true, disabled: true });
							select.render(el);
						}},
						{className:'viewmode-label',key:'mandatory',label:'Обязательный',width:100,maxAutoWidth:100,formatter:function (el, oRecord, oColumn, oData, oDataTable) {
							var bChecked = (oData===true) ? ' checked="checked"' : '';
							el.innerHTML = '<input disabled="true" type="checkbox"' + bChecked + ' class="' + YAHOO.widget.DataTable.CLASS_CHECKBOX + '"/>';
						}},
						{className:'viewmode-label',key:'many',label:'Множественная',width:203,maxAutoWidth:203,formatter:function (el, oRecord, oColumn, oData, oDataTable) {
							var bChecked = (oData===true) ? ' checked="checked"' : '';
							el.innerHTML = '<input disabled="true" type="checkbox"' + bChecked + ' class="' + YAHOO.widget.DataTable.CLASS_CHECKBOX + '"/>';
						}}
					],
					DSPAssocs = new YAHOO.util.DataSource(type.assocs, {
						responseSchema:  {fields: [{key: '_name'},{key: 'class'},{key: 'title'},{key: 'mandatory'},{key: 'many'}]}
					});
					datatable2 = new YAHOO.widget.DataTable(div22, colDefAssocs, DSPAssocs);
				}
			}
		},
		
		formatActions: function (el, oRecord, oColumn, oData, oDataTable) {
			Dom.setStyle(el.parentElement, 'vertical-align','top');
			if(oColumn.key==='expand') {
				if (oRecord.getData("type")!=null || oRecord.getData("props")!=null || oRecord.getData("assocs")!=null || oRecord.getData("table")!=null || (oRecord.getData("aspect")!=null&&(oRecord.getData("aspect").props.length>0||oRecord.getData("aspect").assocs.length>0))) {
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
			}
		},
		
		expandRow: function(oArgs) {
			var target = oArgs.target;
			var column = this.getColumn(target);
			var oRecord = this.getRecord(target);
			if (column.key == 'expand') {
				if(target.firstChild&&target.firstChild.firstChild) {
					var expanded = oRecord.getData("expanded") != null && oRecord.getData("expanded");
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

						this.widgets.datatable = new YAHOO.widget.DataTable(this.id + '-datatable', this.options.columnDefinitions, this.widgets.datasource,{associations:this.options.associations,dTypes:this.options.dTypes,dTokenised:this.options.dTokenised});
						this.widgets.datatable.subscribe('cellClickEvent', this.expandRow);
					}
				},
				failureMessage: this.msg('Не удалось получить данные')
			});
		}
	}, true);
})();
