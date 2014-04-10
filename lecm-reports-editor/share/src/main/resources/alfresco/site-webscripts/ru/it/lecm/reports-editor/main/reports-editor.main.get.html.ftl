<#import "/ru/it/lecm/base-share/components/lecm-datagrid.ftl" as grid/>
<#import "/ru/it/lecm/base-share/components/base-components.ftl" as comp/>

<#assign id = args.htmlid>
<#assign aDateTime = .now>

<#assign toolbarId = "re-reports-toolbar-" + id/>
<#assign reportsLabel ="reports-" + aDateTime?iso_utc>
<#assign importFormId = toolbarId + "-import-form">
<#assign importInfoFormId = toolbarId + "-import-info-form">
<#assign importErrorFormId = toolbarId + "-import-error-form">

<div id="${toolbarId}">
    <@comp.baseToolbar toolbarId true false false>
        <div class="new-row">
            <span id="${toolbarId}-newElementButton" class="yui-button yui-push-button">
                   <span class="first-child">
                      <button type="button" title="${msg('label.new-report.btn')}">${msg('label.new-report.btn')}</button>
                   </span>
            </span>
        </div>

	    <div class="import-xml"  title="${msg('button.import-xml')}">
	        <span id="${toolbarId}-importXmlButton" class="yui-button yui-push-button">
	            <span class="first-child">
	                <button type="button">${msg('button.import-xml')}</button>
	            </span>
	        </span>
	    </div>

	    <div id="${importInfoFormId}" class="yui-panel">
		    <div id="${importInfoFormId}-head" class="hd">${msg("title.import.info")}</div>
		    <div id="${importInfoFormId}-body" class="bd">
			    <div id="${importInfoFormId}-content" class="import-info-content"></div>
		    </div>
	    </div>

	    <div id="${importErrorFormId}" class="yui-panel">
		    <div id="${importErrorFormId}-head" class="hd">${msg("title.import.info")}</div>
		    <div id="${importErrorFormId}-body" class="bd">
			    <div id="${importErrorFormId}-content" class="import-info-content">
				    <div class="import-error-header">
					    <h3>${msg("import.failure")}</h3>
					    <a href="javascript:void(0);" id="${importErrorFormId}-show-more-link">${msg("import.failure.showMore")}</a>
				    </div>
				    <div id="${importErrorFormId}-more" class="import-error-more">
					    <div class="import-error-exception">
					    ${msg("import.failure.exception")}:
						    <div class="import-error-exception-content" id="${importErrorFormId}-exception">
						    </div>
					    </div>
					    <div class="import-error-stack-trace">
					    ${msg("import.failure.stack-trace")}:
						    <div class="import-error-stack-trace-content" id="${importErrorFormId}-stack-trace">
						    </div>
					    </div>
				    </div>
			    </div>
		    </div>
	    </div>

	    <div id="${importFormId}" class="yui-panel">
		    <div id="${importFormId}-head" class="hd">${msg("title.import")}</div>
		    <div id="${importFormId}-body" class="bd">
			    <div id="${importFormId}-content">
				    <form method="post" id="${toolbarId}-import-xml-form" enctype="multipart/form-data">
					    <ul class="import-form">
						    <li>
							    <label for="${importFormId}-import-file">${msg("label.import-file")}*</label>
							    <input id="${importFormId}-import-file" type="file" name="file" accept=".xml,application/xml,text/xml">
						    </li>
						    <li>
							    <label for="${importFormId}-chbx-ignore">${msg("label.ignore-errors")}</label>
							    <input id="${importFormId}-chbx-ignore" type="checkbox" name="ignoreErrors" value="true"/>
						    </li>
					    </ul>
					    <div class="bdft">
						    <button id="${importFormId}-submit" disabled="true" tabindex="0">${msg("button.import-xml")}</button>
						    <button id="${importFormId}-cancel" tabindex="1">${msg("button.cancel")}</button>
					    </div>
				    </form>
			    </div>
		    </div>
	    </div>
    </@comp.baseToolbar>
