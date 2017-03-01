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

(function()
{
	var Dom = YAHOO.util.Dom;
	var Bubbling = YAHOO.Bubbling;

	LogicECM.module.MeetingsDocumentTable = function (fieldHtmlId)
	{
		LogicECM.module.MeetingsDocumentTable.superclass.constructor.call(this, "LogicECM.module.MeetingsDocumentTable", fieldHtmlId, [ "container", "datasource"]);
        YAHOO.Bubbling.on("initDatagrid", this.onInitDataGrid, this);
		return this;
	};

	YAHOO.extend(LogicECM.module.MeetingsDocumentTable, Alfresco.component.Base,
		{
			options: {
				currentValue: null,
				bubblingLabel: "custom",
				toolbarId: null,
				containerId: null,
				datagridFormId: "datagrid",
				attributeForShow: "",
				disabled: null,
				messages: null,
				mode: null,
				isTableSortable: null,
				sort: null,
                externalCreateId: null,
                refreshAfterCreate: false,
                showActions: true,
				deleteMessageFunction: null,
				editFormTitleMsg: "label.edit-row.title",
				createFormTitleMsg: "label.create-row.title",
                viewFormTitleMsg: "logicecm.view",
                expandable: false,
                expandDataSource: "components/form",
				isInitOrSec: false
			},

            datagrid: null,

			tableData: null,

			onReady: function() {
				var item = this.options.currentValue;
				if (item) {
					var nodeUUID = item.replace("workspace://SpacesStore/","");
					Alfresco.util.Ajax.request(
						{
							method: "GET",
							url: Alfresco.constants.PROXY_URI + "slingshot/doclib/node/workspace/SpacesStore/" + nodeUUID,
							successCallback: {
								fn: this.checkPermissions,
								scope: this
							},
							failureMessage: this.msg("message.failure"),
							scope: this,
							execScripts: false
						});
				}
			},

			checkPermissions: function (response) {
				var res = response.json;
				if (res && res.item && res.item.permissions && res.item.permissions.userAccess) {
					if (!res.item.permissions.userAccess["delete"]) {
						this.options.disabled = true;
					}
				}
				this.loadTableData();
			},

            // инициализация грида
            onInitDataGrid: function(layer, args) {
                var datagrid = args[1].datagrid;
                if ((!this.options.bubblingLabel || !datagrid.options.bubblingLabel) || this.options.bubblingLabel == datagrid.options.bubblingLabel) {
                    this.dataGrid = datagrid;
                    YAHOO.Bubbling.unsubscribe("initDatagrid", this.onInitDataGrid, this);
                }
            },

            /**
             * New Row button click handler
             */
            onNewRow: function(e, p_obj) {
                var orgMetadata = this.dataGrid.datagridMeta;
                if (orgMetadata != null && orgMetadata.nodeRef.indexOf(":") > 0) {
                    var destination = orgMetadata.nodeRef;
                    var itemType = orgMetadata.itemType;
                    this.dataGrid.showCreateDialog({itemType: itemType, nodeRef: destination});
                }
            },

			loadTableData: function() {
				if (this.options.currentValue != null && this.options.currentValue.length > 0) {
					var sUrl = sUrl = Alfresco.constants.PROXY_URI + "/lecm/document/tables/api/getData?nodeRef=" + encodeURIComponent(this.options.currentValue);
					Alfresco.util.Ajax.jsonGet(
						{
							url: sUrl,
							successCallback: {
								fn: function (response) {
									this.tableData = response.json;
									this.createToolbar();
									//узнаем: 
									//-согласована повестка или нет;
									//-является ли текущий пользователь Инициатором или Секретарем	
									Alfresco.util.Ajax.jsonRequest(
									{
										url: Alfresco.constants.PROXY_URI + "/lecm/meetings/createSiteAction/check",
										method: "POST",
										dataObj:
										{
											dataTableRef: this.options.currentValue
										},
										successCallback:
										{
											fn: function(response){
												this.isInitOrSec = response.json.isInitOrSec === "true" ? true : false;
												this.createDataGrid();
												this.externalCreateButton();
											},
											scope: this
										},
										failureCallback:
										{
											fn: function(){},
											scope: this
										}
									});
								},
								scope: this
							},
							failureMessage: "message.failure"
						});
				}
			},

			createToolbar: function() {
				if (this.tableData != null && this.tableData.rowType != null && this.options.toolbarId != null && this.options.mode=="edit") {
					new LogicECM.module.Base.Toolbar(null, this.options.toolbarId).setMessages(this.options.messages).setOptions({
						bubblingLabel: this.options.bubblingLabel,
						itemType: this.tableData.rowType,
						destination: this.tableData.nodeRef,
						newRowButtonType: this.options.disabled ? "inActive" : "defaultActive"
					});
				}
			},

            externalCreateButton: function() {
                if (this.options.externalCreateId != null && this.options.externalCreateId != "") {
                    YAHOO.util.Event.on(this.options.externalCreateId, "click", this.onNewRow, this, true);
                }
            },

			createDataGrid: function() {
				if (this.tableData != null && this.tableData.rowType != null) {
					var actions = [];
					var actionType = "datagrid-action-link-" + this.options.bubblingLabel;
					if (!this.options.disabled && this.options.mode=="edit") {
						actions.push({
							type: actionType,
							id: "onActionEdit",
							permission: "edit",
							label: this.msg("actions.edit")
						});
						actions.push({
							type: actionType,
							id: "onActionDelete",
							permission: "delete",
							label: this.msg("actions.delete-row")
						});
					}
                    var splitActionAt = actions.length;

                    if (!this.options.isTableSortable && this.options.showActions && this.options.mode=="edit" && !this.options.disabled) {
                        var otherActions = [];
                        otherActions.push({
                            type: actionType,
                            id: "onMoveTableRowUp",
                            permission: "edit",
                            label: this.msg("actions.tableRowUp")
                        });
                        otherActions.push({
                            type: actionType,
                            id: "onMoveTableRowDown",
                            permission: "edit",
                            label: this.msg("action.tableRowDown")
                        });
                        otherActions.push({
                            type: actionType,
                            id: "onAddRow",
                            permission: "edit",
                            label: this.msg("action.addRow")
                        });
					
						if (this.isInitOrSec){
							otherActions.push({
								type: actionType,
								id: "onEditWorkspace",
								permission: "edit",
								label: this.msg("action.edit.workspace")
							});
						}

                        actions = actions.concat(otherActions);
                        splitActionAt = actions.length;
                    }

					var datagrid = new LogicECM.module.MeetingsDocumentTableDataGrid(this.options.containerId).setOptions({
						usePagination: true,
						showExtendSearchBlock: false,
						formMode: this.options.mode,
						actions: actions,
                        splitActionsAt: 3,
						datagridMeta: {
							useFilterByOrg: false,
							itemType: this.tableData.rowType,
							datagridFormId: this.options.datagridFormId,
							createFormId: "",
							nodeRef: this.tableData.nodeRef,
							actionsConfig: {
								fullDelete: true,
                                trash: false
					        },
							sort: this.options.sort ? this.options.sort : "lecm-document:indexTableRow",
							useChildQuery: true
						},
						bubblingLabel: this.options.bubblingLabel,
						showActionColumn: this.options.showActions,
						showOtherActionColumn: true,
						showCheckboxColumn: false,
						attributeForShow: this.options.attributeForShow,
						pageSize: this.tableData.pageSize != null && this.tableData.pageSize > 0 ? this.tableData.pageSize : 10,
                        useCookieForSort: false,
                        overrideSortingWith: this.options.isTableSortable,
                        refreshAfterCreate: this.options.refreshAfterCreate,
						editFormTitleMsg: this.options.editFormTitleMsg,
						createFormTitleMsg: this.options.createFormTitleMsg,
						viewFormTitleMsg: this.options.viewFormTitleMsg,
                        expandable: this.options.expandable,
                        expandDataSource: this.options.expandDataSource
					}).setMessages(this.options.messages);
				}

                if (this.tableData != null) {
                    datagrid.tableDataNodeRef = this.tableData.nodeRef;
                }
				datagrid.deleteMessageFunction = this.options.deleteMessageFunction;
				datagrid.draw();
            }
		});
})();

