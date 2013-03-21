<#if folderRef??>
    <#import "/ru/it/lecm/base-share/components/lecm-datagrid.ftl" as grid/>
    <#assign id = args.htmlid>
    <#assign containerId = id + "-container">

<div class="dashlet document bordered">
    <div class="title dashlet-title">
        <span>${msg("label.title")}</span>
        <span class="lecm-dashlet-actions">
            <a id="${id}-action-expand" href="javascript:void(0);" onclick="documentMembersComponent.onExpand()" class="expand" title="${msg("dashlet.expand.tooltip")}">&nbsp</a>
         </span>
    </div>
    <div class="body scrollableList" id="${id}_results">
        <@grid.datagrid containerId true containerId + "-form">
            <script type="text/javascript">//<![CDATA[
            (function () {
                var datagrid = null;
                YAHOO.util.Event.onDOMReady(function (){
                    datagrid = new LogicECM.module.DocumentMembers.DataGrid('${containerId}').setOptions({
                        usePagination: false,
                        showExtendSearchBlock: false,
                        datagridMeta: {
                            itemType: "lecm-doc-members:member",
                            nodeRef: "${folderRef}"
                        },
                        dataSource:"lecm/search",
                        bubblingLabel: "${containerId}",

                        allowCreate: false,
                        showActionColumn: false,
                        showCheckboxColumn: false
                    }).setMessages(${messages});
                    datagrid.draw();
                });

            })();
            //]]></script>
        </@grid.datagrid>
    </div>
</div>
</#if>