if (typeof LogicECM == "undefined" || !LogicECM) {
    LogicECM = {};
}

LogicECM.module = LogicECM.module || {};
LogicECM.module.Templates = LogicECM.module.Templates || {};

(function () {

    LogicECM.module.Templates.DataGrid = function (containerId) {
        return LogicECM.module.Templates.DataGrid.superclass.constructor.call(this, containerId);
    };

    YAHOO.lang.extend(LogicECM.module.Templates.DataGrid, LogicECM.module.Base.DataGrid);

    YAHOO.lang.augmentObject(LogicECM.module.Templates.DataGrid.prototype, {
        getCustomCellFormatter: function (grid, elCell, oRecord, oColumn, oData) {
            var html = '';
            if (!oRecord) {
                oRecord = this.getRecord(elCell);
            }
            if (!oColumn) {
                oColumn = this.getColumn(elCell.parentNode.cellIndex);
            }

            if (oRecord && oColumn) {
                if (!oData) {
                    oData = oRecord.getData('itemData')[oColumn.field];
                }

                if (oData) {
                    var datalistColumn = grid.datagridColumns[oColumn.key];
                    if (datalistColumn) {
                        oData = YAHOO.lang.isArray(oData) ? oData : [oData];
                        for (var i = 0, ii = oData.length, data; i < ii; i++) {
                            data = oData[i];

                            var columnContent = '';
                            switch (datalistColumn.name) { //  меняем отрисовку для конкретных колонок
                                case 'lecm-notification-template:send-enable':
                                    if (data.value) {
                                        columnContent += grid.msg("lecm-notification-template-send-enable");
                                    } else {
                                        columnContent += grid.msg("lecm-notification-template-send-disabled");
                                    }
                                    break;
                                case 'lecm-notification-template:exclusions-list':
                                    if (data.value) {
                                        var exclusions = YAHOO.lang.JSON.parse(data.value);
                                        if (exclusions && exclusions.rows && exclusions.rows.length) {
                                            columnContent += '<div>';
                                            columnContent += '<img src="' + Alfresco.constants.URL_RESCONTEXT + 'components/images/complete-16.png' + '" width="16"/>';
                                            columnContent += '</div>';
                                        } else {
                                            columnContent += '';
                                        }
                                    }
                                    break;
                                default:
                                    break;
                            }

                            if (i < ii - 1) {
                                html += "<br />";
                            }

                            html += columnContent;
                        }
                    }
                }
            }
            return html.length > 0 ? html : null;  // возвращаем NULL чтобы выызвался основной метод отрисовки
        }
    }, true);
})();
