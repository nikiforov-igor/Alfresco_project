/*// Ensure LogicECM root object exists
 if (typeof LogicECM == "undefined" || !LogicECM) {
 var LogicECM = {};
 }

 *//**
 * LogicECM top-level module namespace.
 *
 * @namespace LogicECM
 * @class LogicECM.module
 *//*
 LogicECM.module = LogicECM.module || {};


 *//**
 * LogicECM Dictionary module namespace.
 *
 * @namespace LogicECM
 * @class LogicECM.module.DocumentsJournal
 *//*
 LogicECM.module.DocumentsJournal = LogicECM.module.DocumentsJournal || {};*/

(function () {

    LogicECM.module.DocumentsJournal.DataGrid = function (containerId) {
        return LogicECM.module.DocumentsJournal.DataGrid.superclass.constructor.call(this, containerId);
    };

    /**
     * Extend from LogicECM.module.Base.DataGrid
     */
    YAHOO.lang.extend(LogicECM.module.DocumentsJournal.DataGrid, LogicECM.module.Base.DataGrid);

    /**
     * Augment prototype with main class implementation, ensuring overwrite is enabled
     */
    YAHOO.lang.augmentObject(LogicECM.module.DocumentsJournal.DataGrid.prototype, {
        getCustomCellFormatter: function (grid, elCell, oRecord, oColumn, oData) {
            var html = "";
            // Populate potentially missing parameters
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
                            switch (datalistColumn.name.toLowerCase()) { //  меняем отрисовку для конкретных колонок
                                case "lecm-document:present-string":
                                    columnContent += "<a href=\'" + window.location.protocol + '//' + window.location.host + Alfresco.constants.URL_PAGECONTEXT + 'document?nodeRef=' + oRecord.getData("nodeRef") + "\'\">" + data.displayValue + "</a>";
                                    columnContent += '<br />';
                                    columnContent += oRecord.getData("itemData")["prop_lecm-document_list-present-string"].value;
                                    break;
                                default:
                                    break;
                            }

                            if (grid.options.attributeForShow != null && datalistColumn.name == grid.options.attributeForShow) {
                                html += "<a href=\'" + window.location.protocol + '//' + window.location.host + Alfresco.constants.URL_PAGECONTEXT + 'document?nodeRef=' + oRecord.getData("nodeRef") + "\'\">" + columnContent + "</a>";
                            } else {
                                html += columnContent;
                            }

                            if (i < ii - 1) {
                                html += "<br />";
                            }
                        }
                    }
                } else if (oColumn.field == "prop_cm_image") {
                    if (oRecord.getData("type")) {
                        var icon = oRecord.getData("type").replace(":", "_") + ".png";
                        html += "<img src='" + Alfresco.constants.URL_RESCONTEXT + "/images/lecm-documents/type-icons/" + icon + "'/>";
                    }else {
                        html += "<img src='" + Alfresco.constants.URL_RESCONTEXT + "/images/lecm-documents/type-icons/default_document.png'/>";
                    }
                }
            }
            return html.length > 0 ? html : null;  // возвращаем NULL чтобы выызвался основной метод отрисовки
        },

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

            var column, sortable;
            for (var i = 0, ii = this.datagridColumns.length; i < ii; i++) {
                column = this.datagridColumns[i];
                sortable = column.sortable;
                if (column.name != "cm:name" && column.name != "lecm-document:list-present-string") {
                    columnDefinitions.push(
                        {
                            key:this.dataResponseFields[i],
                            label:column.label.length > 0 ? column.label : this.msg(column.name.replace(":", "_")),
                            sortable:sortable,
                            sortOptions:{
                                field:column.formsName,
                                sortFunction:this.getSortFunction()
                            },
                            formatter:this.getCellFormatter(column.dataType),
                            className: (column.dataType == 'boolean') ? 'centered' : ''
                        });
                }
            }
            if (this.options.showActionColumn){
                // Add actions as last column
                columnDefinitions.push(
                    { key:"actions", label:this.msg("label.column.actions"), sortable:false, formatter:this.fnRenderCellActions(), width:80 }
                );
            }
            return columnDefinitions;
        }
    }, true);
})();
