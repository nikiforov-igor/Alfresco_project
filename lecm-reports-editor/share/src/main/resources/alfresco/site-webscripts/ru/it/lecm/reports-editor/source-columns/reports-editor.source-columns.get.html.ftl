<#import "/ru/it/lecm/base-share/components/base-components.ftl" as comp/>
<#import "/ru/it/lecm/base-share/components/lecm-datagrid.ftl" as grid/>

<#assign id = args.htmlid>
<#if page.url.args.sourceId??>
<div class="yui-t1" id="report-editor-sourcesList">
    <div id="yui-main-2">
        <div class="yui-b" id="alf-content" style="margin-left: 0;">
            <@grid.datagrid id=id showViewForm=false showArchiveCheckBox=false>
                <script type="text/javascript">//<![CDATA[
                LogicECM.module.ReportsEditor.ColumnsGrid = function (containerId) {
                    LogicECM.module.ReportsEditor.ColumnsGrid.superclass.constructor.call(this, containerId);
                    return this;
                };

                YAHOO.lang.extend(LogicECM.module.ReportsEditor.ColumnsGrid, LogicECM.module.Base.DataGrid);

                function createColumnsDatagrid() {
                    var datagrid = new LogicECM.module.ReportsEditor.ColumnsGrid('${id}').setOptions(
                            {
                                usePagination: true,
                                useDynamicPagination: false,
                                showExtendSearchBlock: false,
                                showActionColumn: true,
                                overrideSortingWith: false,
                                actions: [
                                    {
                                        type: "datagrid-action-link-sourceColumns",
                                        id: "onActionEdit",
                                        permission: "edit",
                                        label: "${msg("actions.edit")}"
                                    },
                                    {
                                        type: "datagrid-action-link-sourceColumns",
                                        id: "onActionDelete",
                                        permission: "delete",
                                        label: "${msg("actions.delete-row")}",
                                        evaluator: function (rowData) {
                                            var itemData = rowData.itemData;
                                            return this.isActiveItem(itemData);
                                        }
                                    }
                                ],
                                bubblingLabel: "sourceColumns",
                                showCheckboxColumn: false
                            }).setMessages(${messages});

                    YAHOO.util.Event.onContentReady('${id}', function () {
                        YAHOO.Bubbling.fire("activeGridChanged", {
                            datagridMeta: {
                                itemType: "lecm-rpeditor:reportDataColumn",
                                nodeRef: "${page.url.args.sourceId}",
                                actionsConfig: {
                                    fullDelete: true,
                                    trash: false
                                },
                                sort: "lecm-rpeditor:dataColumnCode|true"
                            },
                            bubblingLabel: "sourceColumns"
                        });
                    });
                }

                function initColumnsDatagrid() {
                    createColumnsDatagrid();
                }

                YAHOO.util.Event.onDOMReady(initColumnsDatagrid);
                //]]></script>
            </@grid.datagrid>
        </div>
    </div>
</div>
<#else>
    <div>${msg("label.unavaiable-page")}</div>
</#if>
