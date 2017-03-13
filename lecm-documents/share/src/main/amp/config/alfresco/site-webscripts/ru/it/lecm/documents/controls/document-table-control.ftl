<#import "/ru/it/lecm/base-share/components/lecm-datagrid.ftl" as grid/>
<#import "/ru/it/lecm/base-share/components/base-components.ftl" as comp/>

<@markup id="lecm-datagrid-dependencies" target="js" action="after" scope="global">
	<@script type="text/javascript" src="${url.context}/res/scripts/lecm-base/components/lecm-toolbar.js"/>
	<@script type="text/javascript" src="${url.context}/res/scripts/lecm-base/components/lecm-datagrid.js"></@script>
	<@script type="text/javascript" src="${url.context}/res/scripts/documents/tables/lecm-document-table.js"></@script>
</@>
<#assign params = field.control.params/>

<#assign controlId = fieldHtmlId + "-cntrl">
<#assign toolbarId = fieldHtmlId + "-toolbar">
<#assign aDateTime = .now>
<#assign containerId = fieldHtmlId + "-container-" + (aDateTime?iso_utc)?replace(":", "_")>
<#assign bubblingId = containerId/>

<#assign expandable="false"/>
<#if params.expandable??>
    <#assign expandable = params.expandable/>
</#if>

<#assign showSearch=false/>
<#if params.showSearch?has_content>
    <#assign showSearch = params.showSearch=="true"/>
</#if>

<#assign showLabel = false>
<#if field.control.params.showLabel?? &&  field.control.params.showLabel == "true">
    <#assign showLabel = true>
</#if>

<#assign dataSource=""/>
<#if params.dataSource?has_content>
    <#assign dataSource = params.dataSource/>
</#if>

<#assign allowCreate = "true"/>
<#if field.control.params.allowCreate??>
	<#assign allowCreate = field.control.params.allowCreate?lower_case/>
</#if>

<#assign allowDelete = "true"/>
<#if field.control.params.allowDelete??>
    <#assign allowDelete = field.control.params.allowDelete?lower_case/>
</#if>

<#assign allowEdit = "true"/>
<#if field.control.params.allowEdit??>
    <#assign allowEdit = field.control.params.allowEdit?lower_case/>
</#if>


<#assign expandDataSource=""/>
<#if params.expandDataSource?has_content>
    <#assign expandDataSource = params.expandDataSource/>
</#if>


<#assign attributeForShow = ""/>
<#if params.attributeForShow??>
    <#assign attributeForShow = params.attributeForShow/>
</#if>

<#assign toolbar = "true"/>
<#if params.toolbar??>
    <#assign toolbar = params.toolbar/>
</#if>

<#assign refreshAfterCreate = "false"/>
<#if params.refreshAfterCreate??>
    <#assign refreshAfterCreate = params.refreshAfterCreate/>
</#if>

<#assign showActions = "true"/>
<#if params.showActions??>
    <#assign showActions = params.showActions/>
</#if>

<#assign usePagination = "true"/>
<#if params.usePagination??>
    <#assign usePagination = params.usePagination/>
</#if>

<#assign showCreateButton = !field.control.params.showCreateBtn?? || "true" == field.control.params.showCreateBtn?lower_case/>

<#assign isTableSortable = false/>
<#if field.control.params.isTableSortable??>
    <#assign isTableSortable = field.control.params.isTableSortable/>
</#if>

<#assign sort = ""/>
<#if field.control.params.sort??>
    <#assign sort = field.control.params.sort/>
</#if>

<#assign jsObjectName = "LogicECM.module.DocumentTable"/>
<#if field.control.params.jsObjectName??>
    <#assign jsObjectName = field.control.params.jsObjectName/>
</#if>

<#assign isFieldMandatory = false>
<#if field.control.params.mandatory??>
    <#if field.control.params.mandatory == "true">
        <#assign isFieldMandatory = true>
    </#if>
<#elseif field.mandatory??>
    <#assign isFieldMandatory = field.mandatory>
<#elseif field.endpointMandatory??>
    <#assign isFieldMandatory = field.endpointMandatory>
</#if>

<#assign controlMode = form.mode?string >

<#if ((params.forceEditable!"false")=="true") >
	<#assign controlMode = "edit" >
</#if>

<#assign newRowDialogTitle = ''>
<#if params.newRowDialogTitle??>
	<#assign newRowDialogTitle = params.newRowDialogTitle>
</#if>
<#assign createItemBtnMsg = ''>
<#if params.createItemBtnMsg??>
	<#assign createItemBtnMsg = params.createItemBtnMsg>
</#if>

<#assign editable = ((params.editable!"true") == "true") && !(field.disabled) && (controlMode?string=="edit") >

