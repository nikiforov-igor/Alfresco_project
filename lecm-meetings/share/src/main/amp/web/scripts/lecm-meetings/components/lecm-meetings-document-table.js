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

LogicECM.module.MeetingsDocumentTableDataGrid= LogicECM.module.MeetingsDocumentTableDataGrid  || {};

(function () {

	var Dom = YAHOO.util.Dom;
	var Bubbling = YAHOO.Bubbling;

	LogicECM.module.MeetingsDocumentTableDataGrid = function (htmlId) {
		return LogicECM.module.MeetingsDocumentTableDataGrid.superclass.constructor.call(this, htmlId);
	};

	/**
	 * Extend from LogicECM.module.Base.DataGrid
	 */
	YAHOO.lang.extend(LogicECM.module.MeetingsDocumentTableDataGrid, LogicECM.module.Base.DataGrid);

	/**
	 * Augment prototype with main class implementation, ensuring overwrite is enabled
	 */
	YAHOO.lang.augmentObject(LogicECM.module.MeetingsDocumentTableDataGrid.prototype, {
		editWorkspaceDialogOpening: false,
		
		tableDataNodeRef: null,

		deleteMessageFunction: null,

        doubleClickLock: false,

		addFooter: function() {
			if (this.tableDataNodeRef != null && (this.datagridMeta.searchConfig == null || this.datagridMeta.searchConfig.fullTextSearch == null)) {
				var sUrl = sUrl = Alfresco.constants.PROXY_URI + "/lecm/document/tables/api/getTotalRows?tableDataRef=" + encodeURIComponent(this.tableDataNodeRef);
				Alfresco.util.Ajax.jsonGet(
					{
						url: sUrl,
						successCallback: {
							fn: function (response) {
								var oResults = response.json;
								if (oResults != null && oResults.length > 0) {
									var row = oResults[0];
									var item = {
										itemData: {},
										nodeRef: row.nodeRef,
										type: "total"
									};

									var datagridColumns = this.datagridColumns;
									if (datagridColumns != null) {
										for (var i = 0; i < datagridColumns.length; i++) {
											var field = datagridColumns[i].name;
											var totalFields = Object.keys(row.itemData);
											for (var j = 0; j < totalFields.length; j++) {
												var totalField = totalFields[j];
												if (totalField.indexOf(field) == 0) {
													var value = row.itemData[totalField];
													item.itemData[datagridColumns[i].formsName] = {
														value: value,
														displayValue: value
													};
												}
											}
										}
									}

									this.widgets.dataTable.addRow(item);
								}
							},
							scope: this
						},
						failureCallback: {
							fn: function (oResponse) {

							},
							scope: this
						}
					});
			}
		},

		onDataItemCreated:function (layer, args) {
			var obj = args[1];
            if (obj && this._hasEventInterest(obj.bubblingLabel) && (obj.nodeRef !== null)) {
				
				if (!this.options.refreshAfterCreate) {
                    var nodeRef = new Alfresco.util.NodeRef(obj.nodeRef);
                    // Reload the node's metadata
                    Alfresco.util.Ajax.jsonPost(
                        {
                            url:Alfresco.constants.PROXY_URI + "lecm/base/item/node/" + nodeRef.uri,
                            dataObj:this._buildDataGridParams(),
                            successCallback:{
                                fn:function DataGrid_onDataItemCreated_refreshSuccess(response) {
                                    this.versionable = response.json.versionable;
                                    var item = response.json.item;
                                    var fnAfterUpdate = function DataGrid_onDataItemCreated_refreshSuccess_fnAfterUpdate() {
                                        var recordFound = this._findRecordByParameter(item.nodeRef, "nodeRef");
                                        if (recordFound !== null) {
                                            var el = this.widgets.dataTable.getTrEl(recordFound);
                                            Alfresco.util.Anim.pulse(el);
                                        }
                                    };
                                    this.afterDataGridUpdate.push(fnAfterUpdate);

                                    this.removeTotalRows();
                                    this.widgets.dataTable.addRow(item);
                                    this.addFooter();
                                },
                                scope:this
                            },
                            failureCallback:{
                                fn:function DataGrid_onDataItemCreated_refreshFailure(response) {
                                    Alfresco.util.PopupManager.displayMessage(
                                        {
                                            text:this.msg("message.create.refresh.failure")
                                        });
                                },
                                scope:this
                            }
                        });
                } else {
                    this.onDataGridRefresh(layer, args);
                }
				YAHOO.Bubbling.fire("formValueChanged", {
					eventGroup: this,
					addedItems: [],
					removedItems: [],
					selectedItems: [],
					selectedItemsMetaData: {}
				});	
			}
		},

		onDataItemsDeleted: function (layer, args)
		{
			var obj = args[1];
			if (obj && this._hasEventInterest(obj.bubblingLabel) && (obj.items !== null))
			{
				var recordFound, el,
					fnCallback = function(record)
					{
						return function DataGrid_onDataItemsDeleted_anim()
						{
							Bubbling.fire("datagridRefresh",
								{
									bubblingLabel:this.options.bubblingLabel
								});
						};
					};

				for (var i = 0, ii = obj.items.length; i < ii; i++)
				{
					recordFound = this._findRecordByParameter(obj.items[i].nodeRef, "nodeRef");
					if (recordFound !== null)
					{
						el = this.widgets.dataTable.getTrEl(recordFound);
						Alfresco.util.Anim.fadeOut(el,
							{
								callback: fnCallback(recordFound),
								scope: this
							});
					}
				}
				YAHOO.Bubbling.fire("formValueChanged", {
					eventGroup: this,
					addedItems: [],
					removedItems: [],
					selectedItems: [],
					selectedItemsMetaData: {}
				});
			}
		},

		removeTotalRows: function() {
			var records = this.widgets.dataTable.getRecordSet().getRecords();
			if (records != null) {
				for (var i = 0; i < records.length; i++) {
					if (records[i].getData("type") == "total") {
						this.widgets.dataTable.deleteRow(records[i]);
					}
				}
			}
		},

		getRowFormater: function () {
			var scope = this;

			return function (elTr, oRecord) {
				if (oRecord.getData("type") == "total") {
					YAHOO.util.Dom.addClass(elTr, 'total-row');
				}
				return true;
			}
		},

		getCustomCellFormatter: function (grid, elCell, oRecord, oColumn, oData) {
			var html = "";
			if (!oRecord) {
				oRecord = this.getRecord(elCell);
			}
			if (!oColumn) {
				oColumn = this.getColumn(elCell.parentNode.cellIndex);
			}

			if (oRecord && oColumn) {
				if (!oData) {
					oData = oRecord.getData("itemData")[oColumn.field];
				}

				if (oData) {
					var datalistColumn = grid.datagridColumns[oColumn.key];
					if (datalistColumn) {
						oData = YAHOO.lang.isArray(oData) ? oData : [oData];
						for (var i = 0, ii = oData.length, data; i < ii; i++) {
							data = oData[i];

                            //статус
                            if (datalistColumn.name.toLowerCase().indexOf("status") > -1) {
                                var tr = elCell.parentElement.parentElement;
                                var children = tr.children;
                                for (var i = 0; i < children.length; i++) {
                                    children[i].setAttribute("status", data.value);
                                }

                            }

							var columnContent = "";
							switch (datalistColumn.dataType.toLowerCase()) { //  меняем отрисовку для конкретных колонок
								case "cm:content":
									var fileIcon = Alfresco.util.getFileIcon(data.displayValue, "cm:content", 16);
									var fileIconHtml = "<img src='" + Alfresco.constants.URL_RESCONTEXT + "components/images/filetypes/" + fileIcon +"' width='16' height='16'/>";

									columnContent = "<a href='" + Alfresco.constants.URL_PAGECONTEXT+"document-attachment?nodeRef="+ data.value +"' title='" + data.displayValue + "'>" + fileIconHtml + data.displayValue + "</a>";
									break;
								case "cm:cmobject":
									columnContent = "<a href='javascript:void(0);' onclick=\"LogicECM.module.Base.Util.viewAttributes({itemId:\'" + data.value + "\', title: \'logicecm.view\'})\">" + data.displayValue + "</a>";
									break;
								case "st:site":
									var fileIcon = Alfresco.util.getFileIcon(data.displayValue, "st:sites", 16);
									var fileIconHtml = "<img src='" + Alfresco.constants.URL_RESCONTEXT + "components/images/filetypes/" + fileIcon +"' width='16' height='16'/>";
							
									var displayValues = data.displayValue.split(";");
									columnContent = "<a href='" + Alfresco.constants.URL_PAGECONTEXT+"site/"+ displayValues[0] + "/dashboard" +"' title='" + displayValues[1] + "'>" + fileIconHtml + "</a>";
									break;	
								default:
									break;
							}
							if (columnContent != "") {
								html += columnContent;

								if (i < ii - 1) {
									html += "<br />";
								}
							}
						}
					}
				}
			}
			return html.length > 0 ? html : null;  // возвращаем NULL чтобы выызвался основной метод отрисовки
		},
        /**
         * Fired by YUI when parent element is available for scripting
         *
         * @method onReady
         */
        onReady: function DataGrid_onReady()
        {
            var me = this;

            if (this.options.actions.length > this.showActionsCount) {
                this.showActionsCount = this.options.splitActionsAt;
            }
            this.splitActionsAtStore = this.options.splitActionsAt;

            if (this.options.showActionColumn){
                // Hook action events
                var fnActionHandler = function DataGrid_fnActionHandler(layer, args)
                {
                    var owner = Bubbling.getOwnerByTagName(args[1].anchor, "div");
                    if (owner !== null)
                    {
                        if (typeof me[owner.className] == "function")
                        {
                            args[1].stop = true;
                            var row = me.widgets.dataTable.getRecord(args[1].target.offsetParent);
                            if (row) {
                                var asset = row.getData();

                                var confirmFunction = null;
                                if (me.options.actions != null) {
                                    for (var i = 0; i < me.options.actions.length; i++) {
                                        if (me.options.actions[i].id == owner.className && me.options.actions[i].confirmFunction != null) {
                                            confirmFunction = me.options.actions[i].confirmFunction;
                                        }
                                    }
                                }

                                me[owner.className].call(me, asset, owner, me.datagridMeta.actionsConfig, confirmFunction);
                            }
                        }
                    }
                    return true;
                };
                Bubbling.addDefaultAction("datagrid-action-link" + (me.options.bubblingLabel ? "-"+ me.options.bubblingLabel : ""), fnActionHandler, me.options.forceSubscribing);
				Bubbling.addDefaultAction("show-more"  + (me.options.bubblingLabel ? "-"+ me.options.bubblingLabel : ""), fnActionHandler, me.options.forceSubscribing);
            }

            if (!this.options.overrideSortingWith && me.options.otherActions != null && me.options.otherActions.length > 0){
                // Hook action events
                var fnOtherActionHandler = function DataGrid_fnActionHandler(layer, args)
                {
                    var owner = Bubbling.getOwnerByTagName(args[1].anchor, "div");
                    if (owner !== null)
                    {
                        if (typeof me[owner.className] == "function")
                        {
                            args[1].stop = true;
                            var row = me.widgets.dataTable.getRecord(args[1].target.offsetParent);
                            if (row) {
                                var asset = row.getData();

                                var confirmFunction = null;
                                if (me.options.otherActions != null) {
                                    for (var i = 0; i < me.options.otherActions.length; i++) {
                                        if (me.options.otherActions[i].id == owner.className && me.options.otherActions[i].confirmFunction != null) {
                                            confirmFunction = me.options.otherActions[i].confirmFunction;
                                        }
                                    }
                                }

                                me[owner.className].call(me, asset, owner, me.datagridMeta.actionsConfig, confirmFunction);
                            }
                        }
                    }
                    return true;
                };
                Bubbling.addDefaultAction("datagrid-other-action-link" + (me.options.bubblingLabel ? "-"+ me.options.bubblingLabel : ""), fnOtherActionHandler, me.options.forceSubscribing);
				Bubbling.addDefaultAction("show-more"  + (me.options.bubblingLabel ? "-"+ me.options.bubblingLabel : ""), fnOtherActionHandler, me.options.forceSubscribing);
            }

            // Actions module
            this.modules.actions = new LogicECM.module.Base.Actions();

            // Reference to Data Grid component (required by actions module)
            this.modules.dataGrid = this;

            this.deferredListPopulation.fulfil("onReady");

            // Finally show the component body here to prevent UI artifacts on YUI button decoration
            Dom.setStyle(this.id + "-body", "visibility", "visible");

            Bubbling.fire("initDatagrid",
                {
                    datagrid:this
                });
        },
        /**
         * Получение колонок dataGrid
         * @return {Array} список колонок
         * @constructor
         */
        getDataTableColumnDefinitions:function DataGrid_getDataTableColumnDefinitions() {
            // YUI DataTable column definitions
            var columnDefinitions = [];
            if (this.options.expandable) {
                columnDefinitions.push({
                    key: "expand",
                    label: "",
                    sortable: false,
                    formatter: this.fnRenderCellExpand (),
                    width: 16
                });
            }
            if (this.options.showCheckboxColumn) {
                columnDefinitions.push({
                    key: "nodeRef",
                    label: "<input type='checkbox' id='" + this.id + "-select-all-records'>",
                    sortable: false,
                    formatter: this.fnRenderCellSelected (),
                    width: 16
                });
            }

            var inArray = function(value, array) {
                for (var i = 0; i < array.length; i++) {
                    if (array[i] == value) return true;
                }
                return false;
            };

            var column, sortable;
            for (var i = 0, ii = this.datagridColumns.length; i < ii; i++) {
                column = this.datagridColumns[i];

                if (this.options.overrideSortingWith === null) {
                    sortable = column.sortable;
                } else {
                    sortable = this.options.overrideSortingWith;
                }

                if (!(this.options.excludeColumns.length > 0 && inArray(column.name, this.options.excludeColumns))) {
                    var className = "";
                    if (column.dataType == "lecm-orgstr:employee" || (this.options.nowrapColumns.length > 0 && inArray(column.name, this.options.nowrapColumns))) {
                        className = "nowrap "
                    }

                    columnDefinitions.push({
                        key:this.dataResponseFields[i],
                        label:column.label.length > 0 ? column.label : this.msg(column.name.replace(":", "_")),
                        sortable:sortable,
                        sortOptions:{
                            field:column.formsName,
                            sortFunction:this.getSortFunction()
                        },
                        formatter:this.getCellFormatter(column.dataType),
                        className: className + ((column.dataType == 'boolean') ? 'centered' : '')
                    });
                }
            }
            if (this.options.showActionColumn){
                // Add actions as last column
                columnDefinitions.push(
                    { key:"actions", label:this.msg("label.column.actions"), sortable:false, formatter:this.fnRenderCellActions(), width: Math.round(26.7 * this.showActionsCount) }
                );
            }
            if (!this.options.overrideSortingWith && this.options.otherActions != null && this.options.otherActions.length > 0){
                // Add actions as last column
                columnDefinitions.push(
                    { key:"other-actions", label:"", sortable:false, formatter:this.fnRenderCellOtherActions(), width:80 }
                );
            }
            return columnDefinitions;
        },
        /**
         * Returns actions custom datacell formatter
         *
         * @method fnRenderCellActions
         */
        fnRenderCellOtherActions: function DataGrid_fnRenderCellActions()
        {
            var scope = this;

            /**
             * Actions custom datacell formatter
             *
             * @method renderCellActions
             * @param elCell {object}
             * @param oRecord {object}
             * @param oColumn {object}
             * @param oData {object|string}
             */
            return function DataGrid_renderCellOtherActions(elCell, oRecord, oColumn, oData)
            {
                Dom.setStyle(elCell, "width", oColumn.width + "px");
                Dom.setStyle(elCell.parentNode, "width", oColumn.width + "px");

                elCell.innerHTML = '<div id="' + scope.id + '-other-actions-' + oRecord.getId() + '" class="hidden"></div>';
            };
        },
        /**
         * Добавляет меню для колонок
         */
        setupActions: function() {
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
            }
            if (this.options.actions != null) {
                onSetupActions(this.options.actions, this.id + "-actionSet","datagrid-action-link ");
            }

            if (this.options.otherActions != null && this.options.otherActions.length > 0) {
                onSetupActions(this.options.otherActions, this.id + "-otherActionSet","datagrid-other-action-link ");
            }
        },
        /**
         * Custom event handler to highlight row.
         *
         * @method onEventHighlightRow
         * @param oArgs.event {HTMLEvent} Event object.
         * @param oArgs.target {HTMLElement} Target element.
         */
        onEventHighlightRow:function DataGrid_onEventHighlightRow(oArgs)
		{
			// Fix if oArgs.target == undefined
			if (!oArgs.target && oArgs.el) {
				oArgs.target = {};
				oArgs.target.id = oArgs.el.id;
			}

			// elActions is the element id of the active table cell where we'll inject the actions
            var elActions = Dom.get(this.id + "-actions-" + oArgs.target.id);

            this.onHighlightRowFunction(oArgs, elActions,(this.id + "-actionSet"), (this.id + "-otherMoreActions"));

            if (this.showingMoreActions) {
                this.deferredActionsMenu = elActions;
            }
            else {
                this.currentActionsMenu = elActions;
                // Show the actions
                Dom.removeClass(elActions, "hidden");
                this.deferredActionsMenu = null;
            }

            if (!this.options.overrideSortingWith){
                var elOtherActions = Dom.get(this.id + "-other-actions-" + oArgs.target.id);
                this.onHighlightRowFunction(oArgs, elOtherActions,(this.id + "-otherActionSet"), (this.id + "-otherMoreActions"));
                Dom.removeClass(elOtherActions, "hidden");
            }
        },
        onHighlightRowFunction: function(oArgs, elActions, actionSetId, moreActionId)
		{
            // Выбранный элемент
            var numSelectItem = this.widgets.dataTable.getTrIndex(oArgs.target);
            if (this.widgets.paginator) {
                numSelectItem = numSelectItem + ((this.widgets.paginator.getCurrentPage() - 1) * this.options.pageSize);
            }

            var selectItem = this.widgets.dataTable.getRecordSet().getRecord(numSelectItem);
            if (selectItem){
                var oData = selectItem.getData();
                this._showVersionLabel(oData.itemData, oArgs.target.id);
            }

            if ((oData != undefined) && (oData.type != "total")) {
                // Inject the correct action elements into the actionsId element
                if (elActions && !this.showingMoreActions) {
                    // Call through to get the row highlighted by YUI
                    this.widgets.dataTable.onEventHighlightRow.call(this.widgets.dataTable, oArgs);

                    // Clone the actionSet template node from the DOM
                    var record = this.widgets.dataTable.getRecord(oArgs.target.id),
                        clone = null;
                    if (elActions.firstChild === null){
                        clone = Dom.get(actionSetId).cloneNode(true);
                        // Token replacement
                        clone.innerHTML = YAHOO.lang.substitute(window.unescape(clone.innerHTML), this.getActionUrls(record));

                        // Generate an id
                        clone.id = elActions.id + "_a";

                        var actionsDivs = YAHOO.util.Selector.query("div", clone);
                        for (index = 0; index < actionsDivs.length; index++) {
                            var actionDiv = actionsDivs[index];
                            Dom.generateId(actionDiv, actionDiv.className + "-" + oArgs.target.id);
                        }

                        // Simple view by default
                        Dom.addClass(clone, "simple");

                        // фильтруем по правам
                        var userAccess = record.getData("permissions").userAccess;

                        // Remove any actions the user doesn't have permission for
                        var actions = YAHOO.util.Selector.query("div", clone),
                            action, aTag, spanTag, actionPermissions, aP, i, ii, j, jj;

                        if (actions.length > this.splitActionsAtStore) {
                            this.options.splitActionsAt = this.splitActionsAtStore - 1;
                        }

                        for (i = 0, ii = actions.length; i < ii; i++) {
                            action = actions[i];
                            aTag = action.firstChild;
                            spanTag = aTag.firstChild;

                            if (aTag.rel !== "") {
                                actionPermissions = aTag.rel.split(",");
                                for (j = 0, jj = actionPermissions.length; j < jj; j++) {
                                    aP = actionPermissions[j];
                                    // Support "negative" permissions
                                    if ((aP.charAt(0) == "~") ? !!userAccess[aP.substring(1)] : !userAccess[aP]) {
                                        clone.removeChild(action);
                                        break;
                                    }
                                    if (!this.versionable && (action.attributes[0].nodeValue == "onActionVersion")) {
                                        clone.removeChild(action);
                                    }
                                }
                            }
                        }
                        elActions.appendChild(clone);
                    }

                    var actionsBlock = elActions.firstChild;

                    this.updateActions(actionsBlock, oArgs.target.id, oData);

                    // Проверяем сколько у нас осталось действий и нужно ли рисовать "More >" контейнер?
                    var splitAt = this.options.splitActionsAt;

                    var getVisibleActions = function(actionsBlock){
                        var actionsDivs = YAHOO.util.Selector.query("div", actionsBlock);
                        var visible = [];
                        for (var j=0; j < actionsDivs.length; j++) {
                            var testDiv = actionsDivs[j];
                            if (testDiv.getAttribute("style") != null){
                                var style = testDiv.getAttribute("style");
                                var attrs = style.split(";");
                                for (var k=0; k <  attrs.length; k++){
                                    if (attrs[k].indexOf("display") >= 0){
                                        var attrDisplay = attrs[k];
                                        var displayValue = attrDisplay.split(":")[1].trim();
                                        if (displayValue != "none"){
                                            visible.push(testDiv);
                                        }
                                    }
                                }
                            } else {
                                visible.push(testDiv);
                            }
                        }
                        return visible;
                    }.bind(this);

                    var visibleActions = getVisibleActions(actionsBlock);
                    var showMoreDiv = null;
                    for (var k=0; k < visibleActions.length; k++) {
                        var testDiv = visibleActions[k];
                        if (Dom.hasClass(testDiv, "onActionShowMore")){
                            showMoreDiv = testDiv;
                            break;
                        }
                    }
                    // actions = YAHOO.util.Selector.query("div", actionsBlock);
                    if (!showMoreDiv && visibleActions.length > 3) {
                        var moreContainer = Dom.get(moreActionId).cloneNode(true);
                        var containerDivs = YAHOO.util.Selector.query("div", moreContainer);
                        // Insert the two necessary DIVs before the splitAt action item
                        Dom.insertBefore(containerDivs[0], visibleActions[splitAt]);
                        Dom.insertBefore(containerDivs[1], visibleActions[splitAt]);
                        // Now make action items after the split, children of the 2nd DIV
                        var index, moreActions = visibleActions.slice(splitAt);
                        for (index in moreActions) {
                            if (moreActions.hasOwnProperty(index)) {
                                containerDivs[1].appendChild(moreActions[index]);
                            }
                        }
                    }
                }
            }
        },
        /**
         * Custom event handler to unhighlight row.
         *
         * @method onEventUnhighlightRow
         * @param oArgs.event {HTMLEvent} Event object.
         * @param oArgs.target {HTMLElement} Target element.
         */
        onEventUnhighlightRow: function DataGrid_onEventUnhighlightRow(oArgs)
		{
			// Fix if oArgs.target == undefined
			if (!oArgs.target && oArgs.el) {
				oArgs.target = {};
				oArgs.target.id = oArgs.el.id;
			}

            // Call through to get the row unhighlighted by YUI
            this.widgets.dataTable.onEventUnhighlightRow.call(this.widgets.dataTable, oArgs);

            var elActions = Dom.get(this.id + "-actions-" + (oArgs.target.id));

            // Don't hide unless the More Actions drop-down is showing, or a dialog mask is present
            if (!this.showingMoreActions || Dom.hasClass(document.body, "masked"))
            {
                // Just hide the action links, rather than removing them from the DOM
                Dom.addClass(elActions, "hidden");
                this.deferredActionsMenu = null;
            }

            if (!this.options.overrideSortingWith){
                var elOtherActions = Dom.get(this.id + "-other-actions-" + oArgs.target.id);
                Dom.addClass(elOtherActions, "hidden");
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
                            if (response.json. isMoveDown == "true") {
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
        onAddRow: function(me, asset, owner, actionsConfig, confirmFunction) {
            if (this.doubleClickLock) return;
            this.doubleClickLock = true;
			var dataGrid = this.modules.dataGrid;
            var orgMetadata = dataGrid.datagridMeta;
            if (orgMetadata != null && orgMetadata.nodeRef.indexOf(":") > 0) {
                var destination = orgMetadata.nodeRef;
                var itemType = orgMetadata.itemType;

                // Intercept before dialog show
                var doBeforeDialogShow = function DataGrid_onActionEdit_doBeforeDialogShow(p_form, p_dialog) {
                    var createFormTitleMsg = dataGrid.options.createFormTitleMsg;
                    var contId = p_dialog.id + "-form-container";
                    Alfresco.util.populateHTML(
                        [contId + "_h", createFormTitleMsg ? this.msg(createFormTitleMsg) : this.msg("label.create-row.title") ]
                    );
                    if (itemType && itemType != "") {
                        Dom.addClass(contId, itemType.replace(":", "_") + "_edit");
                    }
                    var rowId = p_dialog.options.onSuccess.rowId;
                    var oDataRow = this.widgets.dataTable.getRecord(rowId);
                    if (oDataRow) {
                        var tempIndexTag = Dom.get(this.id + "-createDetails_prop_lecm-document_indexTableRow");
                        if (tempIndexTag) {
                            var index = eval(oDataRow.getData().itemData["prop_lecm-document_indexTableRow"].value);
                            tempIndexTag.value = index+1;
                        }
                    }
                    this.doubleClickLock = false;
	                p_dialog.dialog.subscribe('destroy', LogicECM.module.Base.Util.formDestructor, {moduleId: p_dialog.id}, this);
                };

                var templateUrl = Alfresco.constants.URL_SERVICECONTEXT + "lecm/components/form";
	            var templateRequestParams = {
		            itemKind:"type",
		            itemId:itemType,
		            destination:destination,
		            mode:"create",
		            formId: "addTableRow",
		            submitType:"json",
		            showCancelButton: true,
					showCaption: false
	            };

                // Using Forms Service, so always create new instance
                var createDetails = new Alfresco.module.SimpleDialog(this.id + "-createDetails");
                createDetails.setOptions(
                    {
                        width:"50em",
                        templateUrl:templateUrl,
	                    templateRequestParams: templateRequestParams,
                        actionUrl:null,
                        destroyOnHide:true,
                        doBeforeDialogShow:{
                            fn:doBeforeDialogShow,
                            scope:this
                        },
                        onSuccess:{
                            fn:function DataGrid_onActionCreate_success(response) {
                                var me = response.config.successCallback.obj.scope.options.onSuccess.scope;
								Alfresco.util.PopupManager.displayMessage(
                                    {
                                        text: this.msg("message.save.success")
                                    });
                                this.doubleClickLock = false;
								Bubbling.fire("datagridRefresh",
		                            {
			                            bubblingLabel:me.options.bubblingLabel
		                            });
                            },
                            scope:this,
                            rowId: asset.id
                        },
                        onFailure:{
                            fn:function DataGrid_onActionCreate_failure(response) {
								LogicECM.module.Base.Util.displayErrorMessageWithDetails(this.msg("logicecm.base.error"), this.msg("message.save.failure"), response.json.message);
                                this.doubleClickLock = true;
                            },
                            scope:this
                        }
                    }).show();
            }
        },
        _itemUpdate:function DataGrid_onDataItemCreated(nodeRef) {
            if (this._hasEventInterest(this.bubblingLabel) && (this.nodeRef !== null)) {
                var nodeRef = new Alfresco.util.NodeRef(nodeRef);
                // Reload the node's metadata
                Alfresco.util.Ajax.jsonPost(
                    {
                        url:Alfresco.constants.PROXY_URI + "lecm/base/item/node/" + nodeRef.uri,
                        dataObj:this._buildDataGridParams(),
                        successCallback:{
                            fn:function DataGrid_onDataItemCreated_refreshSuccess(response) {
                                var me = response.config.successCallback.scope;
                                YAHOO.Bubbling.fire("dataItemUpdated",
                                    {
                                        item: response.json.item,
                                        bubblingLabel: me.options.bubblingLabel
                                    });
                            },
                            scope:this
                        },
                        failureCallback:{
                            fn:function DataGrid_onDataItemCreated_refreshFailure(response) {
                                Alfresco.util.PopupManager.displayMessage(
                                    {
                                        text:this.msg("message.details.failure")
                                    });
                            },
                            scope:this
                        },
                        scope:this
                    });
            }
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

		_executeFunctionByName: function(functionName) {
			var args = Array.prototype.slice.call(arguments).splice(1);
			var namespaces = functionName.split(".");
			var func = namespaces.pop();
			var context = window
			for(var i = 0; i < namespaces.length; i++) {
				context = context[namespaces[i]];
			}
			return context[func].apply(this, args);
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
										newWorkspace: newWorkspace === "true" ? true : false
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
						var isApproved = response.json.isApproved === "true" ? true : false;
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


