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
LogicECM.module.Base.AssociationDataGrid= LogicECM.module.Base.AssociationDataGrid  || {};

(function () {

    var Dom = YAHOO.util.Dom,
        Event = YAHOO.util.Event,
        Selector = YAHOO.util.Selector,
        Bubbling = YAHOO.Bubbling;

    LogicECM.module.Base.AssociationDataGrid = function (htmlId) {
        return LogicECM.module.Base.AssociationDataGrid.superclass.constructor.call(this, htmlId);
    };

    /**
     * Extend from LogicECM.module.Base.DataGrid
     */
    YAHOO.lang.extend(LogicECM.module.Base.AssociationDataGrid, LogicECM.module.Base.DataGrid);

    /**
     * Augment prototype with main class implementation, ensuring overwrite is enabled
     */
    YAHOO.lang.augmentObject(LogicECM.module.Base.AssociationDataGrid.prototype, {
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
		    return function (elTr, oRecord) {
	            if (oRecord.getData("type") == "total") {
	                YAHOO.util.Dom.addClass(elTr, 'total-row');
	            }
	            return true;
		    }
        },

        onActionEdit:function DataGrid_onActionEdit(item) {
            // Для предотвращения открытия нескольких карточек (при многократном быстром нажатии на кнопку редактирования)
            if (this.editDialogOpening) {
                return;
            }
            this.editDialogOpening = true;
            var me = this;

            var templateUrl = Alfresco.constants.URL_SERVICECONTEXT + "lecm/components/form";
            var templateRequestParams = {
                itemKind: "node",
                itemId: item.nodeRef,
                mode: "edit",
                submitType: "json",
                showCancelButton: true
            };
            if (this.options.editForm) {
                templateRequestParams.formId = this.options.editForm;
            }

            // Using Forms Service, so always create new instance
            var editDetails = new Alfresco.module.SimpleDialog(this.id + "-editDetails");
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
                            p_dialog.dialog.setHeader(this.msg(this.options.editFormTitleMsg));
                            this.editDialogOpening = false;

                            p_dialog.dialog.subscribe('destroy', LogicECM.module.Base.Util.formDestructor, {moduleId: p_dialog.id}, this);
                        },
                        scope:this
                    },
                    onSuccess:{
                        fn:function DataGrid_onActionEdit_success(response) {
                            // Reload the node's metadata
                            // Fire "itemUpdated" event
                            Alfresco.util.PopupManager.displayMessage({
                                text:this.msg("message.details.success")
                            });
                            Alfresco.util.Ajax.jsonPost(
                                {
                                    url: Alfresco.constants.PROXY_URI + "lecm/base/item/node/" + new Alfresco.util.NodeRef(response.json.persistedObject).uri,
                                    dataObj: this._buildDataGridParams(),
                                    successCallback: {
                                        fn: function DataGrid_onActionEdit_refreshSuccess(response) {
                                            // Fire "itemUpdated" event
                                            YAHOO.Bubbling.fire("dataItemUpdated",
                                                {
                                                    item: response.json.item,
                                                    bubblingLabel: me.options.bubblingLabel
                                                });
                                        },
                                        scope: this
                                    }
                                });
                            this.editDialogOpening = false;
                        },
                        scope:this
                    },
                    onFailure:{
                        fn:function DataGrid_onActionEdit_failure(response) {
                            Alfresco.util.PopupManager.displayMessage(
                                {
                                    text:this.msg("message.details.failure")
                                });
                            this.editDialogOpening = false;
                        },
                        scope:this
                    }
                }).show();
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

                                var recordsNum = this.widgets.dataTable.getRecordSet().getRecords().length;
                                this.widgets.dataTable.addRow(item, recordsNum);
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
                                        if (me.selectItemsTag != null) {
                                            me.selectItemsTag.value = item.nodeRef;
                                        }
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
                                    if (me.selectItemsTag != null) {
                                        me.selectItemsTag.value = me.inputAdded.value + "," + me.input.value;
                                        me.filterValues = me.selectItemsTag != null ? me.selectItemsTag.value : null;
                                    }
                                }
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
                                        if (me.selectItemsTag != null) {
                                            me.selectItemsTag.value = me.inputAdded.value + "," + me.input.value;
                                            me.filterValues = me.selectItemsTag.value;
                                        }
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
            var filterValues = this.filterValues;
            var filter = "";
            if (filterValues != null && filterValues != "") {
                var items = filterValues.split(",");
                for (var item in items) {
                    if (items[item] != "") {
                        filter = filter + " ID:" + items[item].replace(":", "\\:");
                    }
                }
            }
            if (filter == "") {
                filter += "ID:NOT_REF";
            }
            this.options.datagridMeta.searchConfig = {filter: (filter.length > 0 ? filter : "")};
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
								case "cm:cmobject":
									columnContent = "<a href='javascript:void(0);' onclick=\"LogicECM.module.Base.Util.viewAttributes({itemId:\'"+ data.value + "\', title: \'logicecm.view\'})\">" + data.displayValue + "</a>";
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
