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

		return this;
	};

	YAHOO.lang.extend(LogicECM.module.ModelEditor.ModelList, Alfresco.component.Base);

	YAHOO.lang.augmentObject(LogicECM.module.ModelEditor.ModelList.prototype, {
		onReady: function () {
			var uriDocListList = YAHOO.lang.substitute(Alfresco.constants.PROXY_URI + "lecm/docmodels/list");
			this.widgets.dataSource = new YAHOO.util.DataSource(uriDocListList,
				{
					responseType: YAHOO.util.DataSource.TYPE_JSON,
					connXhrMode: "queueRequests",
					responseSchema: {
						resultsList: "items",
						metaFields: {
							metadata: "metadata"
						}
					}
				});

			// DataTable column defintions
			var columnDefinitions = [
				{key: "title", label: "Модель документа", sortable: false, formatter: this._formatTitle, width: 250, maxAutoWidth: 250},
				{key: "active", label: "Активна", sortable: false, formatter: this._formatActive, width: 100, maxAutoWidth: 100},
				{key: "edit-model", label: "", sortable: false, formatter: this._formatEditModel(), width: 15, maxAutoWidth: 15},
				{key: "edit-form", label: "", sortable: false, formatter: this._formatEditForm(), width: 15, maxAutoWidth: 15},
				{key: "edit-statemachine", label: "", sortable: false, formatter: this._formatEditStatemachine(), width: 15, maxAutoWidth: 15},
				{key: "delete", label: "", sortable: false, formatter: this._formatDelete(), width: 15, maxAutoWidth: 15}
			];

			// DataTable definition
			this.widgets.dataTable = new YAHOO.widget.DataTable(this.id + "-body", columnDefinitions, this.widgets.dataSource);
			this.widgets.dataTable.subscribe('cellClickEvent', this._deleteRow);
			this.widgets.dataTable.subscribe("dataReturnEvent", function(oArgs) {
				this.destination = oArgs.response.meta.metadata.parent;
			}, this);

			this.widgets.simpleView = Alfresco.util.createYUIButton(this, "button", function () {
				var destination = this.widgets.dataTable.destination;
				var url = Alfresco.constants.URL_PAGECONTEXT + "doc-model-create?formId=create-model&redirect=" + Alfresco.constants.URL_PAGECONTEXT + "doc-model-list&destination=" + destination + "&itemId=cm:dictionaryModel&mimeType=text/xml";
				window.location = url;
			}, {label: "Создать"});

		},

		_formatTitle: function formaterRenderActions(el, oRecord, oColumn, oData, oDataTable) {
			el.innerHTML = (oRecord.getData("title") || oData);
		},

		_formatActive: function formaterRenderActive(el, oRecord, oColumn, oData, oDataTable) {
			var activeElement = document.createElement("span");
			activeElement.innerHTML = (oRecord.getData("isActive") ? "Да" : "Нет");
			el.appendChild(activeElement);
		},

		_formatDelete: function () {
			var scope = this;

			return function (el, oRecord, oColumn, oData, oDataTable) {
				if (oRecord.getData("nodeRef") != null) {
					var deleteLink = document.createElement("a");
					deleteLink.title = scope.msg("title.model.delete");
					Dom.addClass(deleteLink, "delete");
					deleteLink.innerHTML = "&nbsp;";
					el.appendChild(deleteLink);
				}
			};
		},

		_formatEditModel: function () {
			var scope = this;

			return function (el, oRecord, oColumn, oData, oDataTable) {
				if (oRecord.getData("nodeRef") != null) {
					var editModelLink = document.createElement("a");
					editModelLink.title = scope.msg("title.model.edit");
					Dom.addClass(editModelLink, "edit-model");
					editModelLink.innerHTML = "&nbsp;";

					var formIdParam = "";
					if (oRecord.getData("isDocument")) {
						formIdParam = "formId=edit-model";
					}

					editModelLink.href = Alfresco.constants.URL_PAGECONTEXT + "doc-model-edit?" + formIdParam + "&nodeRef=" + oRecord.getData("nodeRef") + "&redirect=" + Alfresco.constants.URL_PAGECONTEXT + "doc-model-list";
					el.appendChild(editModelLink);
				}
			};
		},

		_formatEditForm: function () {
			var scope = this;

			return function (el, oRecord, oColumn, oData, oDataTable) {
				if (oRecord.getData("isActive")) {
					var editFormLink = document.createElement("a");
					editFormLink.title = scope.msg("title.forms.edit");
					Dom.addClass(editFormLink, "edit-form");
					editFormLink.innerHTML = "&nbsp;";
					editFormLink.href = Alfresco.constants.URL_PAGECONTEXT + "doc-forms-list?doctype=" + oRecord.getData("id");
					el.appendChild(editFormLink);
				}
			};
		},

		_formatEditStatemachine: function () {
			var scope = this;

			return function (el, oRecord, oColumn, oData, oDataTable) {
				if (oRecord.getData("isActive") && oRecord.getData("isDocument")) {
					var editStatemachineLink = document.createElement("a");
					editStatemachineLink.title = scope.msg("title.statemachine.edit");
					Dom.addClass(editStatemachineLink, "edit-statemachine");
					editStatemachineLink.innerHTML = "&nbsp;";
					editStatemachineLink.href = Alfresco.constants.URL_PAGECONTEXT + "statemachine?statemachineId=" + oRecord.getData("id").replace(":", "_");
					el.appendChild(editStatemachineLink);
				}
			};
		},

		_deleteRow: function (oArgs) {
			var target = oArgs.target;
			var column = this.getColumn(target);
			var oRecord = this.getRecord(target);
			if (column.key == 'delete') {
				if (oRecord.getData("isActive")) {
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
