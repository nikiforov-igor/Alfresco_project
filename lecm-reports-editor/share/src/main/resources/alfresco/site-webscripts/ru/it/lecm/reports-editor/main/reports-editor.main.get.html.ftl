<#import "/ru/it/lecm/base-share/components/lecm-datagrid.ftl" as grid/>
<#assign id = args.htmlid>

<#if !page.url.args.reportId??>
<div class="yui-t1" id="re-reports-grid">
<div id="yui-main-2">
<div class="yui-b" id="alf-content" style="margin-left: 0;">
    <@grid.datagrid id=id showViewForm=false>
        <script type="text/javascript">//<![CDATA[
            /**
             * Alfresco Slingshot aliases
             */
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

                onDataItemsDeleted: function DataGrid_onDataItemsDeleted(layer, args) {
                    var obj = args[1];
                    var me = this;
                    if (obj && this._hasEventInterest(obj.bubblingLabel) && (obj.items !== null)){
                        var recordFound, el,
                                fnCallback = function(record){
                                    return function DataGrid_onDataItemsDeleted_anim() {
                                        this.widgets.dataTable.deleteRow(record);
                                    };
                                };

                        for (var i = 0, ii = obj.items.length; i < ii; i++) {
                            recordFound = this._findRecordByParameter(obj.items[i].nodeRef, "nodeRef");
                            if (recordFound !== null) {
                                var sUrl = Alfresco.constants.PROXY_URI + "/lecm/reports/rptmanager/undeployReport?reportCode={reportCode}";
                                sUrl = YAHOO.lang.substitute(sUrl, {
                                    reportCode: obj.items[i].itemData["prop_lecm-rpeditor_reportCode"].value
                                });
                                me._showSplash();
                                var callback = {
                                    success: function (oResponse) {
                                        oResponse.argument.parent._hideSplash();
                                        obj.items[i].itemData["prop_lecm-rpeditor_reportIsDeployed"] = {value: false, displayValue: false};
                                        YAHOO.Bubbling.fire("dataItemUpdated",
                                                {
                                                    item: obj.items[i],
                                                    bubblingLabel: me.options.bubblingLabel
                                                });
                                        Alfresco.util.PopupManager.displayMessage(
                                                {
                                                    text: "Отчет удален из системы",
                                                    displayTime: 3
                                                });
                                    },
                                    failure: function (oResponse) {
                                        oResponse.argument.parent._hideSplash();
                                        Alfresco.util.PopupManager.displayMessage(
                                                {
                                                    text: "При удалении отчета из системы произошла ошибка",
                                                    displayTime: 3
                                                });
                                    },
                                    argument: {
                                        parent: me
                                    },
                                    timeout: 30000
                                };
                                YAHOO.util.Connect.asyncRequest('GET', sUrl, callback);

                                el = this.widgets.dataTable.getTrEl(recordFound);
                                Alfresco.util.Anim.fadeOut(el,
                                        {
                                            callback: fnCallback(recordFound),
                                            scope: this
                                        });
                            }
                        }
                    }
                },

                onActionDeploy: function (item) {
                    var me = this;
                    Alfresco.util.PopupManager.displayPrompt({
                        title: "Регистрация отчета",
                        text: "Вы действительно хотите добавить отчет в систему?",
                        buttons: [
                            {
                                text: "Да",
                                handler: function () {
                                    this.destroy();
                                    var sUrl = Alfresco.constants.PROXY_URI + "/lecm/reports/rptmanager/deployReport?reportDescNode={reportDescNode}";
                                    sUrl = YAHOO.lang.substitute(sUrl, {
                                        reportDescNode: item.nodeRef
                                    });
                                    me._showSplash();
                                    var callback = {
                                        success: function (oResponse) {
                                            oResponse.argument.parent._hideSplash();
                                            item.itemData["prop_lecm-rpeditor_reportIsDeployed"] =  {value: true, displayValue: true};
                                            YAHOO.Bubbling.fire("dataItemUpdated",
                                                    {
                                                        item: item,
                                                        bubblingLabel: me.options.bubblingLabel
                                                    });
                                            Alfresco.util.PopupManager.displayMessage(
                                                    {
                                                        text: "Отчет зарегистрирован в системе",
                                                        displayTime: 3
                                                    });
                                        },
                                        failure: function (oResponse) {
                                            oResponse.argument.parent._hideSplash();
                                            alert(oResponse.responseText);
                                            Alfresco.util.PopupManager.displayMessage(
                                                    {
                                                        text: "При регистрации отчета произошла ошибка",
                                                        displayTime: 3
                                                    });
                                        },
                                        argument: {
                                            parent: me
                                        },
                                        timeout: 30000
                                    };
                                    YAHOO.util.Connect.asyncRequest('GET', sUrl, callback);
                                }
                            },
                            {
                                text: "Нет",
                                handler: function dlA_onActionDelete_cancel() {
                                    this.destroy();
                                },
                                isDefault: true
                            }
                        ]
                    });
                },

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

            function createDatagrid() {
                var datagrid = new LogicECM.module.ReportsEditor.Grid('${id}').setOptions(
                        {
                            usePagination: true,
                            useDynamicPagination: true,
                            showExtendSearchBlock: false,
                            actions: [
                                {
                                    type: "datagrid-action-link-reports",
                                    id: "onActionDeploy",
                                    permission: "edit",
                                    label: "${msg("actions.deploy")}"
                                },
                                {
                                    type: "datagrid-action-link-reports",
                                    id: "onActionDelete",
                                    permission: "delete",
                                    label: "${msg("actions.delete-row")}"
                                }
                            ],
                            bubblingLabel: "reports",
                            showCheckboxColumn: false
                        }).setMessages(${messages});

                YAHOO.util.Event.onContentReady('${id}', function () {
                    YAHOO.Bubbling.fire("activeGridChanged", {
                        datagridMeta: {
                            itemType: "lecm-rpeditor:reportDescriptor",
                            nodeRef: LogicECM.module.ReportsEditor.SETTINGS.reportsContainer,
                            actionsConfig: {
                                fullDelete: true,
                                trash: false
                            },
                            sort: "cm:name|true"
                        },
                        bubblingLabel: "reports"
                    });
                });
            }

            function init() {
                createDatagrid();
            }

            YAHOO.util.Event.onDOMReady(init);
        //]]></script>
    </@grid.datagrid>
