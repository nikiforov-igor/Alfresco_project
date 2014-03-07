<#include "/org/alfresco/components/form/controls/common/utils.inc.ftl" />

<#assign fieldValue=field.value!"">
<#assign controlId = fieldHtmlId + "-cntrl">

<#assign disabled = form.mode == "view" || (field.disabled && !(field.control.params.forceEditable?? && field.control.params.forceEditable == "true"))>

<script type="text/javascript">//<![CDATA[
(function()
{
	var control = new LogicECM.module.TextCheckboxesControl("${fieldHtmlId}").setMessages(${messages});
	control.setOptions(
			{
				<#if disabled>
					disabled: true,
				</#if>
				<#if field.control.params.dataSource??>
					dataSource: "${field.control.params.dataSource}",
				</#if>
                <#if field.control.params.singleValue??>
                    singleValue: ${field.control.params.singleValue},
                </#if>
				<#if field.mandatory??>
					mandatory: ${field.mandatory?string},
				<#elseif field.endpointMandatory??>
					mandatory: ${field.endpointMandatory?string},
				</#if>
				itemId: "${form.arguments.itemId}",
				<#if form.destination??>
					destination: "${form.destination}",
				</#if>
				currentValues: "${field.value!''}".split(",")
			});
})();
//]]></script>

<div class="form-field">
<#if disabled>
	<div class="viewmode-field">
		<#if field.mandatory && !(fieldValue?is_number) && fieldValue?string == "">
		<span class="incomplete-warning">
			<img src="${url.context}/res/components/form/images/warning-16.png" title="${msg("form.field.incomplete")}" />
		<span>
		</#if>
		<span class="viewmode-label">${field.label?html}:</span>
	</div>
<#else>
	<label for="${controlId}-checkboxes">
	${field.label?html}:
		<#if field.mandatory>
			<span class="mandatory-indicator">
			${msg("form.required.fields.marker")}
			</span>
		</#if>
	</label>
</#if>
	<ul id="${controlId}-checkboxes"></ul>
	<input type="hidden" id="${fieldHtmlId}" name="${field.name}" value="${field.value?html}"/>
</div>