/**
 * LogicECM root namespace.
 *
 * @namespace LogicECM
 */
// Ensure LogicECM root object exists
if (typeof LogicECM == "undefined" || !LogicECM) {
	LogicECM = {};
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

	LogicECM.module.ModelEditor.ModelList2 = function (fieldHtmlId) {
		LogicECM.module.ModelEditor.ModelList2.superclass.constructor.call(this, "LogicECM.module.ModelEditor.ModelList2", fieldHtmlId, ["button", "dom", "datasource", "datatable", "paginator", "event", "element"]);

		return this;
	};

	YAHOO.lang.extend(LogicECM.module.ModelEditor.ModelList2, Alfresco.component.Base);

	YAHOO.lang.augmentObject(LogicECM.module.ModelEditor.ModelList2.prototype, {
		onReady: function () {
			var uriDocListList = YAHOO.lang.substitute(Alfresco.constants.PROXY_URI + "lecm/docmodels/list?parentType=dict");
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
				{key: "expand", label: "", sortable: false, formatter: this._formatExpandModel(), width: 15, maxAutoWidth: 15},
				{key: "title", label: Alfresco.util.message('lecm.meditor.lbl.document.model'), sortable: false, formatter: this._formatTitle, width: 250, maxAutoWidth: 250},
				{key: "active", label: Alfresco.util.message('lecm.meditor.lbl.active'), sortable: false, formatter: this._formatActive, width: 100, maxAutoWidth: 100},
				{key: "edit-model", label: "", sortable: false, formatter: this._formatEditModel(), width: 15, maxAutoWidth: 15},
				{key: "edit-control", label: "", sortable: false, formatter: this._formatEditControl(), width: 15, maxAutoWidth: 15},
				{key: "edit-form", label: "", sortable: false, formatter: this._formatEditForm(), width: 15, maxAutoWidth: 15},
				{key: "restore-model", label: "", sortable: false, formatter: this._formatRestoreModel(), width: 15, maxAutoWidth: 15},
				{key: "delete", label: "", sortable: false, formatter: this._formatDelete(), width: 15, maxAutoWidth: 15}
			];

			// DataTable definition
			this.widgets.dataTable = new YAHOO.widget.DataTable(this.id + "-body", columnDefinitions, this.widgets.dataSource);
			this.widgets.dataTable.subscribe('cellClickEvent', this._onClickListener);
			this.widgets.dataTable.subscribe("dataReturnEvent", function(oArgs) {
				var fakeItem = {
					isActive: true,
					isDocumentModel: false,
					isRestorable: false,
					modelName: "fakeModel",
					nodeRef: null,
					title: Alfresco.util.message('lecm.meditor.ttl.fake.model'),
					types: [{
						isDocument: false,
						modelName: "fakeModel",
						title: Alfresco.util.message('lecm.meditor.ttl.fake.document'),
						typeName: "fake"
					}]
				};
				oArgs.response.results.splice(0, 0, fakeItem);
				this.destination = oArgs.response.meta.metadata.parent;
			}, this);

			this.widgets.simpleView = Alfresco.util.createYUIButton(this, "button", function () {
				var destination = this.widgets.dataTable.destination;
				var url = Alfresco.constants.URL_PAGECONTEXT + "dict-model-create?doctype=&formId=create-dict-model&redirect=" + Alfresco.constants.URL_PAGECONTEXT + "doc-model-list&destination=" + destination + "&itemId=cm:dictionaryModel&mimeType=text/xml";
				window.location = url;
			}, {label: Alfresco.util.message('lecm.meditor.lbl.create')});

			// Finally show the component body here to prevent UI artifacts on YUI button decoration
			Dom.setStyle(this.id + "-body", "visibility", "visible");
		},

		_formatExpandModel: function () {
			var scope = this;

			return function (el, oRecord, oColumn, oData, oDataTable) {
				if (oRecord.getData("types") != null) {
					el.innerHTML = "";

					if (oRecord.getData("expanded")) {
						var collapseLink = document.createElement("a");
						collapseLink.title = scope.msg("title.model.collapse");
						Dom.addClass(collapseLink, "collapse");
						collapseLink.innerHTML = "&nbsp;";
						el.appendChild(collapseLink);
					} else {
						var expandLink = document.createElement("a");
						expandLink.title = scope.msg("title.model.expand");
						Dom.addClass(expandLink, "expand");
						expandLink.innerHTML = "&nbsp;";
						el.appendChild(expandLink);
					}
				}
			};
		},

		_formatTitle: function formaterRenderActions(el, oRecord, oColumn, oData, oDataTable) {
			el.innerHTML = (oRecord.getData("title") || oData);
		},

		_formatActive: function formaterRenderActive(el, oRecord, oColumn, oData, oDataTable) {
			if (oRecord.getData("isActive") != null) {
				el.innerHTML = "";

				var activeElement = document.createElement("span");
				activeElement.innerHTML = (oRecord.getData("isActive") ? Alfresco.util.message('lecm.meditor.lbl.yes') : Alfresco.util.message('lecm.meditor.lbl.no'));
				el.appendChild(activeElement);
			}
		},

		_formatDelete: function () {
			var scope = this;

			return function (el, oRecord, oColumn, oData, oDataTable) {
				if (oRecord.getData("nodeRef") != null && !oRecord.getData("isRestorable") && oRecord.getData("isDocumentModel") != null) {
					el.innerHTML = "";

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
				if (oRecord.getData("nodeRef") != null && oRecord.getData("isDocument")==false) {
					el.innerHTML = "";

					var editModelLink = document.createElement("a");
					editModelLink.title = scope.msg("title.model.edit");
					Dom.addClass(editModelLink, "edit-model");
					editModelLink.innerHTML = "&nbsp;";

					editModelLink.href = Alfresco.constants.URL_PAGECONTEXT + "dict-model-edit?formId=edit-dict-model&nodeRef=" + oRecord.getData("nodeRef") + "&redirect=" + Alfresco.constants.URL_PAGECONTEXT + "doc-model-list&doctype=" + oRecord.getData("typeName");
					el.appendChild(editModelLink);
				} else if(oRecord.getData("isDocument")==false) {
					el.innerHTML = "";

					var editModelLink = document.createElement("a");
					editModelLink.title = scope.msg("title.model.edit");
					Dom.addClass(editModelLink, "edit-model");
					editModelLink.innerHTML = "&nbsp;";

					editModelLink.href = Alfresco.constants.URL_PAGECONTEXT + "dict-model-view?itemId=cm:dictionaryModel&formId=view-dict-model&nodeRef=" + oRecord.getData("nodeRef") + "&redirect=" + Alfresco.constants.URL_PAGECONTEXT + "doc-model-list&doctype=" + oRecord.getData("typeName");
					el.appendChild(editModelLink);
				}
			};
		},

        _formatRestoreModel: function () {
            var scope = this;

            return function (el, oRecord, oColumn, oData, oDataTable) {
                if (oRecord.getData("isRestorable")) {
	                el.innerHTML = "";

                    var restoreModelLink = document.createElement("a");
                    restoreModelLink.title = scope.msg("title.model.restore");
                    Dom.addClass(restoreModelLink, "restore-model");
                    restoreModelLink.innerHTML = "&nbsp;";

                    restoreModelLink.addEventListener("click", function() {
                        var config = {
                            method: "GET",
                            url: Alfresco.constants.PROXY_URI + "lecm/models/restore?modelName=" + oRecord.getData("modelName"),
                            successCallback: {
                                fn: function (response, obj) {
                                    Alfresco.util.PopupManager.displayMessage({ text: Alfresco.util.message('lecm.meditor.msg.model.restored') });
                                },
                                scope: this
                            },
                            failureCallback: {
                                fn: function (response, obj) {
                                    Alfresco.util.PopupManager.displayMessage(
                                        {
                                            text: Alfresco.util.message("lecm.meditor.msg.model.restored.failed")
                                        });
                                },
                                scope: this
                            },
                            dataObj: {
                                name: oRecord.getData("displayName")
                            }
                        };

                        if (confirm(Alfresco.util.message('lecm.meditor.msg.defaul.model'))) {
                            Alfresco.util.Ajax.jsonRequest(config);
                        }

                    });
                    el.appendChild(restoreModelLink);
                }
            };
        },

		_formatEditControl: function() {
			var scope = this;

			return function (el, oRecord, oColumn, oData, oDataTable) {
				if (oRecord.getData("isActiveModel") && oRecord.getData("typeName")) {
					el.innerHTML = "";

					var editControlLink = document.createElement("a");
					editControlLink.title = scope.msg("title.controls.edit");
					Dom.addClass(editControlLink, "edit-control");
					editControlLink.innerHTML = "&nbsp;";
					editControlLink.href = Alfresco.constants.URL_PAGECONTEXT + "dict-controls-list?doctype=" + oRecord.getData("typeName");
					el.appendChild(editControlLink);
				}
			};
		},

		_formatEditForm: function () {
			var scope = this;

			return function (el, oRecord, oColumn, oData, oDataTable) {
				if (oRecord.getData("isActiveModel") && oRecord.getData("typeName") && oRecord.getData("typeName") != "fake") {
					el.innerHTML = "";

					var editFormLink = document.createElement("a");
					editFormLink.title = scope.msg("title.forms.edit");
					Dom.addClass(editFormLink, "edit-form");
					editFormLink.innerHTML = "&nbsp;";
					editFormLink.href = Alfresco.constants.URL_PAGECONTEXT + "dict-forms-list?doctype=" + oRecord.getData("typeName");
					el.appendChild(editFormLink);
				}
			};
		},

		_formatEditStatemachine: function () {
			var scope = this;

			return function (el, oRecord, oColumn, oData, oDataTable) {
				if (oRecord.getData("isActiveModel") && oRecord.getData("isDocument") && oRecord.getData("typeName") && oRecord.getData("typeName") != "fake") {
					el.innerHTML = "";

					var editStatemachineLink = document.createElement("a");
					editStatemachineLink.title = scope.msg("title.statemachine.edit");
					Dom.addClass(editStatemachineLink, "edit-statemachine");
					editStatemachineLink.innerHTML = "&nbsp;";
					editStatemachineLink.href = Alfresco.constants.URL_PAGECONTEXT + "statemachine?statemachineId=" + oRecord.getData("typeName").replace(":", "_");
					el.appendChild(editStatemachineLink);
				}
			};
		},

		_onClickListener: function (oArgs) {
			var target = oArgs.target;
			var column = this.getColumn(target);
			var oRecord = this.getRecord(target);
			if (column.key == 'delete') {
				if (oRecord.getData("nodeRef") != null && !oRecord.getData("isRestorable")) {
					if (oRecord.getData("isActive")) {
						mySimpleDialog = new YAHOO.widget.SimpleDialog("dlg-" + oRecord.getId(), {
							width: "20em",
							fixedcenter: true,
							modal: true,
							visible: false,
							draggable: false,
							close: false
						});

						mySimpleDialog.setHeader(Alfresco.util.message('lecm.meditor.ttl.caution'));
						mySimpleDialog.setBody(Alfresco.util.message('lecm.meditor.msg.can.not.remove.act.model'));
						mySimpleDialog.cfg.setProperty("icon", YAHOO.widget.SimpleDialog.ICON_WARN);
						mySimpleDialog.cfg.queueProperty("buttons", [
							{ text: Alfresco.util.message('lecm.meditor.lbl.ok'), handler: function () {
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
											text: Alfresco.util.message("lecm.meditor.msg.delete.model.fail")
										});
								},
								scope: this
							},
							dataObj: {
								name: oRecord.getData("displayName")
							}
						};

						if (confirm(Alfresco.util.message('lecm.meditor.msg.want.delete.model'))) {
							Alfresco.util.Ajax.jsonRequest(config);
						}
					}
				}
			} else if (column.key == 'expand') {
				var types = oRecord.getData("types");
				if (types != null) {
					var expanded = oRecord.getData("expanded") != null && oRecord.getData("expanded");
					if (!expanded) {
						var typeRows = [];
						for (var i = 0; i < types.length; i++) {
							typeRows.push({
								title: types[i].title,
								typeName: types[i].typeName,
								isActiveModel: oRecord.getData("isActive"),
								isDocument: types[i].isDocument,
								parentModelName: oRecord.getData("modelName"),
								nodeRef: oRecord.getData("nodeRef")
							});

						}
						this.addRows(typeRows, this.getTrIndex(oArgs.target) + 1);
					} else {
						var recordSet = this.getRecordSet();
						var findedRows = [];
						for (i = 0; i < recordSet.getLength(); i++) {
							if (recordSet.getRecord(i).getData("parentModelName") == oRecord.getData("modelName")) {
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
		}
	});
})();
