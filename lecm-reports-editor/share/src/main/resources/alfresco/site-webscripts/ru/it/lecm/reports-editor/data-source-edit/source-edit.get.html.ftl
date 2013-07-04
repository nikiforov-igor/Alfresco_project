<#import "/ru/it/lecm/base-share/components/lecm-datagrid.ftl" as grid/>
<#import "/ru/it/lecm/base-share/components/base-components.ftl" as comp/>
<#assign id = args.htmlid>
<#if page.url.args.reportId??>
<div class="reports">
    <#assign toolbarId = "${id}-columns-toolbar"/>
    <!-- Toolbar Start-->
    <div id="${toolbarId}">
        <@comp.baseToolbar toolbarId true false false>
            <div class="new-row">
                    <span id="${toolbarId}-newColumnButton" class="yui-button yui-push-button">
                        <span class="first-child">
                            <button type="button"
                                    title="${msg("label.new-column.btn")}">${msg("label.new-column.btn")}</button>
                        </span>
                    </span>
            </div>
            <div class="new-row">
                    <span id="${toolbarId}-saveAsButton" class="yui-button yui-push-button">
                        <span class="first-child">
                            <button type="button"
                                    title="${msg("label.save-as-source.btn")}">${msg("label.save-as-source.btn")}</button>
                        </span>
                    </span>
            </div>
        </@comp.baseToolbar>
    </div>
    <!-- Toolbar End-->
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
                                    actions: [
                                        {
                                            type: "datagrid-action-link-source-columns",
                                            id: "onActionEdit",
                                            permission: "edit",
                                            label: "${msg("actions.edit")}"
                                        },
                                        {
                                            type: "datagrid-action-link-source-columns",
                                            id: "onActionDelete",
                                            permission: "delete",
                                            label: "${msg("actions.delete-row")}"
                                        }
                                    ],
                                    bubblingLabel: "source-columns",
                                    showCheckboxColumn: false
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
                                bubblingLabel: "source-columns"
                            });
                        });
                    }

                    YAHOO.util.Event.onDOMReady(initGrid);
                    //]]></script>
                </@grid.datagrid>
            </div>
        </div>
    </div>
    <!-- Grid End -->
    <script type="text/javascript">
        function init() {
            var editor = new LogicECM.module.ReportsEditor.EditSourceEditor("${id}").setMessages(${messages});
            editor.setReportId("${page.url.args.reportId}");
            editor.setDataSourceId("${activeSourceId!""}");
            editor.markAsNewSource(!${existInRepo?string});
        }

        YAHOO.util.Event.onDOMReady(init);
    </script>
</div>
<#else>
<div>${msg("label.unavaiable-page")}</div>
</#if>
