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
 * LogicECM Dictionary module namespace.
 *
 * @namespace LogicECM
 * @class LogicECM.module.DocumentMembers
 */
LogicECM.module.DocumentMembers = LogicECM.module.DocumentMembers || {};

(function () {

    LogicECM.module.DocumentMembers.DataGrid = function (containerId) {
        return LogicECM.module.DocumentMembers.DataGrid.superclass.constructor.call(this, containerId);
    };

    /**
     * Extend from LogicECM.module.Base.DataGrid
     */
    YAHOO.lang.extend(LogicECM.module.DocumentMembers.DataGrid, LogicECM.module.Base.DataGrid);

    /**
     * Augment prototype with main class implementation, ensuring overwrite is enabled
     */
    YAHOO.lang.augmentObject(LogicECM.module.DocumentMembers.DataGrid.prototype, {
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
                                case "lecm-doc-members:member-fio":
                                    columnContent += grid.getEmployeeView(data.value, data.displayValue);
                                    break;
                                default:
                                    break;
                            }
                            if (columnContent != "") {
                                if (grid.options.attributeForShow != null && datalistColumn.name == grid.options.attributeForShow) {
                                    html += "<a href='javascript:void(0);' onclick=\"LogicECM.module.Base.Util.viewAttributes({itemId:\'" + oRecord.getData("nodeRef") + "\'})\">" + columnContent + "</a>";
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
        getEmployeeView: function DataGrid_getEmployeeView(employeeNodeRef, displayValue) {
            return "<a href='javascript:void(0);' onclick=\"LogicECM.module.Base.Util.viewAttributes({itemId:\'" + employeeNodeRef + "\',title:\'logicecm.employee.view\' })\">" + displayValue + "</a>";
        }
    }, true);
})();
