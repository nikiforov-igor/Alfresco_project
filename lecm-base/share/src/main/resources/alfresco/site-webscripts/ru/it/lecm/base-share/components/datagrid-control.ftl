<#import "/ru/it/lecm/base-share/components/lecm-datagrid.ftl" as grid/>

<#assign controlId = fieldHtmlId + "-cntrl">
<#assign containerId = fieldHtmlId + "-container">

<div class="form-field">
    <div id="${controlId}">
        <label for="${controlId}">${field.label?html}:<#if field.endpointMandatory!false || field.mandatory!false>
            <span class="mandatory-indicator">${msg("form.required.fields.marker")}</span></#if></label>
        <@grid.datagrid containerId>
            <script type="text/javascript">//<![CDATA[
                (function () {
                    YAHOO.util.Event.onDOMReady(function (){
                        var datagrid = new LogicECM.module.Base.DataGrid('${containerId}').setOptions({
                            usePagination: false,
                            showExtendSearchBlock: false,
                            actions: [{
                                            type: "action-link-${containerId}",
                                            id: "onActionEdit",
                                            permission: "edit",
                                            label: "${msg("actions.edit")}"
                                        },
                                        {
                                            type: "action-link-${containerId}",
                                            id: "onActionDelete",
                                            permission: "delete",
                                            label: "${msg("actions.delete-row")}"
                                        }],
                            datagridMeta: {
                                    itemType: "${field.control.params.itemType!""}",
                                    nodeRef: "${form.arguments.itemId}",
                                    searchConfig:{ //настройки поиска (необязателен)
                                        filter:'PARENT: "${form.arguments.itemId}"', // дополнительный запрос(фильтр)
                                        sort: "cm:name|true" // сортировка. Указываем по какому полю и порядок (true - asc), например, cm:name|true
                                    }
                                },
                            bubblingLabel: "${containerId}",
                            height: 100,
                            allowCreate: true
                        }).setMessages(${messages});

                        datagrid.draw();
                    });

                })();
            //]]></script>
        </@grid.datagrid>

    </div>
</div>
