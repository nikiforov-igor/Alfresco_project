<#import "/ru/it/lecm/base-share/components/lecm-datagrid.ftl" as grid/>

<#assign aDateTime = .now>
<#assign controlId = fieldHtmlId + "-cntrl">
<#assign containerId = fieldHtmlId + "-container-" + aDateTime?iso_utc>
<#assign objectId = field.name?replace("-", "_")>

<#assign allowCreate = true/>
<#if field.control.params.allowCreate??>
	<#assign allowCreate = field.control.params.allowCreate/>
</#if>

<#assign allowDelete = "true"/>
<#if field.control.params.allowDelete??>
    <#assign allowDelete = field.control.params.allowDelete?lower_case/>
</#if>

<#assign allowEdit = "true"/>
<#if field.control.params.allowEdit??>
    <#assign allowEdit = field.control.params.allowEdit?lower_case/>
</#if>

<#assign showActions = true/>
<#if field.control.params.showActions??>
	<#assign showActions = field.control.params.showActions/>
</#if>

<#assign useBubbling = "true"/>
<#if field.control.params.useBubbling??>
    <#assign useBubbling = field.control.params.useBubbling?lower_case/>
<#else>
    <#assign useBubbling = "true"/>
</#if>

<#if useBubbling = "false">
    <#assign bubblingId = ""/>
<#else>
    <#assign bubblingId = containerId/>
</#if>

<#assign usePagination = false/>
<#if field.control.params.usePagination??>
	<#assign usePagination = field.control.params.usePagination/>
</#if>

<div class="form-field with-grid" id="${controlId}">
    <label for="${controlId}" style="white-space: nowrap; overflow: visible;">${field.label?html}:<#if field.endpointMandatory!false || field.mandatory!false>
        <span class="mandatory-indicator">${msg("form.required.fields.marker")}</span></#if></label>
    <@grid.datagrid containerId false>
        <script type="text/javascript">//<![CDATA[
            (function () {

                LogicECM.module.Base.DataGridControl_${objectId} = function(htmlId) {
                    var module = LogicECM.module.Base.DataGridControl_${objectId}.superclass.constructor.call(this, htmlId, ["button", "container", "datasource", "datatable", "paginator", "animation"]);
                    return module;
                };

                YAHOO.extend(LogicECM.module.Base.DataGridControl_${objectId}, LogicECM.module.Base.DataGrid, {
                    ${field.control.params.actionsHandler!""}
                });

                YAHOO.util.Event.onDOMReady(function (){

                    var datagrid = new LogicECM.module.Base.DataGridControl_${objectId}('${containerId}').setOptions({
                        usePagination: ${usePagination?string},
                        showExtendSearchBlock: false,
                        actions: [
                                    <#if field.control.params.actionsDescriptor?? >
                                        ${field.control.params.actionsDescriptor}
                                    </#if>

                                    <#if field.control.params.actionsDescriptor?? && (allowEdit = "true" || allowDelete = "true")>
                                        ,
                                    </#if>

                                    <#if allowEdit = "true">
                                        {
                                            type: "datagrid-action-link-<#if bubblingId != "">${bubblingId}<#else>-custom</#if>",
                                            id: "onActionEdit",
                                            permission: "edit",
                                            label: "${msg("actions.edit")}"
                                        }
                                    </#if>

                                    <#if allowEdit = "true" && allowDelete = "true">
                                        ,
                                    </#if>

                                    <#if allowDelete = "true">
                                        {
                                            type: "datagrid-action-link-<#if bubblingId != "">${bubblingId}<#else>custom</#if>",
                                            id: "onActionDelete",
                                            permission: "delete",
                                            label: "${msg("actions.delete-row")}"
                                        }
                                    </#if>
                                 ],
                        datagridMeta: {
                                itemType: "${field.control.params.itemType!""}",
                                useChildQuery: true,
                                datagridFormId: "${field.control.params.datagridFormId!"datagrid"}",
                                createFormId: "${field.control.params.createFormId!""}",
                                nodeRef: <#if field.value?? && field.value != "">"${field.value}"<#else>"${form.arguments.itemId}"</#if>,
                                actionsConfig: {
                                    fullDelete: "${field.control.params.fullDelete!"false"}"
                                },
                                sort: "${field.control.params.sort!""}"
                            },
                        dataSource:"${field.control.params.ds!"lecm/search"}",
                        <#if bubblingId != "">
                            bubblingLabel: "${bubblingId}",
                        <#else>
                            bubblingLabel: "custom",
                        </#if>
                        <#if field.control.params.height??>
                            height: ${field.control.params.height},
                        </#if>
                        <#if field.control.params.configURL??>
                            configURL: "${field.control.params.configURL}",
                        </#if>
                        <#if field.control.params.repoDatasource??>
                            repoDatasource: ${field.control.params.repoDatasource},
                        </#if>
                        allowCreate: ${allowCreate?string},
                        showActionColumn: ${showActions?string},
                        showCheckboxColumn: false
                        <#if field.control.params.fixedHeader??>
                            ,fixedHeader: ${field.control.params.fixedHeader}
                        </#if>
                    }).setMessages(${messages});

                    datagrid.draw();
                });

            })();
        //]]></script>
    </@grid.datagrid>
</div>
