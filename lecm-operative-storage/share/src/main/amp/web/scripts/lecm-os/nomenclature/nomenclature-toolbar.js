if (typeof LogicECM == "undefined" || !LogicECM) {
	LogicECM = {};
}

LogicECM.module = LogicECM.module || {};
LogicECM.module.Nomenclature = LogicECM.module.Nomenclature || {};

(function() {

    var Bubbling = YAHOO.Bubbling;

    LogicECM.module.Nomenclature.Toolbar = function(htmlId) {
		LogicECM.module.Nomenclature.Toolbar.superclass.constructor.call(this, "LogicECM.module.Nomenclature.Toolbar", htmlId);

		Bubbling.on("selectedItemsChanged", this.onCheck, this);
		Bubbling.on("dataItemCreated", this.onItemCreated, this);
		return this;
	};


	YAHOO.extend(LogicECM.module.Nomenclature.Toolbar, LogicECM.module.Base.Toolbar);

	YAHOO.lang.augmentObject(LogicECM.module.Nomenclature.Toolbar.prototype, {
		node: null,
		editDialogOpening: false,
		importFromDialog: null,
		importInfoDialog: null,
		importErrorDialog: null,
		nodeChildren: null,
		allowedNodeChildrenActions: ['Уничтожение номенклатурного дела', 'Передача номенклатурного дела в архив'],
        serviceRoot: null,
		options: {
			armSelectedNodeRef: null,
			isRoot: false
		},

		_initButtons: function() {
			this.loadSelectedTreeNode();

			this.toolbarButtons["defaultActive"].newRowButton = Alfresco.util.createYUIButton(this, "newRowButton", this.onNewRow);
			this.toolbarButtons["defaultActive"].newRowButtonAdditional = Alfresco.util.createYUIButton(this, "newRowButtonAdditional", this.onNewRow);
			// this.toolbarButtons["defaultActive"].deleteNodeButton = Alfresco.util.createYUIButton(this, "deleteNodeButton", this.onDeleteNode);
			this.toolbarButtons["defaultActive"].exportButton = Alfresco.util.createYUIButton(this, "exportButton", this.onExport);
			this.toolbarButtons["defaultActive"].importButton = Alfresco.util.createYUIButton(this, "importButton", this.showImportDialog,
				{
					disabled: this.options.searchButtonsType != 'defaultActive'
				});

			this.toolbarButtons["defaultActive"].groupActionsButton = new YAHOO.widget.Button(
				this.id + "-groupActionsButton",
				{
					type: "menu",
					menu: [],
					disabled: false
				}
			);


			this.toolbarButtons["defaultActive"].groupActionsButton.set("label", this.msg("button.group-actions"));
			this.toolbarButtons["defaultActive"].groupActionsButton.on("click", this.onCheckDocumentFinished.bind(this));
			this.toolbarButtons["defaultActive"].groupActionsButton.on("click", this.onRootActionClick.bind(this));
			this.toolbarButtons["defaultActive"].groupActionsButton.getMenu().subscribe("hide", this.clearOperationsList.bind(this));
			this.toolbarButtons["defaultActive"].groupActionsButton.set("disabled", this.options.isRoot);

			if(!this.options.isRoot) {
				this.toolbarButtons["defaultActive"].groupActionsButton.set("label", "Действия на текущем узле");
			}

			Alfresco.util.createYUIButton(this, "import-form-cancel", this.hideImportDialog, {});
			YAHOO.util.Event.on(this.id + "-import-form-import-file", "change", this.checkImportFile, null, this);
			YAHOO.util.Event.on(this.id + "-import-error-form-show-more-link", "click", this.errorFormShowMore, null, this);
		},

		getCurrentNodeCasesToDestroy: function() {
			var nodeRef = new Alfresco.util.NodeRef(this.options.armSelectedNodeRef);

			var templateUrl = Alfresco.constants.PROXY_URI + "/lecm/forms/picker/node/" + nodeRef.uri + "/children";
			var templateRequestParams = {
				selectableType: "lecm-os:nomenclature-case",
				additionalFilter: "@lecm\\-os\\:nomenclature\\-case\\-status:\"MARK_TO_DESTROY\" OR @lecm\\-os\\:nomenclature\\-case\\-status:\"CLOSED\"",
				searchTerm: "",
				nameSubstituteString: "{lecm-os:nomenclature-case-index} - {cm:title}"
			};

			Alfresco.util.Ajax.jsonRequest({
                method: "GET",
                url: templateUrl,
                dataObj: templateRequestParams,
                successCallback: {
                    fn: function (oResponse) {
                        this.nodeChildren = oResponse.json.data.items;
                    },
                    scope: this
                },
                failureCallback: {
                    fn: function () {
                    }
                },
                scope: this,
                execScripts: true
            });


		},

		onCheck: function() {
			var button = this.toolbarButtons["defaultActive"].groupActionsButton;
			var buttonName = this.msg("button.group-actions");
			var items = this.modules.dataGrid.getSelectedItems();

			if (items.length == 0) {
				// button.set("disabled", true);
				buttonName = 'Действия на текущем узле';
			} else {
				button.set("disabled", false);
				buttonName += "<span class=\"group-actions-counter\">";
				buttonName += "(" + items.length + ")";
				buttonName += "</span>";
			}

			button.set("label", buttonName);
		},

		_createScriptForm: function _createScriptFormFunction(item, cbObj) {
			var doBeforeDialogShow = function (p_form, p_dialog) {
				var contId = p_dialog.id + "-form-container";
				Alfresco.util.populateHTML(
					[contId + "_h", item.actionId ]
				);

				Dom.addClass(contId, "metadata-form-edit");
				this.doubleClickLock = false;

				p_dialog.dialog.subscribe('destroy', LogicECM.module.Base.Util.formDestructor, {moduleId: p_dialog.id}, this);
			};


			var templateUrl = Alfresco.constants.URL_SERVICECONTEXT + "/lecm/components/form/script";
			var templateRequestParams = {
				itemKind: "type",
				itemId: item.actionId,
				formId: "scriptForm",
				mode: "create",
				submitType: "json",
				items: JSON.stringify(item.items)
			};

			// Using Forms Service, so always create new instance
			var scriptForm = new Alfresco.module.SimpleDialog(this.id + "-scriptForm");
			scriptForm.setOptions(
				{
					width: "40em",
					templateUrl: templateUrl,
					templateRequestParams: templateRequestParams,
					actionUrl: null,
					selectedItems: item.items,
					destroyOnHide: true,
					doBeforeDialogShow: {
						fn: doBeforeDialogShow,
						scope: this
					},
					onSuccess: {
						fn: function DataGrid_onActionCreate_success(response) {
							if (cbObj && YAHOO.lang.isFunction(cbObj.fn)) {
								cbObj.fn.call(this, '', cbObj);
							} else {
								this._actionResponse(item.actionId);
							}
							this._actionResponse(item.actionId, response);
						},
						scope: this
					},
					onFailure: {
						fn: function DataGrid_onActionCreate_failure(response) {
							Alfresco.util.PopupManager.displayMessage(
								{
									text: this.msg("message.save.failure")
								});
							this.doubleClickLock = false;
						},
						scope: this
					},
					scope: this
				}).show();
		},

		onRootActionClick: function() {

			var itemsData = this.modules.dataGrid.getSelectedItems();
			if(itemsData && itemsData.length) {
				// Есть выбранные элементы -> действия именно над ними, не будем мешаться
				return;
			}

			var button = this.toolbarButtons["defaultActive"].groupActionsButton;
			var menu = button.getMenu();

			Alfresco.util.Ajax.jsonRequest({
                method: "GET",
                url: Alfresco.constants.PROXY_URI + 'lecm/os/nomenclature/getCasesForRootAction?nodeRef=' + this.node.nodeRef,
                successCallback: {
                    fn: function (oResponse) {
						var actionItems = [];
                        var forArchive = oResponse.json.forArchive;
                        var forDestroy = oResponse.json.forDestroy;

						if(forArchive != null && forArchive.length > 0) {
							actionItems.push({
								text: 'Передача номенклатурного дела в архив',
								value: 'Передача номенклатурного дела в архив',
								onclick: {
									fn: this.onGroupActionsClickProxy,
									obj: {
										actionId: 'Передача номенклатурного дела в архив',
										type: 'lecm-group-actions:script-action',
										withForm: false,
										items: forArchive,
										label: 'Передача номенклатурного дела в архив'
									},
									scope: this
								}
							});
						}

						if(forDestroy != null && forDestroy.length > 0) {
							actionItems.push({
								text: 'Уничтожение номенклатурного дела',
								value: 'Уничтожение номенклатурного дела',
								onclick: {
									fn: this.onGroupActionsClickProxy,
									obj: {
										actionId: 'Уничтожение номенклатурного дела',
										type: 'lecm-group-actions:script-action',
										withForm: false,
										items: forDestroy,
										label: 'Уничтожение номенклатурного дела'
									},
									scope: this
								}
							});
						}

						if (actionItems.length == 0) {
							actionItems.push({
								text: Alfresco.util.message('lecm.os.msg.no.operations'),
								disabled: true
							});
						}

						if (YAHOO.util.Dom.inDocument(menu.element)) {
							menu.clearContent();
							menu.addItems(actionItems);
							menu.render();
						} else {
							menu.addItems(actionItems);
						}

                    },
                    scope: this
                },
                failureCallback: {
                    fn: function () {
                    }
                },
                scope: this,
                execScripts: true
            });
		},

		onCheckDocumentFinished: function(){
			var button = this.toolbarButtons["defaultActive"].groupActionsButton;
			var menu = button.getMenu();
			var itemsData = this.modules.dataGrid.getSelectedItems();
			var items = [];
			itemsData.forEach(function(el) {
				items.push(el.nodeRef);
			});
			if(itemsData.length == 0) {

				// Элементы не выбраны, но событие по клику пришло -> действие на узле
				return;
			}
			var loadItem = [];
			loadItem.push({
				text: Alfresco.util.message('lecm.os.msg.loading'),
				disabled: true
			});
			if (YAHOO.util.Dom.inDocument(menu.element)) {
				menu.clearContent();
				menu.addItems(loadItem);
				menu.render();
			} else {
				menu.itemData = loadItem;
			}
			var me = this;
			Alfresco.util.Ajax.jsonRequest({
				method: "POST",
				url: Alfresco.constants.PROXY_URI + "lecm/groupActions/list",
				dataObj: {
					items: JSON.stringify(items)
				},
				successCallback: {
					fn: function (oResponse) {
						var json = oResponse.json;
						var actionItems = [];
						var wideActionItems = [];
						for (var i in json) {
							if (!json[i].wide) {
								actionItems.push({
									text: json[i].title,
									value: json[i].id,
									onclick: {
										fn: me.onGroupActionsClickProxy,
										obj: {
											actionId: json[i].id,
											type: json[i].type,
											withForm: json[i].withForm,
											items: items,
											workflowId: json[i].workflowId,
											label: json[i].title
										},
										scope: me
									}
								});
							}
						}
						if (actionItems.length == 0) {
							actionItems.push({
								text: Alfresco.util.message('lecm.os.msg.no.operations'),
								disabled: true
							});
						}

						if (YAHOO.util.Dom.inDocument(menu.element)) {
							menu.clearContent();
							menu.addItems(actionItems);
							menu.addItems(wideActionItems);
							menu.render();
						} else {
							menu.addItems(actionItems);
							menu.addItems(wideActionItems);
						}
					}
				},
				failureCallback: {
					fn: function () {
					}
				},
				scope: this,
				execScripts: true
			});
		},

		canDeleteUnit: function(p_sType, p_aArgs, p_oItem) {

			Alfresco.util.Ajax.jsonRequest({
				method: 'GET',
				url: Alfresco.constants.PROXY_URI + 'lecm/os/nomenclature/unitHaveChildren',
				dataObj: {
					items: p_oItem.items
				},
				successCallback: {
					scope: this,
					fn: function(response) {
						if (response.json.notEmpty) {
							Alfresco.util.PopupManager.displayPrompt({
								title:Alfresco.util.message('lecm.os.lbl.remove.section'),
								text: Alfresco.util.message('lecm.os.msg.not.empty.sections'),
								buttons:[
								{
									text: Alfresco.util.message('lecm.os.btn.ok'),
									handler: {
										fn: cancel
									}
								}]
							});
						} else {
							this.onGroupActionsClick(p_sType, p_aArgs, p_oItem);
						}
					}
				},
				failureMessage: this.msg('message.failure'),
				scope: this
			});

			function cancel() {
				this.destroy();
			}
		},

		destroyND_Propmt: function(p_sType, p_aArgs, p_oItem) {
			var nodeRef = new Alfresco.util.NodeRef(p_oItem.items[0]);

			var html = '<p>';
			var itemsData = this.modules.dataGrid.getSelectedItems();
			if(itemsData.length == 0) {
				this.nodeChildren.forEach(function(el){
					var msg = el.name + '<br>';
					html += "<div class=\"noerror-item\">" + msg + "</div>";
				});
			}

			itemsData.forEach(function(el) {
				var msg = el.itemData['prop_os-aspects_common-index'].displayValue+ ' - ' + el.itemData['prop_cm_title'].displayValue + '<br>';
				html += "<div class=\"noerror-item\">" + msg + "</div>";
			});

			Alfresco.util.PopupManager.displayPrompt({
				title:'Уничтожить дела',
				text: html,
				noEscape: true,
				buttons:[
					{
						text:'Ок',
						handler: {
							obj: {
								context: this,
								p_sType: p_sType,
								p_aArgs: p_aArgs,
								p_oItem: p_oItem
							},
							fn: destroyCases
						}
					},
					{
						text:'Отмена',
						handler:function DataGridActions__onActionDelete_cancel() {
							this.destroy();
						}
					}
				]
			});

			function destroyCases(event, obj) {
				obj.context.onGroupActionsClick(obj.p_sType, obj.p_aArgs, obj.p_oItem);
				this.destroy();
			}

		},

		deleteND_Propmt: function(p_sType, p_aArgs, p_oItem) {

			Alfresco.util.Ajax.jsonRequest({
				method: 'GET',
				url: Alfresco.constants.PROXY_URI + 'lecm/os/nomenclature/caseHasDocsVolumes',
				dataObj: {
					items: p_oItem.items,
					checkVolumes: false
				},
				successCallback: {
					scope: this,
					fn: function(response) {
						if(response.json.notEmpty) {
							Alfresco.util.PopupManager.displayMessage({
								text: Alfresco.util.message('lecm.os.msg.doc.contains.docs')
							});
						} else {
							this.onGroupActionsClick(p_sType, p_aArgs, p_oItem);
						}
					}
				},
				failureMessage: Alfresco.util.message('lecm.os.msg.error'),
				scope: this
			});

			function destroyND(event, obj) {
				obj.context.onGroupActionsClick(obj.p_sType, obj.p_aArgs, obj.p_oItem);
				this.destroy();
			}
		},

		closeYearSectionPrompt: function(p_sType, p_aArgs, p_oItem) {
			var nodeRef = p_oItem.items[0];
			Alfresco.util.Ajax.jsonRequest({
				method: 'GET',
				url: Alfresco.constants.PROXY_URI + 'lecm/os/nomenclature/getOpenTransientCases?nodeRef=' + nodeRef,
				successCallback: {
					scope: this,
					fn: function(response) {
						var items = response.json.items;
						if (items && items.length) {
							Alfresco.util.PopupManager.displayPrompt({
								title:Alfresco.util.message('lecm.os.lbl.close.nomen'),
								text: Alfresco.util.message('lecm.os.msg.nomen.not.closed.docs'),
								buttons:[{
									text: Alfresco.util.message('lecm.os.msg.all.docs.close'),
									handler: {
										obj: {
											context: this,
											p_sType: p_sType,
											p_aArgs: p_aArgs,
											p_oItem: p_oItem
										},
										fn: closeAllCases
									}
								}, {
									text: Alfresco.util.message('lecm.os.msg.move.passing.docs'),
									handler: {
										obj: {
											context: this,
											p_sType: p_sType,
											p_aArgs: p_aArgs,
											p_oItem: p_oItem,
											cases: items,
											fn: closeAllCases
										},
										fn: moveOpenTransientCases
									}
								}, {
									text: Alfresco.util.message('lecm.os.btn.cancel'),
									handler: {
										fn: cancel
									}
								}]
							});
						} else {
							this.onGroupActionsClick(p_sType, p_aArgs, p_oItem);
						}
					}
				},
				failureMessage: this.msg('message.failure'),
				scope: this
			});

			function closeAllCases(event, obj) {
				obj.context.onGroupActionsClick(obj.p_sType, obj.p_aArgs, obj.p_oItem);
				this.destroy();
			}

			function moveOpenTransientCases(event, obj) {
				var newParams = {};
				newParams.actionId = 'Перемещение номенклатурного дела';
				newParams.items = obj.cases;
				obj.context._createScriptForm(newParams, obj);
				this.destroy();
			}

			function cancel() {
				this.destroy();
			}
		},

		onGroupActionsClickProxy: function onGroupActionsClickProxy(p_sType, p_aArgs, p_oItem){
			switch (p_oItem.actionId) {
				case 'Удаление номенклатурного дела':
					this.deleteND_Propmt.call(this, p_sType, p_aArgs, p_oItem);
					break;
				case 'Уничтожение номенклатурного дела':
					this.destroyND_Propmt.call(this, p_sType, p_aArgs, p_oItem);
					break;
				case 'Закрытие номенклатуры дел':
					this.closeYearSectionPrompt(p_sType, p_aArgs, p_oItem);
					break;
				case 'Удаление раздела номенклатуры дел':
					this.canDeleteUnit(p_sType, p_aArgs, p_oItem);
					break;
				default:
					this.onGroupActionsClick(p_sType, p_aArgs, p_oItem);
			}
		},

		onGroupActionsClick: function onGroupActionsClick(p_sType, p_aArgs, p_oItem) {
			if (p_oItem.withForm) {
				this._createScriptForm(p_oItem);
			} else {
				if (p_oItem.type == "lecm-group-actions:script-action") {
					var me = this;
					Alfresco.util.PopupManager.displayPrompt(
						{
							title: Alfresco.util.message('lecm.os.ttl.action.performing'),
							text: Alfresco.util.message('lecm.os.ttl.confirm.action') + " \"" + p_oItem.actionId + "\"",
							buttons: [
								{
									text: Alfresco.util.message('lecm.os.btn.ok'),
									handler: function dlA_onAction_action() {
										this.destroy();
										Alfresco.util.Ajax.jsonRequest({
											method: "POST",
											url: Alfresco.constants.PROXY_URI + "lecm/groupActions/exec",
											dataObj: {
												items: p_oItem.items,
												actionId: p_oItem.actionId
											},
											successCallback: {
												fn: function (oResponse) {
													me._actionResponse(p_oItem.actionId, oResponse);
												}
											},
											failureCallback: {
												fn: function () {
												}
											},
											scope: me,
											execScripts: true
										});

									}
								},
								{
									text: Alfresco.util.message('lecm.os.btn.cancel'),
									handler: function dlA_onActionDelete_cancel() {
										this.destroy();
									},
									isDefault: true
								}
							]
						});
				} else if (p_oItem.type == "lecm-group-actions:workflow-action") {
					if (this.doubleClickLock) return;
					this.doubleClickLock = true;

					this.options.currentSelectedItems = p_oItem.items;
					var templateUrl = Alfresco.constants.URL_SERVICECONTEXT;
					var formWidth = "84em";

					templateUrl += "lecm/components/form";
					var templateRequestParams = {
							itemKind: "workflow",
							itemId: p_oItem.workflowId,
							mode: "create",
							submitType: "json",
							formId: "workflow-form",
							showCancelButton: true
						};
					var responseHandler = function(response) {
							document.location.href = document.location.href;
						}
					var me = this;
                    LogicECM.CurrentModules = LogicECM.CurrentModules || {};
					LogicECM.CurrentModules.WorkflowForm = new Alfresco.module.SimpleDialog("workflow-form").setOptions({
						width: formWidth,
						templateUrl: templateUrl,
						templateRequestParams: templateRequestParams,
						actionUrl: null,
						destroyOnHide: true,
						doBeforeDialogShow: {
							scope: this,
							fn: function(p_form, p_dialog) {
								p_dialog.dialog.setHeader(this.msg("logicecm.workflow.runAction.label", p_oItem.label));
								var contId = p_dialog.id + "-form-container";
								Dom.addClass(contId, "metadata-form-edit");
								Dom.addClass(contId, "no-form-type");

								this.doubleClickLock = false;

								p_dialog.dialog.subscribe('destroy', LogicECM.module.Base.Util.formDestructor, {moduleId: p_dialog.id}, this);
							}
						},
						onSuccess: {
							scope: this,
							fn: responseHandler
						}
					}).show();
				}
			}
		},

		clearOperationsList: function clearOperationsListFunction() {
			var button = this.toolbarButtons["defaultActive"].groupActionsButton;
			var menu = button.getMenu();
			if (YAHOO.util.Dom.inDocument(menu.element)) {
				menu.clearContent();
				menu.render();
			}
		},

		_actionResponse: function actionResponseFunction(actionId, response) {

			Bubbling.fire("datagridRefresh");
            Bubbling.fire("armRefreshSelectedTreeNode"); // обновить ветку в дереве

            Alfresco.util.PopupManager.displayMessage({
				text: Alfresco.util.message('lecm.os.msg.action') + ' "' + actionId + '" ' + Alfresco.util.message('lecm.os.msg.completed')
			});
		},

		_openMessageWindow: function openMessageWindowFunction(title, message, reload) {
			Alfresco.util.PopupManager.displayPrompt(
				{
					title: Alfresco.util.message('lecm.os.msg.operation.result') + " \"" + title + "\"",
					text: message,
					noEscape: true,
					buttons: [
						{
							text: Alfresco.util.message('lecm.os.btn.ok'),
							handler: function dlA_onAction_action()
							{
								this.destroy();
								if (reload) {
									document.location.href = document.location.href;
								}
							}
						}]
				});
		},

		onNewRow: function(e, target, itemType) {
			if (this.node != null) {
				if (itemType == null) {
					itemType = this.node.itemType;
				}
				if (this.editDialogOpening) {
					return;
				}

				if (itemType == "cm:folder") {
					if (target == this.toolbarButtons["defaultActive"].newRowButton) {
						itemType = "lecm-os:nomenclature-case";
					} else {
						itemType = "lecm-os:nomenclature-unit-section";
					}
				}

				this.editDialogOpening = true;

				var templateUrl = Alfresco.constants.URL_SERVICECONTEXT + "lecm/components/form";
				var templateRequestParams = {
					itemKind: "type",
					itemId: itemType,
					destination: this.node.nodeRef,
					mode: "create",
					formId: "",
					submitType: "json",
					showCancelButton: true
				};

				// Using Forms Service, so always create new instance
				var createDialog = new Alfresco.module.SimpleDialog(this.id + "-createDetails");
				createDialog.setOptions({
					width: "50em",
					templateUrl: templateUrl,
					templateRequestParams: templateRequestParams,
//					actionUrl: null,
					destroyOnHide: true,
					doBeforeDialogShow: {
						fn: function(p_form, p_dialog) {
							p_dialog.dialog.setHeader(this.msg("label.create-row.title"));
							this.editDialogOpening = false;
							p_dialog.dialog.subscribe('destroy', LogicECM.module.Base.Util.formDestructor, {moduleId: p_dialog.id}, this);
						},
						scope: this
					},
					doBeforeFormSubmit: {
						scope: this,
						fn: function() {
								var sortField = document.getElementsByName('prop_os-aspects_sort-value')[0];
								var unitIndexField = document.getElementsByName('prop_lecm-os_nomenclature-unit-section-index');
								var caseIndexField = document.getElementsByName('prop_lecm-os_nomenclature-case-index');
								var commonIndexField = document.getElementsByName('prop_os-aspects_common-index')[0];

								if(unitIndexField && unitIndexField.length) {
									sortField.value = 'a' + unitIndexField[0].value;
									commonIndexField.value = unitIndexField[0].value;
									return;
								}

								if(caseIndexField && caseIndexField.length) {
									sortField.value = 'b' + caseIndexField[0].value;
									commonIndexField.value = caseIndexField[0].value;
									return;
								}

						}
					},
					onSuccess: {
						fn: function(response) {
							Bubbling.fire("addTreeItem", {
								nodeRef: response.json.persistedObject
							});
                            Bubbling.fire("dataItemCreated", // обновить данные в гриде
								{
									nodeRef: response.json.persistedObject,
									bubblingLabel: this.options.bubblingLabel,
									itemType: this.node.itemType
								});
                            Bubbling.fire("armRefreshSelectedTreeNode"); // обновить ветку в дереве
                            Alfresco.util.PopupManager.displayMessage({
								text: this.msg("message.save.success")
							});
							this.createBJRecord(response.json.persistedObject, "ADD");
							this.editDialogOpening = false;
						},
						scope: this
					},
					onFailure: {
						fn: function(response) {
							Alfresco.util.PopupManager.displayMessage({
								text: this.msg("message.save.failure")
							});
							this.editDialogOpening = false;
						},
						scope: this
					}
				});
				createDialog.show();
			}
		},

		onItemCreated: function(layer, args) {
			if(LogicECM.Nomenclature.isCentralized) {
				return;
			}
			var object = args[1];
			var nodeRef = object.nodeRef;
			if(object.itemType == 'lecm-os:nomenclature-year-section') {
				Alfresco.util.PopupManager.displayPrompt({
					title:'Дополнительное действие',
					text: 'Сформировать разделы номенклатуры по организационной структуре?',
					buttons:[
						{
							text:'Да',
							handler: {
								obj: {
									context: this,
									yearNodeRef: nodeRef
								},
								fn: function(event, obj) {
									Alfresco.util.Ajax.jsonRequest({
										method: Alfresco.util.Ajax.GET,
										url: Alfresco.constants.PROXY_URI + "lecm/os/nomenclature/generateSections?yearRef=" + obj.yearNodeRef,
										successCallback: {
											fn: function() {
				                                Bubbling.fire("armRefreshSelectedTreeNode"); // обновить ветку в дереве
											}
										},
										execScripts: true
									});
	                                this.destroy();
								}
							}
						},
						{
							text:'Нет',
							handler:function DataGridActions__onActionDelete_cancel() {
								this.destroy();
							}
						}
					]
				});
			}
		},

		onDeleteNode: function() {
			if (this.node != null) {
				var me = this;

				var fnActionDeleteConfirm = function(nodeRef) {
					Alfresco.util.Ajax.jsonRequest({
						method: Alfresco.util.Ajax.POST,
						url: Alfresco.constants.PROXY_URI + "lecm/base/action/delete?full=true&trash=false&alf_method=delete",
						dataObj: {
							nodeRefs: [nodeRef]
						},
						responseContentType: Alfresco.util.Ajax.JSON,
						successCallback: {
							fn: function() {
								Bubbling.fire("deleteSelectedTreeItem");
                                Bubbling.fire("armRefreshSelectedTreeNode"); // обновить ветку в дереве
                                Alfresco.util.PopupManager.displayMessage({
									text: me.msg("message.delete.success")
								});
							}
						},
						failureMessage: "message.delete.failure",
						execScripts: true
					});
				};

				Alfresco.util.PopupManager.displayPrompt({
					title: this.msg("message.confirm.delete.title"),
					text: this.msg("message.confirm.delete.description"),
					buttons: [{
							text: this.msg("button.delete"),
							handler: function() {
								this.destroy();
								fnActionDeleteConfirm.call(me, me.node.nodeRef);
							}
						}, {
							text: this.msg("button.cancel"),
							handler: function() {
								this.destroy();
							},
							isDefault: true
						}]
				});
			}
		},

		createBJRecord: function(nodeRef, evType) {
			var logText = '#initiator создал элемент номенклатуры дел.';
			Alfresco.util.Ajax.jsonRequest({
				method: 'POST',
				url: Alfresco.constants.PROXY_URI + 'lecm/business-journal/api/record/create',
				dataObj: {
					mainObject: nodeRef,
					description: logText,
					category: evType
				},
				failureMessage: this.msg('message.failure'),
				scope: this
			});
		},

		loadSelectedTreeNode: function() {

			if (this.options.isRoot) {
				Alfresco.util.Ajax.request({
					method: "GET",
					url: Alfresco.constants.PROXY_URI_RELATIVE + 'lecm/os/nomenclature/getNomenclatureFolder',
					successCallback: {
						scope: this,
						fn: function(response) {
							if (response) {
								this.node = {
									nodeRef: response.json.nodeRef,
									itemType: response.json.itemType,
									currentItemType: response.json.type
								};
								this.updateDatagridAndButtons();
							}
						}
					},
					failureMessage: "message.failure",
					execScripts: true,
					scope: this
				});
			} else if (this.options.armSelectedNodeRef != null) {
				this.getCurrentNodeCasesToDestroy();
				Alfresco.util.Ajax.jsonRequest({
					method: 'GET',
					url: Alfresco.constants.PROXY_URI + 'api/metadata?nodeRef=' + this.options.armSelectedNodeRef + "&shortQNames",
					successCallback: {
						scope: this,
						fn: function(response) {
							var props = response.json.properties,
								type = response.json.type,
								allowedStatuses = ["PROJECT", "APPROVED"],
								status;

							switch(type){
								case "lecm-os:nomenclature-year-section":
									status = props["lecm-os:nomenclature-year-section-status"];
									break;

								case "lecm-os:nomenclature-unit-section":
									status = props["lecm-os:nomenclature-unit-section-status"];
									break;
							}

							this.node = {
								nodeRef: this.options.armSelectedNodeRef,
								itemType: props["lecm-dic:valueContainsType"],
								currentItemType: type
							};

							var disable = (status ? (allowedStatuses.indexOf(status) < 0) : false);

							this.toolbarButtons["defaultActive"].newRowButton.set("disabled", disable);
							this.toolbarButtons["defaultActive"].newRowButtonAdditional.set("disabled", disable);

							this.updateDatagridAndButtons();
						}
					},
					failureMessage: this.msg('message.failure'),
					scope: this,
					execScripts: true
				});
			}
		},
		updateDatagridAndButtons: function() {
			Bubbling.fire("activeGridChanged",
				{
					datagridMeta: {
						itemType: this.node.itemType,
						currentItemType: this.node.currentItemType,
						recreate: true,
						sort: "os-aspects:sort-value",
						nodeRef: this.node.nodeRef
					},
					bubblingLabel: this.options.bubblingLabel,
					scrollTo: true
				});

			if(this.node.itemType == 'cm:folder'){
				this.toolbarButtons["defaultActive"].newRowButtonAdditional.set("label", Alfresco.util.message('lecm.os.lbl.add.depart.section'));
				this.toolbarButtons["defaultActive"].newRowButtonAdditional.setStyle("display", "inline-block");
				this.toolbarButtons["defaultActive"].newRowButton.set("label", Alfresco.util.message('lecm.os.lbl.add.nomen.doc'));
			} else {
				this.toolbarButtons["defaultActive"].newRowButtonAdditional.setStyle("display", "none");
				this.toolbarButtons["defaultActive"].newRowButton.set("label", Alfresco.util.message('lecm.os.lbl.add') + " " + this.getTypeName(this.node.itemType));
			}

			// this.toolbarButtons["defaultActive"].deleteNodeButton.set("label", Alfresco.util.message('lecm.os.lbl.remove.selected') + " " + this.getTypeName(this.node.currentItemType));

			// this.toolbarButtons["defaultActive"].deleteNodeButton.set("disabled", this.node.itemType != "lecm-dic:dictionary");

			this.toolbarButtons["defaultActive"].exportButton.set("disabled", this.node.currentItemType != "lecm-dic:dictionary");
			this.toolbarButtons["defaultActive"].importButton.set("disabled", this.node.currentItemType != "lecm-dic:dictionary");
		},
		getTypeName: function(type) {
			if (type == "lecm-os:nomenclature-year-section") {
				return Alfresco.util.message('lecm.os.lbl.annual.section');
			} else if (type == "lecm-os:nomenclature-unit-section") {
				return Alfresco.util.message('lecm.os.lbl.management.section');
			}
			return Alfresco.util.message('lecm.os.lbl.element');
		},
		onExport: function() {
			if (this.node != null) {
				document.location.href = Alfresco.constants.PROXY_URI + "lecm/dictionary/get/export?nodeRef=" + this.node.nodeRef;
			}
		},
		showImportDialog: function() {
            if (!this.importFromSubmitButton) {
                this.importFromSubmitButton = Alfresco.util.createYUIButton(this, "import-form-submit", this.onImportXML, {
                    disabled: true
                });
            }
			Dom.get(this.id + "-import-form-chbx-ignore").checked = false;
			Dom.get(this.id + "-import-form-import-file").value = "";
			Dom.removeClass(this.importFromDialog.id, "hidden1");
            if (this.serviceRoot == null) {
                Alfresco.util.Ajax.request({
                    url: Alfresco.constants.PROXY_URI + 'lecm/operative-storage/serviceRoot',
                    successCallback: {
                        scope: this,
                        fn: function(response) {
                            var oResults = JSON.parse(response.serverResponse.responseText);

                            if (oResults && oResults.nodeRef) {
                                this.serviceRoot = oResults.nodeRef;
                                this.importFromDialog.show();
                            }
                        }
                    },
                    failureMessage: 'message.failure'
                });
            } else {
                this.importFromDialog.show();
            }
		},
		onImportXML: function() {
			var me = this;
			YAHOO.util.Connect.setForm(this.id + '-import-xml-form', true);
			var url = Alfresco.constants.URL_CONTEXT + "proxy/alfresco/lecm/dictionary/post/import?nodeRef=" + this.serviceRoot;
			var callback = {
				upload: function(oResponse) {
					var oResults = YAHOO.lang.JSON.parse(oResponse.responseText);
					Bubbling.fire("itemsListChanged");
					if (oResults[0] != null && oResults[0].text != null) {
						Dom.get(me.id + "-import-info-form-content").innerHTML = oResults[0].text;
						Dom.removeClass(me.importInfoDialog.id, "hidden1");
						me.importInfoDialog.show();
					} else if (oResults.exception != null) {
						Dom.get(me.id + "-import-error-form-exception").innerHTML = oResults.exception.replace(/\n/g, '<br>').replace(/\r/g, '<br>');
						Dom.get(me.id + "-import-error-form-stack-trace").innerHTML = me.getStackTraceString(oResults.callstack);
						Dom.setStyle(me.id + "-import-error-form-more", "display", "none");
						Dom.removeClass(me.importErrorDialog.id, "hidden1");
						me.importErrorDialog.show();
					}
				}
			};
			this.hideImportDialog();
			YAHOO.util.Connect.asyncRequest(Alfresco.util.Ajax.POST, url, callback);
									}
	}, true);
})();