<script type="text/javascript">//<![CDATA[
(function() {
	function drawForm(){
		var control = new ${jsObjectName}("${fieldHtmlId}").setMessages(${messages});
		control.setOptions(
			{
				currentValue: "${field.value!""}",
                usePagination: ${usePagination?string},
				messages: ${messages},
				bubblingLabel: "${bubblingId}",
                <#if toolbar == "true">
				toolbarId: "${toolbarId}",
                </#if>
				containerId: "${containerId}",
				datagridFormId: "${params.datagridFormId!"datagrid"}",
				attributeForShow: "${attributeForShow}",
				mode: "${controlMode?string}",
				disabled: ${field.disabled?string},
				isTableSortable: ${isTableSortable?string},
                sort: "${sort?string}",
                externalCreateId: "${form.arguments.externalCreateId!""}",
                itemId: "${form.arguments.itemId!""}",
                refreshAfterCreate: ${refreshAfterCreate?string},
				allowCreate: ${allowCreate},
				allowDelete: ${allowDelete},
				allowEdit: ${allowEdit},
				<#if params.deleteMessageFunction??>
					deleteMessageFunction: "${params.deleteMessageFunction}",
				</#if>
				<#if params.editFormTitleMsg??>
					editFormTitleMsg: "${params.editFormTitleMsg}",
				</#if>
			    <#if params.createFormTitleMsg??>
				    createFormTitleMsg: "${params.createFormTitleMsg}",
				</#if>
				<#if params.viewFormTitleMsg??>
					viewFormTitleMsg: "${params.viewFormTitleMsg}",
				</#if>
                expandable: ${expandable?string},
                <#if expandDataSource?has_content>
                    expandDataSource: "${expandDataSource}",
                </#if>
                <#if dataSource?has_content>
                    dataSource: "${dataSource}",
                </#if>
				<#if newRowDialogTitle?has_content>
					newRowDialogTitle: "${newRowDialogTitle}",
				</#if>
				<#if createItemBtnMsg?has_content>
					createItemBtnMsg: "${createItemBtnMsg}",
				</#if>
                showActions: <#if editable>${showActions?string}<#else>false</#if>
			});
	}
	function init() {
        LogicECM.module.Base.Util.loadResources([
	        'scripts/lecm-base/components/advsearch.js',
            'modules/simple-dialog.js',
	        'scripts/lecm-base/components/lecm-datagrid.js',
            'scripts/lecm-base/components/lecm-toolbar.js',
            'scripts/documents/tables/lecm-document-table.js',
	        <#if params.jsDependencies??>
		        <#list params.jsDependencies?split(",") as js>
			        '${js}',
		        </#list>
	        </#if>
		],
		[
            'css/components/document-table-control.css',
	        <#if params.cssDependencies??>
		        <#list params.cssDependencies?split(",") as css>
			        '${css}',
		        </#list>
	        </#if>
        ], drawForm);
    }
	YAHOO.util.Event.onDOMReady(init);
})();
//]]></script>

<div class="control document-table-control with-grid">
	<div class="label-div">
		<#if showLabel>
			<label for="${fieldHtmlId}">
			${field.label?html}:
				<#if isFieldMandatory><span class="mandatory-indicator">${msg("form.required.fields.marker")}</span></#if>
			</label>
		</#if>
	</div>
    <div class="container document-table-width">
        <div class="value-div">
        <#if toolbar == "true" && controlMode?string=="edit">
            <div id="${toolbarId}">
                <@comp.baseToolbar toolbarId true showSearch false>
                    <#if showCreateButton>
                        <div class="new-row">
                                    <span id="${toolbarId}-newRowButton" class="yui-button yui-push-button">
                                       <span class="first-child">
                                          <button type="button"
                                                  title="${msg("label.table.row.create.title")}">${msg("label.table.row.create.title")}</button>
                                       </span>
                                    </span>
                        </div>
                    </#if>
                </@comp.baseToolbar>
            </div>
        </#if>
            <div id="${controlId}">
                <@grid.datagrid containerId false>
                    <div class="hidden1">
                        <!-- Action Set "More..." container -->
                        <div id="${containerId}-otherMoreActions">
                            <div class="onActionShowMore"><a href="#" class="show-more"
                                                             title="${msg("actions.more")}"><span></span></a>
                            </div>
                            <div class="more-actions hidden"></div>
                        </div>
                        <div id="${containerId}-otherActionSet" class="action-set simple"></div>
                    </div>
                </@grid.datagrid>
                 <input type="hidden" id="${fieldHtmlId}" name="${field.name}" value="${field.value?html}"/>
            </div>
        </div>
    </div>
</div>
<div class="clear"></div>
