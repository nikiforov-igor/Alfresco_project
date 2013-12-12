/**
 * LogicECM root namespace.
 *
 * @namespace LogicECM
 */
// Ensure LogicECM root object exists
if (typeof LogicECM == "undefined" || !LogicECM) {
	var LogicECM = {};
}

/**
 * LogicECM top-level module namespace.
 *
 * @namespace LogicECM
 * @class LogicECM.module
 */
LogicECM.module = LogicECM.module || {};

LogicECM.module.ModelEditor = LogicECM.module.ModelEditor || {};

(function () {
	var Dom = YAHOO.util.Dom;

	LogicECM.module.ModelEditor.ModelList = function (fieldHtmlId) {
		LogicECM.module.ModelEditor.ModelList.superclass.constructor.call(this, "LogicECM.module.ModelEditor.ModelList", fieldHtmlId, ["button", "dom", "datasource", "datatable", "paginator", "event", "element"]);
		this.widgets = {};
		this.currentFilter = {};
		this.tagId = {
			id: 0,
			tags: {}
		};

		return this;
	};

	YAHOO.extend(LogicECM.module.ModelEditor.ModelList, Alfresco.component.Base, {
		options: {
			siteId: "",
			containerId: "blog",
			initialFilter: {},
			pageSize: 10,
			simpleView: false,
			maxContentLength: 512
		},

		currentFilter: null,
		widgets: null,
		modules: null,
		tagId: null,
		busy: false,
		showPublishingActions: false,
		recordOffset: 0,
		totalRecords: 0,

		onReady: function () {
			var uriDocListList = YAHOO.lang.substitute(Alfresco.constants.URL_SERVICECONTEXT + "components/documentlibrary/data/doclist/all/node/alfresco/company/home/?libraryRoot=/app:company_home/app:dictionary/app:models",
				{
					site: this.options.siteId,
					container: this.options.containerId
				});
			this.widgets.dataSource = new YAHOO.util.DataSource(uriDocListList,
				{
					responseType: YAHOO.util.DataSource.TYPE_JSON,
					connXhrMode: "queueRequests",
					responseSchema: {
						resultsList: "items",
						metaFields: {
							recordOffset: "startIndex",
							totalRecords: "total",
							metadata: "metadata"
						}
					}
				});

			// DataTable column defintions
			var columnDefinitions = [
				{key: "displayName", label: "Модель документа", sortable: false, formatter: this._formatActions, width: 250, maxAutoWidth: 250},
				{key: "active", label: "Активна", sortable: false, formatter: this._formatActive, width: 100, maxAutoWidth: 100},
				{key: "delete", label: "", sortable: false, formatter: this._formatDelete, width: 15, maxAutoWidth: 15},
				{key: "edit-model", label: "", sortable: false, formatter: this._formatEditModel, width: 15, maxAutoWidth: 15},
				{key: "edit-form", label: "", sortable: false, formatter: this._formatEditForm, width: 15, maxAutoWidth: 15}
			];//, {key: "nodeRef", label: "Ссылка", sortable: false}];

			// DataTable definition
			this.widgets.dataTable = new YAHOO.widget.DataTable(this.id + "-body", columnDefinitions, this.widgets.dataSource, {
				//initialLoad: false,
				//dynamicData: true,
			});
			this.widgets.dataTable.subscribe('cellClickEvent', this._deleteRow);
			this.widgets.dataTable.subscribe("dataReturnEvent", function (oArgs) {
				this.destination = oArgs.response.meta.metadata.parent.nodeRef;
			}, this);

			this.widgets.simpleView = Alfresco.util.createYUIButton(this, "button", function () {
				var destination = this.widgets.dataTable.destination;
				var url = Alfresco.constants.URL_PAGECONTEXT + "doc-model-create?formId=create-model&redirect=" + Alfresco.constants.URL_PAGECONTEXT + "doc-model-list&destination=" + destination + "&itemId=cm:dictionaryModel&mimeType=text/xml";
				window.location = url;
			}, {label: "Создать"});

		},

		_formatActions: function formaterRenderActions(el, oRecord, oColumn, oData, oDataTable) {
			var oDT = oDataTable || this;
			var editLink = document.createElement("a");
			editLink.innerHTML = (oRecord.getData("node").properties["cm:modelDescription"] || oData);
			editLink.href = Alfresco.constants.URL_PAGECONTEXT + "doc-model-edit?formId=edit-model&nodeRef=" + oRecord.getData("nodeRef") + "&redirect=" + Alfresco.constants.URL_PAGECONTEXT + "doc-model-list";
			el.appendChild(editLink);
		},

		_formatActive: function formaterRenderActive(el, oRecord, oColumn, oData, oDataTable) {
			var oDT = oDataTable || this;
			var activeElement = document.createElement("span");
			activeElement.innerHTML = (oRecord.getData("node").properties["cm:modelActive"] ? "Да" : "Нет");
			el.appendChild(activeElement);
		},

		_formatDelete: function formaterRenderActive(el, oRecord, oColumn, oData, oDataTable) {
			var oDT = oDataTable || this;
			//if(!oRecord.getData("node").properties["cm:modelActive"]) {
			var deleteLink = document.createElement("a");
			Dom.addClass(deleteLink, "delete");
			deleteLink.innerHTML = "&nbsp;";
			el.appendChild(deleteLink);
			//}
		},

		_formatEditModel: function (el, oRecord, oColumn, oData, oDataTable) {
			var editModelLink = document.createElement("a");
			Dom.addClass(editModelLink, "edit-model");
			editModelLink.innerHTML = "&nbsp;";
			editModelLink.href = Alfresco.constants.URL_PAGECONTEXT + "doc-model-edit?formId=edit-model&nodeRef=" + oRecord.getData("nodeRef") + "&redirect=" + Alfresco.constants.URL_PAGECONTEXT + "doc-model-list";
			el.appendChild(editModelLink);
		},

		_formatEditForm: function (el, oRecord, oColumn, oData, oDataTable) {
			var editFormLink = document.createElement("a");
			Dom.addClass(editFormLink, "edit-form");
			editFormLink.innerHTML = "&nbsp;";
			var modelName = oRecord.getData("fileName");
			modelName = modelName + "NS:" + modelName;
			editFormLink.href = Alfresco.constants.URL_PAGECONTEXT + "doc-forms-list?doctype=" + modelName;
			el.appendChild(editFormLink);
		},

		_deleteRow: function (oArgs) {
			var target = oArgs.target;
			var column = this.getColumn(target);
			var oRecord = this.getRecord(target);
			if (column.key == 'delete') {
				if (oRecord.getData("node").properties["cm:modelActive"]) {
					mySimpleDialog = new YAHOO.widget.SimpleDialog("dlg-" + oRecord.getId(), {
						width: "20em",
						fixedcenter: true,
						modal: true,
						visible: false,
						draggable: false,
						close: false
					});

					mySimpleDialog.setHeader("Внимание!");
					mySimpleDialog.setBody("Нельзя удалить активную модель");
					mySimpleDialog.cfg.setProperty("icon", YAHOO.widget.SimpleDialog.ICON_WARN);
					mySimpleDialog.cfg.queueProperty("buttons", [
						{ text: "Ок", handler: function () {
							this.hide();
						} }
					]);
					mySimpleDialog.render(document.body);
					mySimpleDialog.show();
				} else {
					var config = {
						method: "DELETE",
						url: Alfresco.constants.PROXY_URI + "slingshot/doclib/action/file/node/" + oRecord.getData("nodeRef").replace(":/", ""),
						successCallback: {
							fn: function (response, obj) {
								if (response.json.message) {
									Alfresco.util.PopupManager.displayMessage({ text: response.json.message });
								}
								this.deleteRow(target);
							},
							scope: this
						},
						failureCallback: {
							fn: function (response, obj) {
								Alfresco.util.PopupManager.displayMessage(
									{
										text: Alfresco.util.message("Ошибка удаления модели")
									});
							},
							scope: this
						},
						dataObj: {
							name: oRecord.getData("displayName")
						}
					};

					if (confirm('Вы действительно хотите удалить модель?')) {
						Alfresco.util.Ajax.jsonRequest(config);
					}
				}
			} else {
				//this.onEventShowCellEditor(oArgs);
			}
		}//_deleteRow
	});
})();
