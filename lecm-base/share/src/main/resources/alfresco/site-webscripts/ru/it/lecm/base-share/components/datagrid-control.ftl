<#import "/ru/it/lecm/base-share/components/lecm-datagrid.ftl" as grid/>

<#assign controlId = fieldHtmlId + "-cntrl">
<#assign containerId = fieldHtmlId + "-container">

<#assign allowCreate = true/>
<#if field.control.params.allowCreate??>
	<#assign allowCreate = field.control.params.allowCreate/>
</#if>

<#assign showActions = true/>
<#if field.control.params.showActions??>
	<#assign showActions = field.control.params.showActions/>
</#if>

<div class="form-field with-grid" id="${controlId}">
    <label for="${controlId}">${field.label?html}:<#if field.endpointMandatory!false || field.mandatory!false>
        <span class="mandatory-indicator">${msg("form.required.fields.marker")}</span></#if></label>
    <@grid.datagrid containerId false>
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
                                datagridFormId: "${field.control.params.datagridFormId!"datagrid"}",
                                createFormId: "${field.control.params.createFormId!""}",
                                nodeRef: <#if field.value?? && field.value != "">"${field.value}"<#else>"${form.arguments.itemId}"</#if>,
                                actionsConfig: {
                                    fullDelete: "${field.control.params.fullDelete!"false"}"
                                }
                            },
                        dataSource:"${field.control.params.ds!"lecm/search"}",
                        bubblingLabel: "${containerId}",
                        <#if field.control.params.height??>
                            height: ${field.control.params.height},
                        </#if>
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
