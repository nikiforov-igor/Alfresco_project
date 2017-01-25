/**
 * Copyright (C) 2005-2010 Alfresco Software Limited.
 *
 * This file is part of Alfresco
 *
 * Alfresco is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Alfresco is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
 */
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


/**
 * LogicECM Base module namespace.
 *
 * @namespace LogicECM
 * @class LogicECM.module.Base
 */
LogicECM.module.Base = LogicECM.module.Base || {};


/**
 * LogicECM Base module namespace.
 *
 * @namespace LogicECM
 * @class LogicECM.module.Base
 */
LogicECM.module.Base = LogicECM.module.Base || {};
/**
 * Base: DataGrid component.
 *
 * @namespace Alfresco
 * @class LogicECM.module.Base.DataGrid
 */
LogicECM.module.Base.DataGridAssociation = LogicECM.module.Base.DataGridAssociation  || {};

(function () {

    LogicECM.module.Base.DataGridAssociation = function (htmlId) {
        return LogicECM.module.Base.DataGridAssociation.superclass.constructor.call(this, htmlId);
    };

    /**
     * Extend from LogicECM.module.Base.DataGrid
     */
    YAHOO.lang.extend(LogicECM.module.Base.DataGridAssociation, LogicECM.module.Base.DataGrid);

    /**
     * Augment prototype with main class implementation, ensuring overwrite is enabled
     */
    YAHOO.lang.augmentObject(LogicECM.module.Base.DataGridAssociation.prototype, {
        expand: false,
        selectedItems: {},
        filterValues: "",
        inputAdded: null,
        inputRemoved: null,
        input: null,
        selectItemsTag: null,

	    itemType: null,
	    assocType: null,
	    documentRef: null,
        doubleClickLock: false,
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
         * @param oArgs.el {HTMLElement} The highlighted TR element.
         * @param oArgs.record {YAHOO.widget.Record} The highlighted Record.
         */
        onEventHighlightRow:function DataGrid_onEventHighlightRow(oArgs) {
            // elActions is the element id of the active table cell where we'll inject the actions
            var elActions = Dom.get(this.id + "-actions-" + oArgs.el.id);

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
                var elOtherActions = Dom.get(this.id + "-other-actions-" + oArgs.el.id);
                this.onHighlightRowFunction(oArgs, elOtherActions,(this.id + "-otherActionSet"), (this.id + "-otherMoreActions"));
                Dom.removeClass(elOtherActions, "hidden");
            }
        },
        onHighlightRowFunction: function(oArgs, elActions, actionSetId, moreActionId){
            var selectItem = oArgs.record;
            if (selectItem){
                var oData = selectItem.getData();
                this._showVersionLabel(oData.itemData, oArgs.el.id);
            }

            if (oData.type != "total") {
                // Inject the correct action elements into the actionsId element
                if (elActions && !this.showingMoreActions) {
                    // Clone the actionSet template node from the DOM
                    var record = this.widgets.dataTable.getRecord(oArgs.el.id),
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
                            Dom.generateId(actionDiv, actionDiv.className + "-" + oArgs.el.id);
                        }

                        // Simple view by default
                        Dom.addClass(clone, "simple");

                        // фильтруем по правам
                        var userAccess = record.getData("permissions").userAccess;

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

                    this.updateActions(actionsBlock, oArgs.el.id, oData);

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
         * @param oArgs.el {HTMLElement} The highlighted TR element.
         * @param oArgs.record {YAHOO.widget.Record} The highlighted Record.
         */
        onEventUnhighlightRow: function DataGrid_onEventUnhighlightRow(oArgs)
        {
            var elActions = Dom.get(this.id + "-actions-" + (oArgs.el.id));

            var elActions = Dom.get(this.id + "-actions-" + (oArgs.el.id));

            // Don't hide unless the More Actions drop-down is showing, or a dialog mask is present
            if (!this.showingMoreActions || Dom.hasClass(document.body, "masked"))
            {
                // Just hide the action links, rather than removing them from the DOM
                Dom.addClass(elActions, "hidden");
                this.deferredActionsMenu = null;
            }

            if (!this.options.overrideSortingWith){
                var elOtherActions = Dom.get(this.id + "-other-actions-" + oArgs.el.id);
                Dom.addClass(elOtherActions, "hidden");
            }
        },
	    addFooter: function() {
		    if (this.documentRef != null && this.itemType != null && this.assocType != null) {
			    var sUrl = sUrl = Alfresco.constants.PROXY_URI + "/lecm/document/tables/api/getTotalRows" +
			            "?documentNodeRef=" + encodeURIComponent(this.documentRef) +
			            "&tableDataType=" + encodeURIComponent(this.itemType) +
			            "&tableDataAssocType=" + encodeURIComponent(this.assocType);
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

	    getRowFormater: function () {
		    var scope = this;

		    return function (elTr, oRecord) {
			    if (oRecord.getData("type") == "total") {
				    YAHOO.util.Dom.addClass(elTr, 'total-row');
			    }
			    return true;
		    }
	    },

        onDataItemCreated: function DataGrid_onDataItemCreated(layer, args) {
            var obj = args[1];
            if (obj && this._hasEventInterest(obj.bubblingLabel) && (obj.nodeRef !== null)) {
                var nodeRef = new Alfresco.util.NodeRef(obj.nodeRef);
                // Reload the node's metadata
                Alfresco.util.Ajax.jsonPost(
                    {
                        url: Alfresco.constants.PROXY_URI + "lecm/base/item/node/" + nodeRef.uri,
                        dataObj: this._buildDataGridParams(),
                        successCallback: {
                            fn: function DataGrid_onDataItemCreated_refreshSuccess(response) {
                                this.versionable = response.json.versionable;
                                var me = response.config.successCallback.scope;
                                var item = response.json.item;
                                var repeating = me.options.repeating;
                                var formMode = me.options.formMode;
                                var fnAfterUpdate = function DataGrid_onDataItemCreated_refreshSuccess_fnAfterUpdate() {
                                    var recordFound = this._findRecordByParameter(item.nodeRef, "nodeRef");
                                    if (recordFound !== null) {
                                        var el = this.widgets.dataTable.getTrEl(recordFound);
                                        Alfresco.util.Anim.pulse(el);
                                    }
                                };
                                this.afterDataGridUpdate.push(fnAfterUpdate);

	                            var recordsNum = this.widgets.dataTable.getRecordSet().getRecords().length - 1;
                                this.widgets.dataTable.addRow(item, recordsNum > 0 ? recordsNum : 0);
                                YAHOO.Bubbling.fire("mandatoryControlValueUpdated", response.config.successCallback.scope);

                                // связь 1:1
                                if (!repeating) {
                                    if (me.input.value != "" || me.inputAdded.value != "") {
                                        var deleteRef = (me.inputAdded.value != "") ? me.inputAdded.value : me.input.value;
                                        var deleteRecord = this._findRecordByParameter(deleteRef, "nodeRef");
                                        var recordTag = this.widgets.dataTable.getTrEl(deleteRecord);
                                        this.widgets.dataTable.deleteRow(recordTag);
                                        //В selectItems добавляем только что добавленую запись,
                                        //это нужно для поиска по только что добавленному значению
                                        me.selectItemsTag.value = item.nodeRef;
                                    }
                                    if (formMode == "create") {
                                        // форма создания
                                        // в форме создания в remove ничего не добавляем так как асоциация еще не создана
                                        me.inputAdded.value = item.nodeRef;
                                        me.input.value = item.nodeRef;
                                    } else {
                                        // форма редактирования и просмотра
                                        if (me.input.value == "") {
                                            me.inputAdded.value = item.nodeRef;
                                        } else {
                                            me.inputAdded.value = item.nodeRef;
                                            // добавляем remove так как связь 1:1 и мы оперируем только с одной записью, чтобы знать что было
                                            me.inputRemoved.value = me.input.value;
                                        }
                                    }
                                } else {
                                    // 1:многим
                                    if (formMode == "create") {
                                        me.inputAdded.value = (me.inputAdded.value == "") ? item.nodeRef : me.inputAdded.value + "," + item.nodeRef;
                                        me.input.value = (me.input.value == "") ? item.nodeRef : me.input.value + "," + item.nodeRef;
                                    } else {
                                        me.inputAdded.value = (me.inputAdded.value == "") ? item.nodeRef : me.inputAdded.value + "," + item.nodeRef;
                                    }
                                    //В selectItemsTag добавляем только что добавленые записи и которые были,
                                    //это нужно для поиска по только что добавленным значениям
                                    me.selectItemsTag.value = me.inputAdded.value + "," + me.input.value;
                                }
                                me.filterValues = me.selectItemsTag.value;
                                me._setSearchConfigFilter();
                                Bubbling.fire("initDatagrid",
                                    {
                                        datagrid: me
                                    });
                            },
                            scope: this
                        },
                        failureCallback: {
                            fn: function DataGrid_onDataItemCreated_refreshFailure(response) {
                                Alfresco.util.PopupManager.displayMessage(
                                    {
                                        text: this.msg("message.create.refresh.failure")
                                    });
                            },
                            scope: this
                        }
                    });
            }
        },
        onDelete_Prompt: function (fnAfterPrompt, me, items, itemsString) {
            if (me._hasEventInterest(me.options.bubblingLabel)) {

                Alfresco.util.PopupManager.displayPrompt(
                    {
                        title: this.msg("message.confirm.delete.title", items.length),
                        text: (items.length > 1) ? this.msg("message.confirm.delete.group.description", items.length) : this.msg("message.confirm.delete.description", itemsString),
                        buttons: [
                            {
                                text: this.msg("button.delete"),
                                handler: function DataGridActions__onActionDelete_delete() {
                                    if (me.inputAdded.value != "") {
                                        var refs = me.inputAdded.value.split(",");
                                        for (var i = 0; i < refs.length; i++) {
                                            if (refs[i] == items[0].nodeRef) {
                                                me.inputAdded.value = me.inputAdded.value.replace(items[0].nodeRef, "");
                                                me.input.value = (!me.options.repeating) ? "" : me.input.value.replace(items[0].nodeRef, "");
                                            }
                                        }
                                        //В selectItems добавляем только что добавленые записи и которые были,
                                        //это нужно для поиска по только что добавленным значениям
                                        me.selectItemsTag.value = me.inputAdded.value + "," + me.input.value;
                                        me.filterValues = me.selectItemsTag.value;
                                        me._setSearchConfigFilter();
                                        Bubbling.fire("initDatagrid",
                                            {
                                                datagrid: me
                                            });
                                    }
                                    YAHOO.Bubbling.fire("mandatoryControlValueUpdated", me);
                                    this.destroy();
//                                    me.selectItems("selectNone");
                                    fnAfterPrompt.call(me, items);
                                }
                            },
                            {
                                text: this.msg("button.cancel"),
                                handler: function DataGridActions__onActionDelete_cancel() {
                                    this.destroy();
                                },
                                isDefault: true
                            }
                        ]
                    });
            }
        },
        _setSearchConfigFilter: function () {
            this.options.datagridMeta.searchNodes = this.filterValues.split(",");
        },
        /**
         * Развернуть информацию
         */
        expandRow: function onViewInformation() {
            var numSelectItem = this.widgets.dataTable.getTrIndex(arguments[1].id);
            var trId = this.widgets.dataTable.getRecord(numSelectItem).getId();
            var selectItem = Dom.get(trId);
            if (selectItem.getAttribute('class').indexOf("expanded") != -1) {
                this.collapseRow(selectItem);
            } else {
                Alfresco.util.Ajax.request(
                    {
                        url: Alfresco.constants.URL_SERVICECONTEXT + "components/form",
                        dataObj: {
                            htmlid: "${fieldHtmlId}" + arguments[0].nodeRef,
                            itemKind: "node",
                            itemId: arguments[0].nodeRef,
                            formId: "table-structure-info",
                            mode: "view"
                        },
                        successCallback: {
                            fn: function (response) {
                                if (response.serverResponse != null) {
                                    var me = response.serverResponse.argument.config.scope;
                                    var rowId = response.serverResponse.argument.config.rowId;
                                    var numSelectItem = me.widgets.dataTable.getTrIndex(rowId);
                                    var oData = me.widgets.dataTable.getRecord(numSelectItem).getData();
                                    me.widgets.dataTable.addRow(oData, numSelectItem + 1);

                                    var trId = me.widgets.dataTable.getRecord(numSelectItem).getId();
                                    var selectItem = Dom.get(trId);
                                    selectItem.setAttribute('class', (selectItem.getAttribute('class') + " " + "expanded"));

                                    trId = me.widgets.dataTable.getRecord(numSelectItem + 1).getId();
                                    var newRecord = Dom.get(trId);
                                    for (var i = 0; i < newRecord.children.length; i++) {
                                        newRecord.removeChild(newRecord.children[i]);
                                    }
                                    var colCount = me.datagridColumns.length + 1;
                                    newRecord.className = "CLASS_EXPANSION";
                                    newRecord.innerHTML = "<td colspan=" + colCount + " class=\"CLASS_EXPANSION_LINE\">" + response.serverResponse.responseText + "</td>";
                                }
                            }
                        },
                        failureMessage: "message.failure",
                        execScripts: true,
                        scope: this,
                        rowId: arguments[1].id
                    });
            }
        },
        /**
         * Свернуть информацию
         * @param selectItem
         */
        collapseRow: function (selectItem) {
            var infoTag = Dom.getNextSibling(selectItem);
            this.widgets.dataTable.deleteRow(infoTag);
            selectItem.setAttribute('class', selectItem.getAttribute('class').replace("expanded"));
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
            if (this.doubleClickLock) return;
            this.doubleClickLock = true;
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
	                    templateRequestParams:templateRequestParams,
                        actionUrl:null,
                        destroyOnHide:true,
                        doBeforeDialogShow:{
                            fn:doBeforeDialogShow,
                            scope:this
                        },
                        onSuccess:{
                            fn:function DataGrid_onActionCreate_success(response) {
                                    var me = response.config.successCallback.obj.scope.options.onSuccess.scope;
                                    var obj = {
                                        datagridMeta: me.datagridMeta
                                    };
                                    YAHOO.Bubbling.fire("activeGridChanged", obj);
                                    Alfresco.util.PopupManager.displayMessage(
                                        {
                                            text: this.msg("message.save.success")
                                        });
                                    this.doubleClickLock = false;
                            },
                            scope:this,
                            rowId: arguments[1].id
                        },
                        onFailure:{
                            fn:function DataGrid_onActionCreate_failure(response) {
                                LogicECM.module.Base.Util.displayErrorMessageWithDetails(this.msg("logicecm.base.error"), this.msg("message.save.failure"), response.json.message);
                                this.doubleClickLock = false;
                            },
                            scope:this
                        }
                    }).show();
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
	    }
    }, true)

})();