LogicECM.module.MeetingsDocumentTableDataGrid = LogicECM.module.MeetingsDocumentTableDataGrid || {};

(function () {

	var Dom = YAHOO.util.Dom;
	var Bubbling = YAHOO.Bubbling;

	LogicECM.module.MeetingsDocumentTableDataGrid = function (htmlId) {
		return LogicECM.module.MeetingsDocumentTableDataGrid.superclass.constructor.call(this, htmlId);
	};

	/**
	 * Extend from LogicECM.module.Base.DataGrid
	 */
	YAHOO.lang.extend(LogicECM.module.MeetingsDocumentTableDataGrid, LogicECM.module.DocumentTableDataGrid);

	/**
	 * Augment prototype with main class implementation, ensuring overwrite is enabled
	 */
	YAHOO.lang.augmentObject(LogicECM.module.MeetingsDocumentTableDataGrid.prototype, {
		editWorkspaceDialogOpening: false,
		
		tableDataNodeRef: null,

		deleteMessageFunction: null,

        doubleClickLock: false,

		onDataItemCreated:function (layer, args) {
			LogicECM.module.MeetingsDocumentTableDataGrid.superclass.onDataItemCreated.call(this, layer, args);
			var obj = args[1];
            if (obj && this._hasEventInterest(obj.bubblingLabel) && obj.nodeRef) {
				YAHOO.Bubbling.fire("formValueChanged", {
					eventGroup: this,
					addedItems: [],
					removedItems: [],
					selectedItems: [],
					selectedItemsMetaData: {}
				});
			}
		},

		onDataItemsDeleted: function (layer, args) {
			LogicECM.module.MeetingsDocumentTableDataGrid.superclass.onDataItemsDeleted.call(this, layer, args);
			var obj = args[1];
			if (obj && this._hasEventInterest(obj.bubblingLabel) && obj.items) {
				YAHOO.Bubbling.fire("formValueChanged", {
					eventGroup: this,
					addedItems: [],
					removedItems: [],
					selectedItems: [],
					selectedItemsMetaData: {}
				});
			}
		},

        /**
         * Fired by YUI when parent element is available for scripting
         *
         * @method onReady
         */
		onReady: function DataGrid_onReady() {
			LogicECM.module.MeetingsDocumentTableDataGrid.superclass.onReady.call(this);
		},

		/**
		 * Добавляет меню для колонок
		 */
		setupActions: function() {
			var moreActionsDiv = document.getElementById(this.id + '-moreActions');
			var actionMoreDiv, actionMoreA;
			if (moreActionsDiv) {
				actionMoreDiv = moreActionsDiv.children[0];
				if (actionMoreDiv) {
					actionMoreA = actionMoreDiv.children[0];
					if (actionMoreA) {
						actionMoreA.className = 'show-more show-more' + (this.options.bubblingLabel ? "-" + this.options.bubblingLabel : "");
					}
				}
			}

			var onSetupActions = function onSetupActions(actions, id, className) {
				var actionsDiv = document.getElementById(id);
				if (actionsDiv.children.length == 0) {
					for (var i = 0; i < actions.length; i++) {
						var action = actions[i];

						var actionDiv = document.createElement("div");
						actionDiv.className = action.id;

						var actionA = document.createElement("a");
						actionA.rel = action.permission;
						actionA.className = className + action.type;
						actionA.title = action.label;

						var actionSpan = document.createElement("span");
						actionSpan.innerHTML = action.label;

						actionA.appendChild(actionSpan);
						actionDiv.appendChild(actionA);
						actionsDiv.appendChild(actionDiv);
					}
				}
			};
			if (this.options.actions) {
				onSetupActions(this.options.actions, this.id + "-actionSet","datagrid-action-link ");
			}

			if (this.options.otherActions && this.options.otherActions.length) {
				onSetupActions(this.options.otherActions, this.id + "-otherActionSet","datagrid-other-action-link ");
			}
		},

        onMoveTableRowUp: function (me, asset, owner, actionsConfig, confirmFunction) {
            Alfresco.util.Ajax.jsonRequest(
                {
                    method: Alfresco.util.Ajax.GET,
                    url: Alfresco.constants.PROXY_URI + "lecm/document/tables/api/moveTableRowUp?nodeRef=" + arguments[0].nodeRef,
                    successCallback: {
                        fn: function (response) {
                            if (response.json. isMoveUp == "true") {
                                var me = response.config.scope;
                                var rowId = response.serverResponse.argument.config.rowId;

                                var oDataRecord1 = me.widgets.dataTable.getRecord(rowId);
                                var index = me.widgets.dataTable.getRecordIndex(oDataRecord1);

                                if (index > 0) {
                                    var oDataRecord2 = me.widgets.dataTable.getRecord(index-1);

                                    // удаляем верхнюю запись(Если она осталась на другой странице, не страшно)
                                    me.widgets.dataTable.deleteRow(oDataRecord2);
                                    //сначала добавляем запись с которой обменялись, т.к. если на странице не остаётся записей, скрипт падает.
                                    me.widgets.dataTable.addRow(response.json.secondItem, index);
                                    //удаляем "исходную" запись
                                    me.widgets.dataTable.deleteRow(oDataRecord1);

                                    if (index % me.widgets.dataTable.configs.paginator.getRowsPerPage() !== 0) {
                                        //если запись не самая верхняя, добавляем ее
                                        me.widgets.dataTable.addRow(oDataRecord1.getData(), index-1);
                                    }

									Bubbling.fire("datagridRefresh",
		                            {
			                            bubblingLabel:me.options.bubblingLabel
		                            });
                                }
                            }
                        }
                    },
                    failureCallback: {
                        fn:function DataGrid_onDataItemCreated_refreshFailure(response) {
                            var me = response.config.scope;
                            Alfresco.util.PopupManager.displayMessage(
                                {
                                    text:me.msg("message.details.failure")
                                });
                        }
                    },
                    scope: this,
                    rowId: asset.id

                });
        },
        onMoveTableRowDown: function (me, asset, owner, actionsConfig, confirmFunction) {
            Alfresco.util.Ajax.jsonRequest(
                {
                    method: Alfresco.util.Ajax.GET,
                    url: Alfresco.constants.PROXY_URI + "lecm/document/tables/api/moveTableRowDown?nodeRef=" + arguments[0].nodeRef,
                    successCallback: {
                        fn: function (response) {
                            if (response.json.isMoveDown == "true") {
                                var me = response.config.scope;
                                var rowId = response.serverResponse.argument.config.rowId;
                                var oDataRecord1 = me.widgets.dataTable.getRecord(rowId);
                                var index = me.widgets.dataTable.getRecordIndex(oDataRecord1);

                                var count = me.widgets.dataTable.getRecordSet()._records.length;
                                if (index < count) {
                                    var oDataRecord2 = me.widgets.dataTable.getRecord(index+1);

                                    me.widgets.dataTable.deleteRow(oDataRecord2);
                                    me.widgets.dataTable.addRow(response.json.secondItem, index);

                                    me.widgets.dataTable.deleteRow(oDataRecord1);
                                    me.widgets.dataTable.addRow(oDataRecord1.getData(), index+1);

									Bubbling.fire("datagridRefresh",
		                            {
			                            bubblingLabel:me.options.bubblingLabel
		                            });
                                }
                            }
                        }
                    },
                    failureCallback: {
                        fn:function DataGrid_onDataItemCreated_refreshFailure(response) {
                            var me = response.config.scope;
                            Alfresco.util.PopupManager.displayMessage(
                                {
                                    text:me.msg("message.details.failure")
                                });
                        }
                    },
                    scope: this,
                    rowId: asset.id

				});
		},

		onDelete_Prompt: function(fnAfterPrompt,me,items,itemsString){
			var text;
			if (this.deleteMessageFunction != null) {
				text = this._executeFunctionByName(this.deleteMessageFunction, items, itemsString);
			} else {
				text = (items.length > 1) ? this.msg("message.confirm.delete.group.description", items.length) : this.msg("message.confirm.delete.description", itemsString);
			}

			Alfresco.util.PopupManager.displayPrompt(
				{
					title:this.msg("message.confirm.delete.title", items.length),
					text: text,
					buttons:[
						{
							text:this.msg("button.delete"),
							handler:function DataGridActions__onActionDelete_delete() {
								this.destroy();
								me.selectItems("selectNone");
								fnAfterPrompt.call(me, items);
												YAHOO.Bubbling.fire("formValueChanged", {
												eventGroup: this,
												addedItems: [],
												removedItems: [],
												selectedItems: [],
												selectedItemsMetaData: {}
											});

							}
						},
						{
							text:this.msg("button.cancel"),
							handler:function DataGridActions__onActionDelete_cancel() {
								this.destroy();
							},
							isDefault:true
						}
					]
				});
		},
		
		showEditWorkspaceDialog: function DataGrid_showEditWorkspaceDialog(item) {
			var templateUrl = Alfresco.constants.URL_SERVICECONTEXT + "lecm/components/form";
			var templateRequestParams = {
				itemKind: "node",
				itemId: item.nodeRef,
				mode: "edit",
				submitType: "json",
				showCancelButton: true,
				showCaption: false
			};

			templateRequestParams.formId = 'editWorkspace';
			

			var editDetails = new Alfresco.module.SimpleDialog(this.id + "-editWorkspaceDetails");
                editDetails.setOptions(
                    {
                        width: this.options.editFormWidth,
                        templateUrl:templateUrl,
	                    templateRequestParams:templateRequestParams,
                        actionUrl:null,
                        destroyOnHide:true,
                        doBeforeDialogShow:{
                            fn: function(p_form, p_dialog) {
                                var contId = p_dialog.id + "-form-container";
                                if (item.type && item.type != "") {
                                    Dom.addClass(contId, item.type.replace(":", "_") + "_edit");
                                }
								p_dialog.dialog.setHeader(this.msg("dialog.title.edit.workspace"));
								this.editWorkspaceDialogOpening = false;

	                            p_dialog.dialog.subscribe('destroy', LogicECM.module.Base.Util.formDestructor, {moduleId: p_dialog.id}, this);
                            },
                            scope:this
                        },
                        onSuccess:{
                            fn:function DataGrid_onActionWorkspaceEdit_success(response) {
	                            this.editWorkspaceDialogOpening = false;
								
								var newWorkspace = response.config.dataObj["lecm-meetings-ts_new-workspace"];
								
								Alfresco.util.Ajax.jsonRequest(
								{
									url: Alfresco.constants.URL_SERVICECONTEXT + "/lecm/meetings/create-site",
									method: "POST",
									dataObj:
									{
										agendaItem: item.nodeRef,
										newWorkspace: newWorkspace === "true"
									},
									successCallback:
									{
										fn: function(){
											Alfresco.util.PopupManager.displayMessage({
												text:this.msg("message.details.success")
											});
											this._itemUpdate(item.nodeRef);
										},
										scope: this
									},
									failureCallback:
									{
										fn: function(){
											Alfresco.util.PopupManager.displayMessage(
											{
												text:this.msg("message.edit.workspace.details.failure")
											});											
										},
										scope: this
									}
								});
                            },
                            scope:this
                        },
                        onFailure:{
                            fn:function DataGrid_onActionWorkspaceEdit_failure(response) {
                                Alfresco.util.PopupManager.displayMessage(
                                    {
                                        text:this.msg("message.edit.workspace.details.failure")
                                    });
	                            this.editWorkspaceDialogOpening = false;
                            },
                            scope:this
                        }
                    }).show();
			
		},
		
		onEditWorkspace: function DataGrid_onEditWorkspace(item) {
			if (this.editWorkspaceDialogOpening) {
				return;
			}
			this.editWorkspaceDialogOpening = true;
			var me = this;
			
			Alfresco.util.Ajax.jsonRequest(
			{
				url: Alfresco.constants.PROXY_URI + "/lecm/meetings/createSiteAction/check",
				method: "POST",
				dataObj:
				{
					itemRef: item.nodeRef
				},
				successCallback:
				{
					fn: function(response){
						var isApproved = response.json.isApproved === "true";
						if (isApproved){
							this.showEditWorkspaceDialog(item);
						} else{
								Alfresco.util.PopupManager.displayPrompt(
								{
									title:this.msg("message.confirm.agenda.not.approved.title"),
									text: this.msg("message.confirm.agenda.not.approved"),
									buttons:[
										{
											text:this.msg("button.ok"),
											handler:function () {
												this.destroy();
												me.showEditWorkspaceDialog(item);
											}
										},
										{
											text:this.msg("button.cancel"),
											handler:function () {
												this.destroy();
												me.editWorkspaceDialogOpening = false;
											},
											isDefault:true
										}
									]
								});							
						}
					},
					scope: this
				},
				failureCallback:
				{
					fn: function(){
						Alfresco.util.PopupManager.displayMessage(
						{
							text:this.msg("message.edit.workspace.details.failure")
						});
						this.editWorkspaceDialogOpening = false;
					},
					scope: this
				}
			});
			
			this.editWorkspaceDialogOpening = false;
		}
	}, true)

})();


