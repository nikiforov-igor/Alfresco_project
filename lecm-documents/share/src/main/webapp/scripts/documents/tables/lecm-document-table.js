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

(function()
{
	var Dom = YAHOO.util.Dom;


	LogicECM.module.DocumentTable = function (fieldHtmlId)
	{
		LogicECM.module.DocumentTable.superclass.constructor.call(this, "LogicECM.module.DocumentTable", fieldHtmlId, [ "container", "datasource"]);
		return this;
	};

	YAHOO.extend(LogicECM.module.DocumentTable, Alfresco.component.Base,
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
				datagridHeight: null,
				repeating: null
			},

			tableData: null,

			onReady: function(){
				this.loadTableData();
			},

			loadTableData: function() {
				if (this.options.currentValue != null) {
					var sUrl = sUrl = Alfresco.constants.PROXY_URI + "/lecm/document/tables/api/getData?nodeRef=" + encodeURIComponent(this.options.currentValue);
					Alfresco.util.Ajax.jsonGet(
						{
							url: sUrl,
							successCallback: {
								fn: function (response) {
									this.tableData = response.json;

									this.createToolbar();
									this.createDataGrid();
								},
								scope: this
							},
							failureCallback: {
								fn: function (oResponse) {
//									var response = YAHOO.lang.JSON.parse(oResponse.responseText);
//									this.widgets.dataTable.set("MSG_ERROR", response.message);
//									this.widgets.dataTable.showTableMessage(response.message, YAHOO.widget.DataTable.CLASS_ERROR);
								},
								scope: this
							}
						});
				}
			},

			createToolbar: function() {
				if (this.tableData != null && this.tableData.rowType != null) {
					new LogicECM.module.Base.Toolbar(null, this.options.toolbarId).setMessages(this.options.messages).setOptions({
						bubblingLabel: this.options.bubblingLabel,
						itemType: this.tableData.rowType,
						destination: this.tableData.nodeRef,
						newRowButtonType: this.options.disabled ? "inActive" : "defaultActive"
					});
				}
			},

			createDataGrid: function() {
				if (this.tableData != null && this.tableData.rowType != null) {
					var actions = [];
					var actionType = "datagrid-action-link-" + this.options.bubblingLabel;
					if (!this.options.disabled) {
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

					var datagrid = new LogicECM.module.DocumentTableDataGrid(this.options.containerId).setOptions({
						usePagination: true,
						showExtendSearchBlock: false,
						formMode: this.options.mode,
						actions: actions,
						otherActions: [
                            {
	                            type: actionType,
								id: "onMoveTableRowUp",
								permission: "edit",
								label: this.msg("actions.tableRowUp")
							},
			                {
				                type: actionType,
								id: "onMoveTableRowDown",
								permission: "edit",
								label: this.msg("action.tableRowDown")
							},
			                {
				                type: actionType,
								id: "onAddRow",
								permission: "edit",
								label: this.msg("action.addRow")
							}
						],
						datagridMeta: {
							itemType: this.tableData.rowType,
							datagridFormId: this.options.datagridFormId,
							createFormId: "",
							nodeRef: this.tableData.nodeRef,
							actionsConfig: {
								fullDelete: true
					        },
							sort: "",
							searchConfig: null
						},
						bubblingLabel: this.options.bubblingLabel,
						height: this.options.datagridHeight,
						showActionColumn: true,
						showOtherActionColumn: true,
						showCheckboxColumn: false,
						attributeForShow: this.options.attributeForShow,
						repeating: this.options.repeating
					}).setMessages(this.options.messages);
				}

				datagrid.draw();
			}
		});
})();

LogicECM.module.DocumentTableDataGrid= LogicECM.module.DocumentTableDataGrid  || {};

