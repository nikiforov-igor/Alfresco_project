<#import "/ru/it/lecm/base-share/components/lecm-datagrid.ftl" as grid/>

<#assign controlId = fieldHtmlId + "-cntrl">
<#assign containerId = fieldHtmlId + "-container">
<div class="form-field">
    <div id="${controlId}">
        <label for="${controlId}">${field.label?html}:<#if field.endpointMandatory!false || field.mandatory!false>
            <span class="mandatory-indicator">${msg("form.required.fields.marker")}</span></#if></label>
        <div id="${containerId}></div>
        <@grid.datagrid containerId>
            <script type="text/javascript">//<![CDATA[
                (function () {
                    YAHOO.util.Event.onDOMReady(function (){
                        var datagrid = new LogicECM.module.Base.DataGrid('${containerId}').setOptions({
                            usePagination: false,
                            showExtendSearchBlock: false,
                            actions: [{
                                            type: "action-link",
                                            id: "onActionEdit",
                                            permission: "edit",
                                            label: "${msg("actions.edit")}"
                                        },
                                        {
                                            type: "action-link",
                                            id: "onActionDelete",
                                            permission: "delete",
                                            label: "${msg("actions.delete-row")}"
                                        }],
                            datagridMeta: {
                                    itemType: "${field.control.params.itemType!""}",
                                    nodeRef: "${form.arguments.itemId}"
                                }
                        }).setMessages(${messages});

                        datagrid.draw();
                    });

                })();
            //]]></script>
        </@grid.datagrid>

    </div>
</div>
