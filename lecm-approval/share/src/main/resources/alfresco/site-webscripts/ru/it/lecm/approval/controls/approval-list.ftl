<#import "/ru/it/lecm/base-share/components/lecm-datagrid.ftl" as grid/>

<#assign aDateTime = .now>
<#assign controlId = fieldHtmlId + "-cntrl">
<#assign containerId = fieldHtmlId + "-container-" + aDateTime?iso_utc>

<#assign allowCreate = false/>
<#assign showActions = false/>
<#assign usePagination = false/>

<div class="form-field with-grid" id="${controlId}">
    <label for="${controlId}" style="white-space: nowrap; overflow: visible;">${field.label?html}:</label>
<@grid.datagrid containerId true "app-list-item-employee-view">
    <script type="text/javascript">//<![CDATA[
    (function () {
        YAHOO.util.Event.onDOMReady(function (){
            var datagrid = new LogicECM.module.Base.DataGrid('${containerId}').setOptions({
                usePagination: ${usePagination?string},
                showExtendSearchBlock: false,
                actions: [],
                datagridMeta: {
                    itemType: "lecm-al:approval-item",
                    datagridFormId: "datagrid",
                    nodeRef: "${form.arguments.itemId}"
                },
                bubblingLabel: "${containerId}",
                allowCreate: ${allowCreate?string},
                showActionColumn: ${showActions?string},
                showCheckboxColumn: false
            }).setMessages(${messages});

            datagrid.draw();
        });

    })();
    //]]></script>
</@grid.datagrid>
</div>