</div>
</div>
</div>
<#else>
<div id="${id}-reportForm"></div>
<script type="text/javascript">
    //<![CDATA[
    var reportForm;
    (function () {
        function init() {
            var deployFunction = function() {
                Alfresco.util.PopupManager.displayPrompt({
                    title: "Регистрация отчета",
                    text: "Вы действительно хотите добавить отчет в систему?",
                    buttons: [
                        {
                            text: "Да",
                            handler: function dlA_onActionDeploy() {
                                this.destroy();
                                var sUrl = Alfresco.constants.PROXY_URI + "/lecm/reports/rptmanager/deployReport?reportDescNode={reportDescNode}";
                                sUrl = YAHOO.lang.substitute(sUrl, {
                                    reportDescNode: "${page.url.args.reportId}"
                                });
                                var callback = {
                                    success: function (oResponse) {
                                        Alfresco.util.PopupManager.displayMessage(
                                                {
                                                    text: "Отчет зарегистрирован в системе",
                                                    displayTime: 3
                                                });
                                    },
                                    failure: function (oResponse) {
                                        alert(oResponse.responseText);
                                        Alfresco.util.PopupManager.displayMessage(
                                                {
                                                    text: "При регистрации отчета произошла ошибка",
                                                    displayTime: 3
                                                });
                                    },
                                    timeout: 30000
                                };
                                YAHOO.util.Connect.asyncRequest('GET', sUrl, callback);
                            }
                        },
                        {
                            text: "Нет",
                            handler: function dlA_onActionDelete_cancel() {
                                this.destroy();
                            },
                            isDefault: true
                        }
                    ]
                });
            };

            var saveFunction = function() {
                this.submit();
            };

            var htmlId = "${page.url.args.reportId}".replace("workspace://SpacesStore/", "").replace("-", "");
            Alfresco.util.Ajax.request(
                    {
                        url: Alfresco.constants.URL_SERVICECONTEXT + "components/form",
                        dataObj: {
                            htmlid: htmlId,
                            itemKind: "node",
                            itemId: "${page.url.args.reportId}",
                            formId: ((LogicECM.module.ReportsEditor.REPORT_SETTINGS && LogicECM.module.ReportsEditor.REPORT_SETTINGS.isSubReport == "true")
                                    ? "subReport": ""),
                            mode: "edit",
                            submitType: "json",
                            showSubmitButton: "true",
                            showCancelButton: "true"
                        },
                        successCallback: {
                            fn: function (response) {
                                var formEl = Dom.get("${id}-reportForm");
                                formEl.innerHTML = response.serverResponse.responseText;
                                Dom.setStyle("${id}-footer", "opacity", "1");

                                // Form definition
                                var form = new Alfresco.forms.Form(htmlId + '-form');

                                if (Dom.get(htmlId + "-form-cancel") !== null) {
                                    Alfresco.util.createYUIButton(null, "", deployFunction, { label: "${msg("actions.deploy")}", title: "${msg("actions.deploy")}" }, htmlId + "-form-cancel");
                                }

                                if (Dom.get(htmlId + "-form-submit") !== null) {
                                    Alfresco.util.createYUIButton(null, "", null, { label: "${msg("actions.save")}", title: "${msg("actions.save")}", type: "submit" }, htmlId + "-form-submit");
                                }

                                form.ajaxSubmit = true;
                                form.setAJAXSubmit(true,
                                        {
                                            successCallback: {
                                                fn: function () {
                                                    Alfresco.util.PopupManager.displayMessage(
                                                            {
                                                                text: "Настройки обновлены"
                                                            });
                                                },
                                                scope: this
                                            },
                                            failureCallback: {
                                                fn: function () {
                                                    Alfresco.util.PopupManager.displayMessage(
                                                            {
                                                                text: "Не удалось обновить настройки"
                                                            });
                                                },
                                                scope: this
                                            }
                                        });
                                form.setSubmitAsJSON(true);
                                form.setShowSubmitStateDynamically(true, false);
                                form.init();
                            }
                        },
                        failureMessage: "message.failure",
                        execScripts: true
                    });
        }

        YAHOO.util.Event.onDOMReady(init);
    })();
    //]]>
</script>
</#if>
