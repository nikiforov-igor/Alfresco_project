<#include "/org/alfresco/components/form/controls/common/utils.inc.ftl" />

<#assign  formId = args.htmlid?js_string/>
<#assign fieldValue=field.value!"">
<#assign fieldId=field.id!"">

<#if fieldValue?string == "" && field.control.params.defaultValueContextProperty??>
	<#if context.properties[field.control.params.defaultValueContextProperty]??>
		<#assign fieldValue = context.properties[field.control.params.defaultValueContextProperty]>
	<#elseif args[field.control.params.defaultValueContextProperty]??>
		<#assign fieldValue = args[field.control.params.defaultValueContextProperty]>
	</#if>
</#if>

<#if field.control.params.notSelectedOptionShow?? && field.control.params.notSelectedOptionShow == "true">
	<#assign notSelectedOptionShow = true>
<#else>
	<#assign notSelectedOptionShow = false>
</#if>

<#assign disabled = form.mode == "view" || (field.disabled && !(field.control.params.forceEditable?? && field.control.params.forceEditable == "true"))>

<script type="text/javascript">//<![CDATA[
(function () {
	var typeSelector = new LogicECM.module.SelectDocumentCategory("${fieldHtmlId}").setOptions(
			{
			<#if field.mandatory??>
				mandatory: ${field.mandatory?string},
			<#elseif field.endpointMandatory??>
				mandatory: ${field.endpointMandatory?string},
			</#if>
				notSelectedOptionShow: ${notSelectedOptionShow?string}
			}).setMessages(${messages});
})();
//]]></script>

<div class="form-field">
<#if disabled>
	<div class="viewmode-field">
		<#if field.mandatory && !(fieldValue?is_number) && fieldValue?string == "">
		<span class="incomplete-warning"><img src="${url.context}/res/components/form/images/warning-16.png"
		                                      title="${msg("form.field.incomplete")}"/><span>
		</#if>
		<span class="viewmode-label">${field.label?html}:</span>
		<span id="${fieldHtmlId}-currentValueDisplay" class="viewmode-value"></span>
		<input type="hidden" id="${fieldHtmlId}" name="${field.name}" value="${field.value?html}"/>
	</div>
<#else>
	<label for="${fieldHtmlId}-added">${field.label?html}:<#if field.mandatory><span
			class="mandatory-indicator">${msg("form.required.fields.marker")}</span></#if></label>
	<input type="hidden" id="${fieldHtmlId}-removed" name="${field.name}_removed" value="${fieldValue}"/>

	<div id="${fieldHtmlId}-controls" class="selectone-control">
		<select id="${fieldHtmlId}" name="${field.name}" tabindex="0"
		        <#if field.description??>title="${field.description}"</#if>
		        <#if field.control.params.size??>size="${field.control.params.size}"</#if>
		        <#if field.control.params.styleClass??>class="${field.control.params.styleClass}"</#if>
		        <#if field.control.params.style??>style="${field.control.params.style}"</#if>
		        <#if field.disabled  && !(field.control.params.forceEditable?? && field.control.params.forceEditable == "true")>disabled="true"</#if>>
			<#if notSelectedOptionShow>
				<option value="">
					<#if field.control.params.notSelectedOptionLabel??>
	                        ${field.control.params.notSelectedOptionLabel}
	                    <#elseif field.control.params.notSelectedOptionLabelCode??>
					${msg(field.control.params.notSelectedOptionLabelCode)}
					</#if>
				</option>
			</#if>
		</select>
	</div>
</#if>
</div>
