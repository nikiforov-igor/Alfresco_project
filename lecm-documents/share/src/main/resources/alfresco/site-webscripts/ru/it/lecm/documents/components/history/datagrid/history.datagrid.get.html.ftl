<#import "/ru/it/lecm/base-share/components/lecm-datagrid.ftl" as grid/>

<#assign gridId = args.htmlid/>
<#assign controlId = gridId + "-cntrl">
<#assign containerId = gridId + "-container">
<#assign nodeRef = args.nodeRef/>

<div class="form-field with-grid" id="bjHistory-${controlId}">

<@grid.datagrid containerId true gridId+"form" true>
    <script type="text/javascript">//<![CDATA[
    (function () {
        YAHOO.util.Event.onDOMReady(function (){
            var datagrid = new LogicECM.module.BusinessJournal.DataGrid('${containerId}').setOptions({
                usePagination: true,
                pageSize: 10,
                showExtendSearchBlock: true,
                datagridMeta: {
                    itemType: "lecm-busjournal:bjRecord",
                    datagridFormId: "bjHistory",
                    createFormId: "",
                    nodeRef: "${nodeRef}",
                    sort:"lecm-busjournal:bjRecord-date|false",
                    actionsConfig: {
                        fullDelete: "false"
                    }
                },
                dataSource:"lecm/business-journal/ds/history",
                allowCreate: false,
                showActionColumn: false,
                showCheckboxColumn: false,
                bubblingLabel: "${bubblingLabel!"bj-history-records"}",
                attributeForShow:"lecm-busjournal:bjRecord-date"
            }).setMessages(${messages});

            datagrid.draw();
        });

    })();
    //]]></script>

</@grid.datagrid>
</div>