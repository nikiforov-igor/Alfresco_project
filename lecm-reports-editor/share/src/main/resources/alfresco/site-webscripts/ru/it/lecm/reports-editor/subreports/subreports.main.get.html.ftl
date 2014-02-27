<#import "/ru/it/lecm/base-share/components/lecm-datagrid.ftl" as grid/>
<#import "/ru/it/lecm/base-share/components/base-components.ftl" as comp/>

<#assign id = args.htmlid>

<#assign toolbarId = "re-subreports-toolbar-" + id/>
<div id="${toolbarId}">
<@comp.baseToolbar toolbarId true false false>
    <div class="new-row">
        <span id="${toolbarId}-newElementButton" class="yui-button yui-push-button">
               <span class="first-child">
                  <button type="button" title="${msg('label.new-subreport.btn')}">${msg('label.new-subreport.btn')}</button>
               </span>
        </span>
    </div>
</@comp.baseToolbar>
</div>

<script type="text/javascript">//<![CDATA[
(function() {
    function initToolbar() {
        new LogicECM.module.ReportsEditor.Toolbar("${toolbarId}").setMessages(${messages}).setOptions({
            bubblingLabel: "subReports",
            newRowDialogTitle: "label.create-subreport.title"
        });
    }
    YAHOO.util.Event.onContentReady("${toolbarId}", initToolbar);
})();
//]]></script>


<#assign gridId = "re-subreports-grid-" + id />
<div class="yui-t1" id="${gridId}">
    <div id="yui-main-2">
        <div class="yui-b" id="alf-content-${gridId}" style="margin-left: 0;">
        <@grid.datagrid id="${gridId}" showViewForm=false>
            <script type="text/javascript">//<![CDATA[
            var $html = Alfresco.util.encodeHTML,
                    $links = Alfresco.util.activateLinks,
                    $combine = Alfresco.util.combinePaths,
                    $userProfile = Alfresco.util.userProfileLink;

            LogicECM.module.ReportsEditor.Grid = function (containerId) {
                return LogicECM.module.ReportsEditor.Grid.superclass.constructor.call(this, containerId);
            };

            YAHOO.lang.extend(LogicECM.module.ReportsEditor.Grid, LogicECM.module.Base.DataGrid);

            YAHOO.lang.augmentObject(LogicECM.module.ReportsEditor.Grid.prototype, {

                splashScreen: null,

                _showSplash: function () {
                    this.splashScreen = Alfresco.util.PopupManager.displayMessage(
                            {
                                text: Alfresco.util.message("label.loading"),
                                spanClass: "wait",
                                displayTime: 0
                            });
                },

                _hideSplash: function () {
                    YAHOO.lang.later(2000, this.splashScreen, this.splashScreen.destroy);
                },

                getCellFormatter: function DataGrid_getCellFormatter() {
                    var scope = this;

                    /**
                     * Data Type formatter
                     *
                     * @method renderCellDataType
                     * @param elCell {object}
                     * @param oRecord {object}
                     * @param oColumn {object}
                     * @param oData {object|string}
                     */
                    return function DataGrid_renderCellDataType(elCell, oRecord, oColumn, oData) {
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
                                                case "checkboxtable":
                                                    columnContent += "<div style='text-align: center'><input type='checkbox' " + (data.displayValue == "true" ? "checked='checked'" : "") + " onClick='changeFieldState(this, \"" + data.value + "\")' /></div>"; //data.displayValue;
                                                    break;
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
                                                    columnContent += Alfresco.util.formatDate(Alfresco.util.fromISO8601(data.value), scope.msg("date-format.default"));
                                                    break;

                                                case "date":
                                                    columnContent += Alfresco.util.formatDate(Alfresco.util.fromISO8601(data.value), scope.msg("date-format.defaultDateOnly"));
                                                    break;

                                                case "text":
                                                    var hexColorPattern = /^#[0-9a-f]{6}$/i;
                                                    if (hexColorPattern.test(data.displayValue)) {
                                                        columnContent += $links(data.displayValue + '<div style="background-color: ' + data.displayValue + '; display: inline; padding: 0px 10px; margin-left: 3px;">&nbsp</div>');
                                                    } else {
                                                        columnContent += $links($html(data.displayValue));
                                                    }
                                                    break;

                                                case "boolean":
                                                    if (data.value && data.value != "false") {
                                                        columnContent += '<img src="' + Alfresco.constants.URL_RESCONTEXT + 'components/images/complete-16.png' + '" width="16" alt="' + $html(data.displayValue) + '" title="' + $html(data.displayValue) + '" />';
                                                    }
                                                    break;

                                                default:
                                                    if (datalistColumn.type == "association") {
                                                        columnContent += $html(data.displayValue);
                                                    } else {
                                                        if (data.displayValue != "false" && data.displayValue != "true") {
                                                            columnContent += $html(data.displayValue);
                                                        } else {
                                                            if (data.displayValue == "true") {
                                                                columnContent += '<img src="' + Alfresco.constants.URL_RESCONTEXT + 'components/images/complete-16.png' + '" width="16" alt="' + $html(data.displayValue) + '" title="' + $html(data.displayValue) + '" />';
                                                            }
                                                        }
                                                    }
                                                    break;
                                            }

                                            if (scope.options.attributeForShow != null && datalistColumn.name == scope.options.attributeForShow) {
                                                html += "<a href=\'" + window.location.protocol + '//' + window.location.host + Alfresco.constants.URL_PAGECONTEXT + 'report-settings?reportId=' + oRecord.getData("nodeRef") + "\'\">" + columnContent + "</a>";
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

            function createSubDatagrid() {
                var datagrid = new LogicECM.module.ReportsEditor.Grid('${gridId}').setOptions(
                        {
                            usePagination: true,
                            useDynamicPagination: false,
                            showExtendSearchBlock: false,
                            forceSubscribing: true,
                            actions: [
                                {
                                    type: "datagrid-action-link-subReports",
                                    id: "onActionDelete",
                                    permission: "delete",
                                    label: "${msg("actions.delete-row")}"
                                }
                            ],
                            datagridMeta: {
                                itemType: "lecm-rpeditor:subReportDescriptor",
                                nodeRef: "${args.reportId}",
                                actionsConfig: {
                                    fullDelete: true,
                                    trash: false
                                },
                                sort: "cm:name|true"
                            },
                            bubblingLabel: "subReports",
                            showCheckboxColumn: false
                        }).setMessages(${messages});

                datagrid.draw();
            }

            YAHOO.util.Event.onContentReady("${gridId}", createSubDatagrid);
            //]]></script>
        </@grid.datagrid>
        </div>
    </div>
</div>
