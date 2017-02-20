<#import "/ru/it/lecm/base-share/components/lecm-datagrid.ftl" as grid/>

<#assign id = args.htmlid>

<div class="yui-t1" id="subscriptions-type-grid">
    <div id="yui-main-2">
        <div class="yui-b datagrid-content" id="alf-content">
            <!-- include base datagrid markup-->
		<@grid.datagrid id=id showViewForm=false>
            <script type="text/javascript">//<![CDATA[
            (function() {
                function createDatagrid() {
                    var $html = Alfresco.util.encodeHTML,
                            $links = Alfresco.util.activateLinks,
                            $userProfile = Alfresco.util.userProfileLink;

                    var sUrl = Alfresco.constants.PROXY_URI + "lecm/subscriptions/roots";
                    Alfresco.util.Ajax.jsonGet({
                        url: sUrl,
                        successCallback: {
                            fn: function (response) {
                                var oResults = response.json;
                                if (oResults) {
                                    for (var nodeIndex in oResults) {
                                        if (oResults[nodeIndex].page == "subscriptions-to-type") {
                                            var root = {
                                                nodeRef:oResults[nodeIndex].nodeRef,
                                                itemType:oResults[nodeIndex].itemType,
                                                page:oResults[nodeIndex].page,
                                                fullDelete:oResults[nodeIndex].fullDelete
                                            };
                                            var namespace = "lecm-subscr";
                                            var cType = root.itemType;
                                            root.itemType = namespace + ":" + cType;
                                            root.bubblingLabel = cType;
                                            LogicECM.module.Base.DataGrid.prototype.getCellFormatter = function DataGrid_getCellFormatter()
                                            {
                                                var scope = this;

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
                                                                                columnContent += Alfresco.util.formatDate(Alfresco.util.fromISO8601(data.value), scope.msg("lecm.date-format.defaultDateOnly"));
                                                                                break;

                                                                            case "date":
                                                                                columnContent += Alfresco.util.formatDate(Alfresco.util.fromISO8601(data.value), scope.msg("lecm.date-format.defaultDateOnly"));
                                                                                break;

                                                                            case "text":
                                                                                columnContent += $links($html(data.displayValue));
                                                                                break;

                                                                            case "boolean":
                                                                                if (data.value) {
                                                                                    columnContent += '<div>'
                                                                                    columnContent += '<img src="' + Alfresco.constants.URL_RESCONTEXT + 'components/images/complete-16.png' + '" width="16" alt="' + $html(data.displayValue) + '" title="' + $html(data.displayValue) + '" />';
                                                                                    columnContent += '</div>'
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
                                                                            html += "<a href='javascript:void(0);' onclick=\"LogicECM.module.Base.Util.viewAttributes({itemId:\'" + oRecord.getData("nodeRef") + "\'})\">" + columnContent + "</a>";
                                                                        } else {
                                                                            html += columnContent;
                                                                        }

                                                                        if (i < ii - 1) {
                                                                            html += "<br />";
                                                                        }
                                                                    }
                                                                }
                                                            } else if (oColumn.field == "assoc_lecm-subscr_object-type-assoc" ||
                                                                    oColumn.field == "assoc_lecm-subscr_event-category-assoc"){
                                                                html += "${msg('subscriptions.all')}";
                                                            }
                                                        }
                                                    } else {
                                                        html = htmlValue;
                                                    }

                                                    if (oRecord && oRecord.getData("itemData")){
                                                        if (oRecord.getData("itemData")["prop_lecm-dic_active"] && oRecord.getData("itemData")["prop_lecm-dic_active"].value == false) {
                                                            elCell.className += " archive-record";
                                                        }
                                                    }
                                                    elCell.innerHTML = html;
                                                };
                                            };

                                            var datagrid = new LogicECM.module.Base.DataGrid('${id}').setOptions(
                                                    {
                                                        usePagination: true,
                                                        actions: [
                                                            {
                                                                type: "datagrid-action-link-${bubblingLabel!''}",
                                                                id: "onActionEdit",
                                                                permission: "edit",
                                                                label: "${msg("actions.edit")}"
                                                            },
                                                            {
                                                                type: "datagrid-action-link-${bubblingLabel!''}",
                                                                id: "onActionDelete",
                                                                permission: "delete",
                                                                label: "${msg("actions.delete-row")}"
                                                            }
                                                        ],
                                                        bubblingLabel: "${bubblingLabel!''}",
                                                        showCheckboxColumn: true,
                                                        attributeForShow:"cm:name",
                                                        advSearchFormId: "${advSearchFormId!''}",
                                                        datagridMeta:{
                                                            useFilterByOrg: false,
                                                            itemType: root.itemType,
                                                            nodeRef: root.nodeRef,
                                                            actionsConfig:{
                                                                fullDelete:true
                                                            }
                                                        }
                                                    }).setMessages(${messages});
                                            datagrid.draw();
                                        }

                                    }
                                }
                            },
                            scope: this
                        },
                        failureCallback: {
                            fn: function (response) {
                                YAHOO.log("Failed to process XHR transaction.", "info", "example");
                            },
                            scope: this
                        }
                    });
                }

                function init() {
                    LogicECM.module.Base.Util.loadResources([
                        'modules/simple-dialog.js',
                        'scripts/lecm-base/components/advsearch.js',
                        'scripts/lecm-base/components/lecm-datagrid.js',
                        'components/form/date-range.js',
                        'components/form/number-range.js',
                        'scripts/lecm-base/components/versions.js',
                        'components/form/form.js'
                    ], [
                        'modules/document-details/historic-properties-viewer.css',
                        'components/search/search.css',
                        'yui/treeview/assets/skins/sam/treeview.css'
                    ], createDatagrid);
                }

                YAHOO.util.Event.onDOMReady(init);
            })();
            //]]></script>
		</@grid.datagrid>
        </div>
    </div>
</div>
