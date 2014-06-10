<#import "/ru/it/lecm/base-share/components/lecm-datagrid.ftl" as grid/>

<#assign gridId = args.htmlid/>
<#assign controlId = gridId + "-cntrl">
<#assign containerId = gridId + "-container">

<div class="form-field with-grid members-info-grid" id="members-info-${controlId}">

<@grid.datagrid containerId false gridId+"form">
    <script type="text/javascript">//<![CDATA[
    (function () {
        YAHOO.util.Event.onDOMReady(function (){
            var datagrid = new LogicECM.module.Base.DataGrid('${containerId}').setOptions({
                usePagination: true,
                disableDynamicPagination: true,
                pageSize: 10,
                showExtendSearchBlock: true,
                datagridMeta: {
                    itemType: "lecm-orgstr:employee",
                    datagridFormId: "dashlet-info"
                },
                dataSource:"lecm/dashlet/ds/members",
                showActionColumn: false,
                showCheckboxColumn: false,
                bubblingLabel: "${"dashlet-info"}"
            }).setMessages(${messages});
            datagrid.draw();
        });

    })();
    //]]></script>

</@grid.datagrid>
</div>