(function () {

	LogicECM.module.DocumentTableDataGrid = function (htmlId) {
		return LogicECM.module.DocumentTableDataGrid.superclass.constructor.call(this, htmlId);
	};

	/**
	 * Extend from LogicECM.module.Base.DataGrid
	 */
	YAHOO.lang.extend(LogicECM.module.DocumentTableDataGrid, LogicECM.module.Base.DataGrid);

	/**
	 * Augment prototype with main class implementation, ensuring overwrite is enabled
	 */
	YAHOO.lang.augmentObject(LogicECM.module.DocumentTableDataGrid.prototype, {
		addFooter: function() {
//			if (this.documentRef != null && this.itemType != null && this.assocType != null) {
//				var sUrl = sUrl = Alfresco.constants.PROXY_URI + "/lecm/document/tables/api/getTotalRows" +
//					"?documentNodeRef=" + encodeURIComponent(this.documentRef) +
//					"&tableDataType=" + encodeURIComponent(this.itemType) +
//					"&tableDataAssocType=" + encodeURIComponent(this.assocType);
//				Alfresco.util.Ajax.jsonGet(
//					{
//						url: sUrl,
//						successCallback: {
//							fn: function (response) {
//								var oResults = response.json;
//								if (oResults != null && oResults.length > 0) {
//									var row = oResults[0];
//									var item = {
//										itemData: {},
//										nodeRef: row.nodeRef,
//										type: "total"
//									};
//
//									var datagridColumns = this.datagridColumns;
//									if (datagridColumns != null) {
//										for (var i = 0; i < datagridColumns.length; i++) {
//											var field = datagridColumns[i].name;
//											var totalFields = Object.keys(row.itemData);
//											for (var j = 0; j < totalFields.length; j++) {
//												var totalField = totalFields[j];
//												if (totalField.indexOf(field) == 0) {
//													var value = row.itemData[totalField];
//													item.itemData[datagridColumns[i].formsName] = {
//														value: value,
//														displayValue: value
//													};
//												}
//											}
//										}
//									}
//
//									this.widgets.dataTable.addRow(item);
//								}
//							},
//							scope: this
//						},
//						failureCallback: {
//							fn: function (oResponse) {
//
//							},
//							scope: this
//						}
//					});
//			}
		},

		getRowFormater: function(elTr, oRecord) {
			if (oRecord.getData("type") == "total") {
				YAHOO.util.Dom.addClass(elTr, 'total-row');
			}
			return true;
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

							var columnContent = "";
							switch (datalistColumn.dataType.toLowerCase()) { //  меняем отрисовку для конкретных колонок
								case "cm:content":
									var fileIcon = Alfresco.util.getFileIcon(data.displayValue, "cm:content", 16);
									var fileIconHtml = "<img src='" + Alfresco.constants.URL_RESCONTEXT + "components/images/filetypes/" + fileIcon +"'/>";

									columnContent = "<a href=\'" + Alfresco.constants.URL_PAGECONTEXT+'document-attachment?nodeRef='+ data.value +"\'\">" + fileIconHtml + data.displayValue + "</a>";
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
                Bubbling.addDefaultAction("show-more", fnActionHandler, me.options.forceSubscribing);
            }

            if (!this.options.overrideSortingWith){
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
                Bubbling.addDefaultAction("show-more", fnOtherActionHandler, me.options.forceSubscribing);
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
                    { key:"actions", label:this.msg("label.column.actions"), sortable:false, formatter:this.fnRenderCellActions(), width:80 }
                );
            }
            if (!this.options.overrideSortingWith){
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
//                        actionSpan.innerHTML = action.label;

                        actionA.appendChild(actionSpan);
                        actionDiv.appendChild(actionA);
                        actionsDiv.appendChild(actionDiv);
                    }
                }
            }
            if (this.options.actions != null) {
                onSetupActions(this.options.actions, this.id + "-actionSet","datagrid-action-link ");
            }

            if (this.options.otherActions != null) {
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
        onEventHighlightRow:function DataGrid_onEventHighlightRow(oArgs) {
            // elActions is the element id of the active table cell where we'll inject the actions
            var elActions = Dom.get(this.id + "-actions-" + oArgs.target.id);

            this.onHighlightRowFunction(oArgs, elActions,(this.id + "-actionSet"), (this.id + "-moreActions"));

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
        onHighlightRowFunction: function(oArgs, elActions, actionSetId, moreActionId){
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

            if (oData.type != "total") {
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
                        var userAccess = record.getData("permissions").userAccess,
                            actionLabels = record.getData("actionLabels") || {};

                        // Remove any actions the user doesn't have permission for
                        var actions = YAHOO.util.Selector.query("div", clone),
                            action, aTag, spanTag, actionPermissions, aP, i, ii, j, jj;
                        if (actions.length > 3) {
                            this.options.splitActionsAt = 2;
                        } else {
                            this.options.splitActionsAt = 3;
                        }
                        for (i = 0, ii = actions.length; i < ii; i++) {
                            action = actions[i];
                            aTag = action.firstChild;
                            spanTag = aTag.firstChild;
                            if (spanTag && actionLabels[action.className]) {
                                spanTag.innerHTML = $html(actionLabels[action.className]);
                            }

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
        onMoveTableRowUp: function () {
            Alfresco.util.Ajax.jsonRequest(
                {
                    method: Alfresco.util.Ajax.GET,
                    url: Alfresco.constants.PROXY_URI + "lecm/document/tables/api/moveTableRowUp?nodeRef=" + arguments[0].nodeRef + "&assocType=" + this.assocType,
                    successCallback: {
                        fn: function (response) {
                            if (response.json. isMoveUp == "true") {
                                var me = response.config.scope;
                                var rowId = response.serverResponse.argument.config.rowId;
                                var numSelectItem = me.widgets.dataTable.getTrIndex(rowId);

                                if (numSelectItem != 0){
                                    var oDataRow1 = me.widgets.dataTable.getRecord(numSelectItem).getData();
                                    var oDataRow2 = me.widgets.dataTable.getRecord(numSelectItem-1).getData();

                                    me.widgets.dataTable.deleteRow(numSelectItem);
                                    me.widgets.dataTable.deleteRow(numSelectItem-1);

                                    me.widgets.dataTable.addRow(oDataRow1, numSelectItem - 1);
                                    me.widgets.dataTable.addRow(oDataRow2, numSelectItem);

                                    me._itemUpdate(oDataRow1.nodeRef);
                                    me._itemUpdate(oDataRow2.nodeRef);
                                }
                            }
                        }
                    },
                    failureCallback: {
                        fn:function DataGrid_onDataItemCreated_refreshFailure(response) {
                            Alfresco.util.PopupManager.displayMessage(
                                {
                                    text:this.msg("message.details.failure")
                                });
                        }
                    },
                    scope: this,
                    rowId: arguments[1].id

                });
        },
        onMoveTableRowDown: function () {
            Alfresco.util.Ajax.jsonRequest(
                {
                    method: Alfresco.util.Ajax.GET,
                    url: Alfresco.constants.PROXY_URI + "lecm/document/tables/api/moveTableRowDown?nodeRef=" + arguments[0].nodeRef + "&assocType=" + this.assocType,
                    successCallback: {
                        fn: function (response) {
                            if (response.json. isMoveDown == "true") {
                                var me = response.config.scope;
                                var rowId = response.serverResponse.argument.config.rowId;
                                var numSelectItem = me.widgets.dataTable.getTrIndex(rowId);

                                var count = me.widgets.dataTable.getRecordSet()._records.length;
                                if (numSelectItem+1 < count){

                                    var oDataRow1 = me.widgets.dataTable.getRecord(numSelectItem).getData();
                                    var oDataRow2 = me.widgets.dataTable.getRecord(numSelectItem+1).getData();

                                    me.widgets.dataTable.deleteRow(numSelectItem + 1);
                                    me.widgets.dataTable.deleteRow(numSelectItem);

                                    me.widgets.dataTable.addRow(oDataRow2, numSelectItem);
                                    me.widgets.dataTable.addRow(oDataRow1, numSelectItem + 1);

                                    me._itemUpdate(oDataRow1.nodeRef);
                                    me._itemUpdate(oDataRow2.nodeRef);
                                }
                            }
                        }
                    },
                    failureCallback: {
                        fn:function DataGrid_onDataItemCreated_refreshFailure(response) {
                            Alfresco.util.PopupManager.displayMessage(
                                {
                                    text:this.msg("message.details.failure")
                                });
                        }
                    },
                    scope: this,
                    rowId: arguments[1].id

                });
        },
        onAddRow: function() {
            var orgMetadata = this.modules.dataGrid.datagridMeta;
            if (orgMetadata != null && orgMetadata.nodeRef.indexOf(":") > 0) {
                var destination = orgMetadata.nodeRef;
                var itemType = orgMetadata.itemType;
                var rowID = arguments[1].id;

                // Intercept before dialog show
                var doBeforeDialogShow = function DataGrid_onActionEdit_doBeforeDialogShow(p_form, p_dialog) {
                    var addMsg = orgMetadata.addMessage;
                    var contId = p_dialog.id + "-form-container";
                    Alfresco.util.populateHTML(
                        [contId + "_h", addMsg ? addMsg : this.msg("label.create-row.title") ]
                    );
                    if (itemType && itemType != "") {
                        Dom.addClass(contId, itemType.replace(":", "_") + "_edit");
                    }
                    var rowId = p_dialog.options.onSuccess.rowId;
                    var numSelectItem = this.widgets.dataTable.getTrIndex(rowId);
                    var oDataRow = this.widgets.dataTable.getRecord(numSelectItem).getData();
                    if (oDataRow) {
                        var tempIndexTag = Dom.get(this.id + "-createDetails_prop_lecm-document_tempIndexTableRow-added");
                        if (tempIndexTag) {
                            tempIndexTag.value = oDataRow.nodeRef;
                        }
                    }
                };

                var templateUrl = YAHOO.lang.substitute(Alfresco.constants.URL_SERVICECONTEXT + "lecm/components/form?itemKind={itemKind}&itemId={itemId}&destination={destination}&mode={mode}&submitType={submitType}&formId={formId}&showCancelButton=true",
                    {
                        itemKind:"type",
                        itemId:itemType,
                        destination:destination,
                        mode:"create",
                        formId: "addTableRow",
                        submitType:"json"
                    });

                // Using Forms Service, so always create new instance
                var createDetails = new Alfresco.module.SimpleDialog(this.id + "-createDetails");
                createDetails.setOptions(
                    {
                        width:"50em",
                        templateUrl:templateUrl,
                        actionUrl:null,
                        destroyOnHide:true,
                        doBeforeDialogShow:{
                            fn:doBeforeDialogShow,
                            scope:this
                        },
                        onSuccess:{
                            fn:function DataGrid_onActionCreate_success(response) {
                                var me = response.config.successCallback.scope.options.onSuccess.scope;
                                var obj = {
                                    datagridMeta: me.datagridMeta
                                };
                                YAHOO.Bubbling.fire("activeGridChanged", obj);
                                Alfresco.util.PopupManager.displayMessage(
                                    {
                                        text: this.msg("message.save.success")
                                    });
                            },
                            scope:this,
                            rowId: arguments[1].id
                        },
                        onFailure:{
                            fn:function DataGrid_onActionCreate_failure(response) {
                                this.displayErrorMessageWithDetails(this.msg("logicecm.base.error"), this.msg("message.save.failure"), response.json.message);
                            },
                            scope:this
                        }
                    }).show();
            }
        }
	}, true)

})();


