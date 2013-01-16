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


/**
 * LogicECM Dictionary module namespace.
 *
 * @namespace LogicECM
 * @class LogicECM.module.BusinessJournal
 */
LogicECM.module.BusinessJournal = LogicECM.module.BusinessJournal || {};

(function () {

    LogicECM.module.BusinessJournal.DataGrid = function (containerId) {
        return LogicECM.module.BusinessJournal.DataGrid.superclass.constructor.call(this, containerId);
    };

    /**
     * Extend from LogicECM.module.Base.DataGrid
     */
    YAHOO.lang.extend(LogicECM.module.BusinessJournal.DataGrid, LogicECM.module.Base.DataGrid);

    /**
     * Augment prototype with main class implementation, ensuring overwrite is enabled
     */
    YAHOO.lang.augmentObject(LogicECM.module.BusinessJournal.DataGrid.prototype, {
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
                            switch (datalistColumn.dataType.toLowerCase()) { //  меняем отрисовку для конкретных типов
                                case "lecm-busjournal:eventcategory":
                                    columnContent += grid.getDicValueView(data.value, data.displayValue);
                                    break;

                                case "lecm-busjournal:objecttype":
                                    columnContent += grid.getDicValueView(data.value, data.displayValue);
                                    break;

                                case "lecm-orgstr:employee":
                                    columnContent += grid.getEmployeeView(data.value, data.displayValue);
                                    break;
                                default:
                                    break;
                            }
                            switch (datalistColumn.name.toLowerCase()) { //  меняем отрисовку для конкретных колонок
                                case "lecm-busjournal:bjrecord-mainobject-assoc":
                                    columnContent += grid.getDicValueView(data.value, data.displayValue);
                                    break;
                                case "lecm-busjournal:secondary-objects":
                                    // если нет доп объектов (строка '; ; ; ; ') - выводим пустую строку
                                    if (data.displayValue && data.displayValue.length <= 8) {
                                        columnContent += "(нет)";
                                    }
                                    break;
                                default:
                                    break;
                            }
                            if (columnContent != "") {
                                if (grid.options.attributeForShow != null && datalistColumn.name == grid.options.attributeForShow) {
                                    html += "<a href='javascript:void(0);' onclick=\"viewAttributes(\'" + oRecord.getData("nodeRef") + "\')\">" + columnContent + "</a>";
                                } else {
                                    html += columnContent;
                                }

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
        getDicValueView: function DataGrid_getDicitonaryValueView(nodeRef, displayValue) {
            if (displayValue.length == 0) {
                return "";
            }
            return "<span><a href='javascript:void(0);' onclick=\"viewAttributes(\'" + nodeRef + "\')\">" + displayValue + "</a></span>";
        },
        getEmployeeView: function DataGrid_getEmployeeView(employeeNodeRef, displayValue) {
            if (displayValue.length == 0 || employeeNodeRef == null || employeeNodeRef.indexOf("://") < 0) {
                return "Система";
            }
            return "<span class='person'><a href='javascript:void(0);' onclick=\"viewAttributes(\'" + employeeNodeRef + "\')\">" + displayValue + "</a></span>";
        }
    }, true);
})();