</div>

<script type="text/javascript">//<![CDATA[
    function initToolbar() {
        new LogicECM.module.ReportsEditor.Toolbar("${toolbarId}").setMessages(${messages}).setOptions({
            bubblingLabel: "${reportsLabel}",
            createFormId: "${args.createFormId!''}",
            newRowDialogTitle: "label.create-report.title",
	        showImportXml: true
        });
    }
    YAHOO.util.Event.onContentReady("${toolbarId}", initToolbar);
//]]></script>

<#assign gridId = "re-reports-grid-" + id/>

<div class="yui-t1" id="${gridId}">
    <div class="yui-b" id="alf-content-${id}" style="margin-left: 0;">
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

                    onDataItemsDeleted: function DataGrid_onDataItemsDeleted(layer, args) {
                        var obj = args[1];
                        var me = this;
                        if (obj && this._hasEventInterest(obj.bubblingLabel) && (obj.items !== null)) {
                            var recordFound, el,
                                    fnCallback = function (record) {
                                        return function DataGrid_onDataItemsDeleted_anim() {
                                            this.widgets.dataTable.deleteRow(record);
                                        };
                                    };

                            //for (var i = 0, ii = obj.items.length; i < ii; i++) {
                            recordFound = this._findRecordByParameter(obj.items[0].nodeRef, "nodeRef");
                            if (recordFound !== null) {
                                var sUrl = Alfresco.constants.PROXY_URI + "/lecm/reports/rptmanager/undeployReport?reportCode={reportCode}";
                                sUrl = YAHOO.lang.substitute(sUrl, {
                                    reportCode: obj.items[0].itemData["prop_lecm-rpeditor_reportCode"].value
                                });
                                me._showSplash();
                                var callback = {
                                    success: function (oResponse) {
                                        oResponse.argument.parent._hideSplash();
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
                            //}
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
                                                item.itemData["prop_lecm-rpeditor_reportIsDeployed"] = {value: true, displayValue: true};
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
                    },

	                onActionExportXML: function(item) {
			            document.location.href = Alfresco.constants.PROXY_URI + "lecm/dictionary/get/export?nodeRef=" + item.nodeRef;
		            }
                }, true);

                function createDatagrid() {
                    var datagrid = new LogicECM.module.ReportsEditor.Grid('${gridId}').setOptions(
                            {
                                usePagination: true,
                                showExtendSearchBlock: false,
                                forceSubscribing: true,
                                actions: [
                                    {
                                        type: "datagrid-action-link-${reportsLabel}",
                                        id: "onActionDeploy",
                                        permission: "edit",
                                        label: "${msg("actions.deploy")}"
                                    },
                                    {
                                        type: "datagrid-action-link-${reportsLabel}",
                                        id: "onActionDelete",
                                        permission: "delete",
                                        label: "${msg("actions.delete-row")}"
                                    },
	                                {
		                                type:"datagrid-action-link-${reportsLabel}",
		                                id:"onActionExportXML",
		                                permission:"edit",
		                                label:"${msg("actions.export-xml")}"
	                                }
                                ],
                                bubblingLabel: "${reportsLabel}",
                                showCheckboxColumn: false
                            }).setMessages(${messages});

                    YAHOO.Bubbling.fire("activeGridChanged", {
                        datagridMeta: {
                            itemType: "lecm-rpeditor:reportDescriptor",
	                        useChildQuery:true,
                            nodeRef: LogicECM.module.ReportsEditor.SETTINGS.reportsContainer,
                            actionsConfig: {
                                fullDelete: true,
                                trash: false
                            },
                            sort: "cm:name|true"
                        },
                        bubblingLabel: "${reportsLabel}"
                    });
                }

                YAHOO.util.Event.onContentReady('${gridId}', createDatagrid);
            //]]></script>
        </@grid.datagrid>
    </div>
</div>
