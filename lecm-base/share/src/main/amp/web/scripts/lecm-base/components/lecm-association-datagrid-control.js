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
        Bubbling = YAHOO.Bubbling;
    var $html = Alfresco.util.encodeHTML,
        $links = Alfresco.util.activateLinks;

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
        filterValues: "",

        inputAdded: null,
        inputRemoved: null,
        input: null,
        selectItemsTag: null,

        documentRef: null,

        tooltipPosition: null,
        maxStripColumnWidth: 9999,
        strippedColumns: null,

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

                            p_dialog.dialog.subscribe('destroy', LogicECM.module.Base.Util.formDestructor, {moduleId: p_dialog.id, force: true}, this);
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
                                me._setSearchConfigFilter(this.filterValues);
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
                                        me._setSearchConfigFilter(this.filterValues);
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

        _setSearchConfigFilter: function (values) {
            this.filterValues = values;

            var filter = "";
            if (this.filterValues) {
                var items = this.filterValues.split(",");
                for (var item in items) {
                    if (items[item]) {
                        if (filter.length) {
                            filter += " OR ";
                        }
                        filter += "ID:" + items[item].replace(":", "\\:");
                    }
                }
                if (filter && this.options.pathToNodes) {
                    filter = "PATH:\"" + this.options.pathToNodes + "//*\" AND (" + filter + ")";
                }
            }
            if (!filter) {
                filter += "ID:\"NOT_REF\"";
            }
            this.options.datagridMeta.searchConfig = {filter: (filter.length ? filter : "")};
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
                            switch (datalistColumn.dataType.toLowerCase()) {
                                case "text":
                                    var hexColorPattern = /^#[0-9a-f]{6}$/i;
                                    if (data.displayValue.indexOf("!html ") == 0) {
                                        columnContent += data.displayValue.substring(6);
                                    } else if (hexColorPattern.test(data.displayValue)) {
                                        columnContent += $links(data.displayValue + '<div class="color-block" style="background-color: ' + data.displayValue + ';">&nbsp</div>');
                                    } else {
                                        columnContent += $links($html(data.displayValue));
                                    }
                                    break;
                                case "boolean":
                                    columnContent += '<div class="centered">';
                                    columnContent += (data.value ? grid.msg("message.yes") : grid.msg("message.no"));
                                    columnContent += '</div>';
                                    break;

                                case "cm:content":
                                    var fileIcon = Alfresco.util.getFileIcon(data.displayValue, "cm:content", 16);
                                    var fileIconHtml = "<img src='" + Alfresco.constants.URL_RESCONTEXT + "components/images/filetypes/" + fileIcon +"'/>";

                                    columnContent = "<a href=\'" + Alfresco.constants.URL_PAGECONTEXT+'document-attachment?nodeRef='+ data.value +"\'\">" + fileIconHtml + data.displayValue + "</a>";
                                    break;
                                case "cm:cmobject":
                                    columnContent = "<a href='javascript:void(0);' onclick=\"LogicECM.module.Base.Util.viewAttributes({itemId:\'"+ data.value + "\', title: \'logicecm.view\'})\">" + data.displayValue + "</a>";
                                    break;
                                default:
                                    if (datalistColumn.type == "association") {
                                        columnContent += $html(data.displayValue);
                                    } else if (data.displayValue == "true" || data.displayValue == "false") {
                                        columnContent += '<div class="centered">';
                                        columnContent += (data.displayValue == "true" ? grid.msg("message.yes") : grid.msg("message.no"));
                                        columnContent += '</div>';
                                    }
                                    break;
                            }
                            if (columnContent) {
                                if (grid.options.noWrapValues) {
                                    html += ("<div class='nowrap'>" + columnContent + "</div>");
                                } else {
                                    html += columnContent
                                }

                                if (i < ii - 1) {
                                    html += "<br />";
                                }
                            }
                        }

                        if (columnContent && grid.strippedColumns) {
                            if (grid.strippedColumns.indexOf(datalistColumn.name.toLowerCase()) >= 0) {
                                var stripedTooltip = html.replace(/<\/?[^>]+>/g,'');
                                if (stripedTooltip.length > grid.maxStripColumnWidth) {
                                    var content = stripedTooltip.substring(0, grid.maxStripColumnWidth) + "...";
                                    if (grid.options.noWrapValues) {
                                        content = ("<div class='nowrap'>" + content + "</div>");
                                    }
                                    html = '<div class="tt">' + html + '</div>' + content;
                                }
                            }
                        }
                    }
                }
            }
            return html.length > 0 ? html : null;  // возвращаем NULL чтобы выызвался основной метод отрисовки
        },

        onExpandClick: function(e, record) {
            var row = this.widgets.dataTable.getRow(record);
            if (Dom.hasClass(row, "expanded")) {
                Dom.get("expand-" + record.getId()).innerHTML = "+";
                Dom.removeClass(row, "expanded");
                this.onCollapse(record);
            } else {
                if (this.options.collapseAllOnExpand) {
                    this.collapseAll();
                }
                Dom.addClass(row, "expanded");
                Dom.get("expand-" + record.getId()).innerHTML = "-";
                var rowId = this.getExpandedRecordId(record);
                if (Dom.get(rowId) != null) {
                    Dom.setStyle(rowId, "display", "table-row");
                } else {
                    this.prepareExpandedRow(record);
                    this.onExpand(record);
                }
            }
        },

        onCellMouseover: function (oArgs) {
            var td = oArgs.target;
            if (td) {
                var tooltip = Selector.query(".yui-dt-liner .tt", td, true);
                if (tooltip) {
                    var windowWidth = window.innerWidth,
                        tdWidth = td.offsetWidth,
                        tooltipWidth = tooltip.offsetWidth,
                        d = 20, // отступ
                        xy = YAHOO.util.Dom.getXY(td);

                    xy[1] = xy[1] + d;
                    if (xy[0] + tooltipWidth + 4 * d > windowWidth) {
                        xy[0] = xy[0] + tdWidth - tooltipWidth - d;
                    } else {
                        xy[0] = xy[0] + d;
                    }
                    this.tooltipPosition = xy;
                    YAHOO.util.Dom.setXY(tooltip, xy);
                }
            }
        },

        onCellMouseout: function (oArgs) {
            var td = oArgs.target;
            if (td) {
                var tooltip = Selector.query(".yui-dt-liner .tt", td, true);
                if (tooltip) {
                    if (this.tooltipPosition != null) {
                        this.tooltipPosition[0] = -2000;
                        YAHOO.util.Dom.setXY(tooltip, this.tooltipPosition);
                        this.tooltipPosition = null;
                    }
                }
            }
        },

        customTableSetup: function () {
            this.widgets.dataTable.subscribe("cellMouseoverEvent", this.onCellMouseover, this, true);
            this.widgets.dataTable.subscribe("cellMouseoutEvent", this.onCellMouseout, this, true);
        },

        _showVersionLabel: function (oData, id) {
            /*disable*/
        },

        _setMaxStripColumnWidth: function (value) {
            this.maxStripColumnWidth = value;
        },

        _setStrippedColumns: function (values) {
            this.strippedColumns = values;
        }
    }, true)

})();
