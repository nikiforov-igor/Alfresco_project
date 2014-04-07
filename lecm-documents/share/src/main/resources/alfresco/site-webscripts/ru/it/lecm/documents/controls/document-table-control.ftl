<#import "/ru/it/lecm/base-share/components/lecm-datagrid.ftl" as grid/>
<#import "/ru/it/lecm/base-share/components/base-components.ftl" as comp/>

<#assign params = field.control.params/>

<#assign controlId = fieldHtmlId + "-cntrl">
<#assign toolbarId = fieldHtmlId + "-toolbar">
<#assign aDateTime = .now>
<#assign containerId = fieldHtmlId + "-container-" + aDateTime?iso_utc>
<#assign bubblingId = containerId/>

<#assign expandable="false"/>
<#if params.expandable??>
    <#assign expandable = params.expandable/>
</#if>

<#assign showSearch=false/>
<#if params.showSearch?has_content>
    <#assign showSearch = params.showSearch=="true"/>
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

<#assign showCreateButton = true/>
<#if field.control.params.showCreateBtn??>
    <#assign showCreateButton = field.control.params.showCreateBtn/>
</#if>

<#assign isTableSortable = false/>
<#if field.control.params.isTableSortable??>
    <#assign isTableSortable = field.control.params.isTableSortable/>
</#if>

<script type="text/javascript">//<![CDATA[
(function() {
	var control = new LogicECM.module.DocumentTable("${fieldHtmlId}").setMessages(${messages});
	control.setOptions(
			{
				currentValue: "${field.value!""}",
				messages: ${messages},
				bubblingLabel: "${bubblingId}",
                <#if toolbar == "true">
				toolbarId: "${toolbarId}",
                </#if>
				containerId: "${containerId}",
				datagridFormId: "${params.datagridFormId!"datagrid"}",
				attributeForShow: "${attributeForShow}",
				mode: "${form.mode?string}",
				disabled: ${field.disabled?string},
				isTableSortable: ${isTableSortable?string},
                externalCreateId: "${form.arguments.externalCreateId!""}",
                refreshAfterCreate: ${refreshAfterCreate?string},
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
                showActions: ${showActions?string}
			});
})();
//]]></script>
<#if toolbar == "true" && form.mode?string=="edit">
<div id="${toolbarId}">
	<@comp.baseToolbar toolbarId true showSearch false>
        <#if showCreateButton>
	    <div class="new-row">
	        <span id="${toolbarId}-newRowButton" class="yui-button yui-push-button">
	           <span class="first-child">
	              <button type="button" title="${msg("label.table.row.create.title")}">${msg("label.table.row.create.title")}</button>
	           </span>
	        </span>
	    </div>
        </#if>
	</@comp.baseToolbar>
</div>
</#if>

<div class="form-field with-grid" id="${controlId}">
	<@grid.datagrid containerId false>
	    <div style="display:none">
	        <!-- Action Set "More..." container -->
	        <div id="${containerId}-otherMoreActions">
	            <div class="onActionShowMore"><a href="#" class="show-more" title="${msg("actions.more")}"><span></span></a></div>
	            <div class="more-actions hidden"></div>
	        </div>
	        <div id="${containerId}-otherActionSet" class="action-set simple"></div>
	    </div>
	</@grid.datagrid>

	<div id="${controlId}-container">
	    <input type="hidden" id="${fieldHtmlId}" name="${field.name}" value="${field.value?html}"/>
	</div>
</div>