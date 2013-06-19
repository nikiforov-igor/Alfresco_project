<#if !page.url.args.reportId??>
<#import "/ru/it/lecm/base-share/components/lecm-datagrid.ftl" as grid/>
<#assign id = args.htmlid>
<div class="yui-t1" id="bj-dictionary-grid">
    <div id="yui-main-2">
        <div class="yui-b" id="alf-content" style="margin-left: 0;">
            <@grid.datagrid id=id showViewForm=false>
                <script type="text/javascript">//<![CDATA[

                LogicECM.module.ReportsEditor.Grid = function (containerId) {
                    return LogicECM.module.ReportsEditor.Grid.superclass.constructor.call(this, containerId);
                };

                /**
                 * Extend from LogicECM.module.Base.DataGrid
                 */
                YAHOO.lang.extend (LogicECM.module.ReportsEditor.Grid, LogicECM.module.Base.DataGrid);

                /**
                 * Augment prototype with main class implementation, ensuring overwrite is enabled
                 */
                YAHOO.lang.augmentObject (LogicECM.module.ReportsEditor.Grid.prototype, {

                    onActionEdit: function (item) {
                        var baseUrl = window.location.protocol + "//" + window.location.host;
                        var template = "reports-editor?reportId={reportId}";
                        var url = YAHOO.lang.substitute (baseUrl + Alfresco.constants.URL_PAGECONTEXT + template, {
                            reportId: item.nodeRef
                        });
                        window.location.href = url
                    }

                }, true);

                function createDatagrid() {
                    var datagrid = new LogicECM.module.ReportsEditor.Grid('${id}').setOptions(
                            {
                                usePagination:true,
                                useDynamicPagination :true,
                                showExtendSearchBlock:false,
                                actions: [
                                    {
                                        type:"datagrid-action-link-reports",
                                        id:"onActionEdit",
                                        permission:"edit",
                                        label:"${msg("actions.edit")}"
                                    },
                                    {
                                        type:"datagrid-action-link-reports",
                                        id:"onActionDelete",
                                        permission:"delete",
                                        label:"${msg("actions.delete-row")}"
                                    }
                                ],
                                bubblingLabel: "reports",
                                showCheckboxColumn: false
                            }).setMessages(${messages});

                    YAHOO.util.Event.onContentReady ('${id}', function () {
                        YAHOO.Bubbling.fire ("activeGridChanged", {
                            datagridMeta: {
                                itemType: "lecm-rpeditor:reportDescriptor",
                                nodeRef: LogicECM.module.ReportsEditor.SETTINGS.reportsContainer,
                                actionsConfig: {
                                    fullDelete: true,
                                    trash: false
                                },
                                sort:"cm:name|desc"
                                /*,searchConfig: {
                                    filter: '+PATH:"/app:company_home/cm:Business_x0020_platform/cm:LECM/cm:Сервис_x0020_Редактор_x0020_Отчетов/cm:Отчеты/*//*"'
                                }*/
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
<div id="reportsEditor">
</div>
<script type="text/javascript">
    //<![CDATA[
    var reportForm;
    (function () {
        function init() {
            var reportsEditor = new LogicECM.module.ReportsEditor.Editor("reportsEditor");
            reportsEditor.setReportId("${page.url.args.reportId}");
            reportsEditor.setMessages(${messages});
            reportsEditor.draw();

            var menu = new LogicECM.module.ReportsEditor.Menu("menu-buttons");
            menu.setMessages(${messages});
            menu.setEditor(reportsEditor);
            menu.draw();
        }

        YAHOO.util.Event.onDOMReady(init);
    })();
    //]]>
</script>
</#if>
