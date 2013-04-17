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
 * @class LogicECM.module.Dictionary
 */
LogicECM.module.Dictionary = LogicECM.module.Dictionary || {};

(function () {
    var $html = Alfresco.util.encodeHTML,
        $links = Alfresco.util.activateLinks,
        $userProfile = Alfresco.util.userProfileLink;
    var attributeForShow;

    LogicECM.module.Dictionary.DataGrid = function (containerId, attributeForShowing) {
        attributeForShow = attributeForShowing;
        return LogicECM.module.Dictionary.DataGrid.superclass.constructor.call(this, containerId);
    };

    /**
     * Extend from LogicECM.module.Base.DataGrid
     */
    YAHOO.lang.extend(LogicECM.module.Dictionary.DataGrid, LogicECM.module.Base.DataGrid);

    /**
     * Augment prototype with main class implementation, ensuring overwrite is enabled
     */
    YAHOO.lang.augmentObject(LogicECM.module.Dictionary.DataGrid.prototype, {
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
                        var content;
                        oData = YAHOO.lang.isArray(oData) ? oData : [oData];

                        for (var i = 0, ii = oData.length, data; i < ii; i++) {
                            data = oData[i];

                            switch (datalistColumn.dataType.toLowerCase()) {
                                case "cm:person":
                                    html += '<span class="person">' + $userProfile(data.metadata, data.displayValue) + '</span>';
                                    break;

                                case "datetime":
                                    content = Alfresco.util.formatDate(Alfresco.util.fromISO8601(data.value), grid.msg("date-format.default"));
                                    if (datalistColumn.name == attributeForShow) {
                                        content = "<a href='javascript:void(0);' onclick=\"viewAttributes(\'" + oRecord.getData("nodeRef") + "\')\">" + content + "</a>";
                                    }
                                    html += content;
                                    break;

                                case "date":
                                    content = Alfresco.util.formatDate(Alfresco.util.fromISO8601(data.value), grid.msg("date-format.defaultDateOnly"));
                                    if (datalistColumn.name == attributeForShow) {
                                        content = "<a href='javascript:void(0);' onclick=\"viewAttributes(\'" + oRecord.getData("nodeRef") + "\')\">" + content + "</a>";
                                    }
                                    html += content;
                                    break;

                                case "text":
                                    var hexColorPattern = /^#[0-9a-f]{6}$/i;
                                    if (hexColorPattern.test(data.displayValue)) {
                                        content = data.displayValue + '<div style="background-color: ' + data.displayValue + '; display: inline; padding: 0px 10px; margin-left: 3px;">&nbsp</div>';
                                    } else {
                                        content = $html(data.displayValue);
                                    }

                                    if (datalistColumn.name == attributeForShow) {
                                        html += "<a href='javascript:void(0);' onclick=\"viewAttributes(\'" + oRecord.getData("nodeRef") + "\')\">" + content + "</a>";
                                    } else {
                                        html += $links(content);
                                    }
                                    break;

                                case "boolean":
                                    if (data.value) {
                                        html += '<div style="text-align: center;">';
                                        html += '<img src="' + Alfresco.constants.URL_RESCONTEXT + 'components/images/complete-16.png' + '" width="16" alt="' + $html(data.displayValue) + '" title="' + $html(data.displayValue) + '" />';
                                        html += '</div>';
                                    }
                                    break;

                                default:
                                    if (datalistColumn.type == "association") {
                                        html += $html(data.displayValue);
                                    } else {
                                        if (data.displayValue != "false" && data.displayValue != "true") {
                                            html += $html(data.displayValue);
                                        } else {
                                            if (data.displayValue == "true") {
                                                html += '<img src="' + Alfresco.constants.URL_RESCONTEXT + 'components/images/complete-16.png' + '" width="16" alt="' + $html(data.displayValue) + '" title="' + $html(data.displayValue) + '" />';
                                            }
                                        }
                                    }
                                    break;
                            }

                            if (i < ii - 1) {
                                html += "<br />";
                            }
                        }
                    }
                }
            }

            return html.length > 0 ? html : null;  // возвращаем NULL чтобы вызвался основной метод отрисовки
        }
    }, true);
})();
