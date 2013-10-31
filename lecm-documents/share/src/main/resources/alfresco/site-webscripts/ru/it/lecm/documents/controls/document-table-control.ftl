<#import "/ru/it/lecm/base-share/components/lecm-datagrid.ftl" as grid/>
<#import "/ru/it/lecm/base-share/components/base-components.ftl" as comp/>

<#assign params = field.control.params/>

<#assign controlId = fieldHtmlId + "-cntrl">
<#assign toolbarId = fieldHtmlId + "-toolbar">
<#assign aDateTime = .now>
<#assign containerId = fieldHtmlId + "-container-" + aDateTime?iso_utc>
<#assign bubblingId = containerId/>

<#assign showLabel = true>
<#if params.showLabel?? &&  params.showLabel == "false">
    <#assign showLabel = false>
</#if>

<#assign attributeForShow = ""/>
<#if params.attributeForShow??>
    <#assign attributeForShow = params.attributeForShow/>
</#if>

<#assign showCreateButton = true/>
<#if field.control.params.showCreateBtn??>
    <#assign showCreateButton = field.control.params.showCreateBtn/>
</#if>

<script type="text/javascript">//<![CDATA[
(function() {
	var control = new LogicECM.module.DocumentTable("${fieldHtmlId}").setMessages(${messages});
	control.setOptions(
			{
				currentValue: "${field.value!""}",
				messages: ${messages},
				bubblingLabel: "${bubblingId}",
				toolbarId: "${toolbarId}",
				containerId: "${containerId}",
				datagridFormId: "${params.datagridFormId!"datagrid"}",
				attributeForShow: "${attributeForShow}",
				mode: "${form.mode?string}",
				datagridHeight: ${params.height},
				repeating: ${field.repeating?string},
				disabled: ${field.disabled?string}
			});
})();
//]]></script>

<div id="${toolbarId}">
	<@comp.baseToolbar toolbarId true true false>
        <#if showCreateButton>
	    <div class="new-row">
	        <span id="${toolbarId}-newRowButton" class="yui-button yui-push-button">
	           <span class="first-child">
	              <button type="button" title="${msg("label.create-row.title")}">${msg("label.create-row.title")}</button>
	           </span>
	        </span>
	    </div>
        </#if>
	</@comp.baseToolbar>
</div>

<div class="form-field with-grid" id="${controlId}">
    <#if showLabel>
        <label for="${controlId}" style="white-space: nowrap; overflow: visible;">${field.label?html}:
            <#if field.endpointMandatory!false || field.mandatory!false>
                <span class="mandatory-indicator">${msg("form.required.fields.marker")}</span>
            </#if>
        </label>
    </#if>

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