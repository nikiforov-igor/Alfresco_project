<#import "/ru/it/lecm/base-share/components/lecm-datagrid.ftl" as grid/>
<#import "/ru/it/lecm/base-share/components/base-components.ftl" as comp/>
<#assign id = args.htmlid>
<#if page.url.args.reportId??>
<div class="reports">
    <!-- Grid Start -->
    <div class="yui-t1" id="${id}-re-reports-grid">
        <div id="yui-main-2">
            <div class="yui-b" id="alf-content" style="margin-left: 0;">
                <@grid.datagrid id='${id}-re-reports-grid' showViewForm=false>
                    <script type="text/javascript">//<![CDATA[
                    function initGrid() {
                        var datagrid = new LogicECM.module.ReportsEditor.ColumnsGrid('${id}-re-reports-grid').setOptions(
                                {
                                    usePagination: true,
                                    useDynamicPagination: false,
                                    showExtendSearchBlock: false,
                                    overrideSortingWith: false,
                                    editForm: (LogicECM.module.ReportsEditor.REPORT_SETTINGS && LogicECM.module.ReportsEditor.REPORT_SETTINGS.isSQLReport == "true")
                                            ? "sql-provider-column" : "",
                                    actions: [
                                        {
                                            type: "datagrid-action-link-editSourceColumns",
                                            id: "onActionEdit",
                                            permission: "edit",
                                            label: "${msg("actions.edit")}"
                                        },
                                        {
                                            type: "datagrid-action-link-editSourceColumns",
                                            id: "onActionDelete",
                                            permission: "delete",
                                            label: "${msg("actions.delete-row")}"
                                        }
                                    ],
                                    bubblingLabel: "editSourceColumns",
                                    showCheckboxColumn: true
                                }).setMessages(${messages});

                        YAHOO.util.Event.onContentReady('${id}-re-reports-grid', function () {
                            YAHOO.Bubbling.fire("activeGridChanged", {
                                datagridMeta: {
                                    itemType: "lecm-rpeditor:reportDataColumn",
                                    nodeRef: "${activeSourceId!"NOT_LOAD"}",
                                    actionsConfig: {
                                        fullDelete: true,
                                        trash: false
                                    },
                                    sort: "lecm-rpeditor:dataColumnCode|true"
                                },
                                bubblingLabel: "editSourceColumns"
                            });
                        });
                    }

                    YAHOO.util.Event.onDOMReady(initGrid);
                    //]]></script>
                </@grid.datagrid>
            </div>
        </div>
    </div>
</div>
<#else>
<div>${msg("label.unavaiable-page")}</div>
</#if>
