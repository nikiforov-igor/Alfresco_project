/**
 * LogicECM root namespace.
 *
 * @namespace LogicECM
 */
// Ensure LogicECM root object exists
if (typeof LogicECM == "undefined" || !LogicECM) {
    LogicECM = {};
}

LogicECM.module = LogicECM.module || {};
LogicECM.module.Navigator = LogicECM.module.Navigator || {};

(function () {
    var $siteURL = Alfresco.util.siteURL;

    LogicECM.module.Navigator.DataGrid = function (htmlId) {
        LogicECM.module.Navigator.DataGrid.superclass.constructor.call(this, htmlId);
        return this;
    };

    /**
     * Extend from Alfresco.component.Base
     */
    YAHOO.extend(LogicECM.module.Navigator.DataGrid, LogicECM.module.Base.DataGrid);

    /**
     * Augment prototype with main class implementation, ensuring overwrite is enabled
     */
    YAHOO.lang.augmentObject(LogicECM.module.Navigator.DataGrid.prototype, {

        onActionViewDocument: function function_onActionViewDocument(item) {
            document.location.href = Alfresco.constants.URL_PAGECONTEXT + "document-details?nodeRef=" + item.nodeRef;
        },

        getCellFormatter: function DataGrid_getCellFormatter() {
            var scope = this;
            return function DataGrid_renderCellDataType(elCell, oRecord, oColumn, oData) {
                var $html = Alfresco.util.encodeHTML,
                    $links = Alfresco.util.activateLinks,
                    $userProfile = Alfresco.util.userProfileLink;
                var html = "";
                var htmlValue = scope.getCustomCellFormatter.call(this, scope, elCell, oRecord, oColumn, oData);
                if (htmlValue == null) { // используем стандартный форматтер
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
                            var datalistColumn = scope.datagridColumns[oColumn.key];
                            if (datalistColumn) {
                                oData = YAHOO.lang.isArray(oData) ? oData : [oData];
                                for (var i = 0, ii = oData.length, data; i < ii; i++) {
                                    data = oData[i];

                                    var columnContent = "";
                                    switch (datalistColumn.dataType.toLowerCase()) {
                                        case "lecm-orgstr:employee":
                                            columnContent += scope.getEmployeeView(data.value, data.displayValue);
                                            break;

                                        case "lecm-orgstr:employee-link":
                                            columnContent += scope.getEmployeeViewByLink(data.value, data.displayValue);
                                            break;

                                        case "cm:person":
                                            columnContent += '<span class="person">' + $userProfile(data.metadata, data.displayValue) + '</span>';
                                            break;

                                        case "datetime":
                                            columnContent += '<span class="datagrid-datetime">' + Alfresco.util.formatDate(Alfresco.util.fromISO8601(data.value), scope.msg("lecm.date-format.defaultDateOnly")) + '</span>';
                                            break;

                                        case "date":
                                            columnContent += '<span class="datagrid-date">' + Alfresco.util.formatDate(Alfresco.util.fromISO8601(data.value), scope.msg("lecm.date-format.defaultDateOnly")) + '</span>';
                                            break;

                                        case "text":
                                            var hexColorPattern = /^#[0-9a-f]{6}$/i;
                                            if (hexColorPattern.test(data.displayValue)) {
                                                columnContent += $links(data.displayValue + '<div class="color-block" style="background-color: ' + data.displayValue + ';">&nbsp</div>');
                                            } else {
                                                columnContent += $links($html(data.displayValue));
                                            }
                                            break;

                                        case "boolean":
                                            if (data.value) {
                                                columnContent += '<div class="centered">';
//                                                columnContent += '<img src="' + Alfresco.constants.URL_RESCONTEXT + 'components/images/complete-16.png' + '" width="16" alt="' + $html(data.displayValue) + '" title="' + $html(data.displayValue) + '" />';
                                                columnContent += '<span class="boolean-true">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</span>';
                                                columnContent += '</div>';
                                            }
                                            break;

                                        default:
                                            if (datalistColumn.name == "size") {
                                                var size = data.value;
                                                var result = size + " б";
                                                if (Math.floor(size / 1024) != 0) {
                                                    size = size / 1024;
                                                    var result = size.toFixed(2) + " кб";
                                                    if (Math.floor(size / 1024) != 0) {
                                                        size = size / 1024;
                                                        var result = size.toFixed(2) + " Мб";
                                                        if (Math.floor(size / 1024) != 0) {
                                                            size = size / 1024;
                                                            var result = size.toFixed(2) + " Гб";
                                                        }
                                                    }
                                                }
                                                columnContent += result;
                                            } else if (datalistColumn.type == "association") {
                                                columnContent += $html(data.displayValue);
                                            } else {
                                                if (data.displayValue != "false" && data.displayValue != "true") {
                                                    columnContent += $html(data.displayValue);
                                                } else {
                                                    if (data.displayValue == "true") {
//                                                        columnContent += '<img src="' + Alfresco.constants.URL_RESCONTEXT + 'components/images/complete-16.png' + '" width="16" alt="' + $html(data.displayValue) + '" title="' + $html(data.displayValue) + '" />';
                                                        columnContent += '<span class="boolean-true">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</span>';
                                                    }
                                                }
                                            }
                                            break;
                                    }
                                    var firstColumnIndex = scope.options.showCheckboxColumn ? 1 : 0;
                                    if (oColumn.getKeyIndex() == firstColumnIndex) {
                                        html += "<a href=\'" + window.location.protocol + '//' + window.location.host + Alfresco.constants.URL_PAGECONTEXT + 'document-details?nodeRef=' + oRecord.getData("nodeRef") + "\'\">" + columnContent + "</a>";
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
                } else {
                    html = htmlValue;
                }

                if (oRecord && oRecord.getData("itemData")) {
                    if (oRecord.getData("itemData")["prop_lecm-dic_active"] && oRecord.getData("itemData")["prop_lecm-dic_active"].value == false) {
                        elCell.className += " archive-record";
                    }
                }
                elCell.innerHTML = html;
            };
        }
    }, true);
})();