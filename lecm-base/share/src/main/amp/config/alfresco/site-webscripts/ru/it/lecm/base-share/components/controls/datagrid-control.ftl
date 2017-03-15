<#import "/ru/it/lecm/base-share/components/lecm-datagrid.ftl" as grid/>

<#assign aDateTime = .now>
<#assign controlId = fieldHtmlId + "-cntrl">
<#assign containerId = fieldHtmlId + "-container-" + (aDateTime?iso_utc)?replace(":", "_")>
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

<#assign showLabel = true/>
<#if field.control.params.showLabel??>
	<#assign showLabel = field.control.params.showLabel == "true"/>
</#if>

<div class="control with-grid" id="${controlId}">
	<#if showLabel>
		<label for="${controlId}">${field.label?html}:<#if field.endpointMandatory!false || field.mandatory!false>
			<span class="mandatory-indicator">${msg("form.required.fields.marker")}</span></#if></label>
	</#if>
    <@grid.datagrid containerId false>
        <script type="text/javascript">//<![CDATA[
        (function () {
			function init() {
                LogicECM.module.Base.Util.loadScripts([
                    'scripts/lecm-base/components/advsearch.js',
                    'scripts/lecm-base/components/lecm-datagrid.js'
                ], createDatagrid);
			}
			YAHOO.util.Event.onDOMReady(init);
			function createDatagrid() {
                LogicECM.module.Base.DataGridControl_${objectId} = function(htmlId) {
                    var module = LogicECM.module.Base.DataGridControl_${objectId}.superclass.constructor.call(this, htmlId, ["button", "container", "datasource", "datatable", "paginator", "animation"]);
                    return module;
                };

                YAHOO.extend(LogicECM.module.Base.DataGridControl_${objectId}, LogicECM.module.Base.DataGrid, {
                    ${field.control.params.actionsHandler!""}
                });


                var datagrid = new LogicECM.module.Base.DataGridControl_${objectId}('${containerId}').setOptions({
                    usePagination: ${usePagination?string},
                    <#if field.control.params.overrideSortingWith??>
                        overrideSortingWith: ${field.control.params.overrideSortingWith?string},
                    </#if>
                    <#if field.control.params.editFormId??>
                        editForm: "${field.control.params.editFormId?string}",
                    </#if>
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
                            itemType: "${field.control.params.itemType!field.endpointType!""}",
                            useChildQuery: true,
                            useFilterByOrg: false,
                            datagridFormId: "${field.control.params.datagridFormId!"datagrid"}",
                            createFormId: "${field.control.params.createFormId!""}",
                            nodeRef: <#if field.value?? && field.value != "">"${field.value}"<#else>"${form.arguments.itemId}"</#if>,
                            actionsConfig: {
                                fullDelete: "${field.control.params.fullDelete!"false"}"
                            },
                            sort: "${field.control.params.sort!"cm:name|true"}"
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
		                <#if field.control.params.createFormTitleMsg??>
			                createFormTitleMsg: "${field.control.params.createFormTitleMsg?string}",
		                </#if>
                        allowCreate: ${allowCreate?string},
                        showActionColumn: ${showActions?string},
                        showCheckboxColumn: false
                        <#if field.control.params.fixedHeader??>
                            ,fixedHeader: ${field.control.params.fixedHeader}
                        </#if>
                        <#if field.control.params.editFormWidth??>
                            ,editFormWidth: "${field.control.params.editFormWidth}"
                        </#if>
                    }).setMessages(${messages});
                datagrid.draw();
            }
        })();
        //]]></script>
    </@grid.datagrid>
</div>
<div class="clear"></div>
