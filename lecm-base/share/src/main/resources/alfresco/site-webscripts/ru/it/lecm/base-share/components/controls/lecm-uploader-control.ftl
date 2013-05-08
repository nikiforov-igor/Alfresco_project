<#include "/org/alfresco/components/form/controls/common/utils.inc.ftl" />

<#assign fieldValue=field.value!"">
<#assign controlId = fieldHtmlId + "-cntrl">

<#if fieldValue?string == "" && field.control.params.defaultValueContextProperty??>
	<#if context.properties[field.control.params.defaultValueContextProperty]??>
		<#assign fieldValue = context.properties[field.control.params.defaultValueContextProperty]>
	<#elseif args[field.control.params.defaultValueContextProperty]??>
		<#assign fieldValue = args[field.control.params.defaultValueContextProperty]>
	</#if>
</#if>
<#assign disabled = form.mode == "view">

<script type="text/javascript">//<![CDATA[
(function()
{
	var control = new LogicECM.control.Uploader("${fieldHtmlId}").setMessages(${messages});
	control.setOptions(
			{
				<#if disabled>
					disabled: true,
				</#if>
				<#if field.control.params.uploadDirectoryPath??>
					uploadDirectoryPath: "${field.control.params.uploadDirectoryPath}",
				</#if>
				currentValue: "${field.value!''}"
			});
})();
//]]></script>

<div class="form-field">
	<#if disabled>
		<div class="viewmode-field">
			<#if showViewIncompleteWarning && field.mandatory && !(fieldValue?is_number) && fieldValue?string == "">
			<span class="incomplete-warning"><img src="${url.context}/res/components/form/images/warning-16.png" title="${msg("form.field.incomplete")}" /><span>
			</#if>
			<span class="viewmode-label">${field.label?html}:</span>
			<span id="${controlId}-currentValueDisplay" class="viewmode-value"></span>
		</div>
	<#else>
		<label for="${controlId}">${field.label?html}:<#if field.endpointMandatory!false || field.mandatory!false><span class="mandatory-indicator">${msg("form.required.fields.marker")}</span></#if></label>
		<div id="${controlId}" class="object-finder">
			<div id="${controlId}-currentValueDisplay" class="current-values"></div>
			<input type="hidden" id="${controlId}-added" name="${field.name}_added"/>
			<input type="hidden" id="${controlId}-removed" name="${field.name}_removed"/>
			<input type="hidden" id="${controlId}-selectedItems"/>

			<div id="${controlId}-itemGroupActions" class="show-picker">
                <span class="file-upload-button">
                    <input type="button" id="${controlId}-file-upload-button" name="-" value="upload"/>
                </span>
			</div>
			<div class="clear"></div>
		</div>
	</#if>
	<input type="hidden" id="${fieldHtmlId}" name="${field.name}" value="${field.value?html}" />
</div>

<div id="${controlId}-file-uploaders">
	<div id="${controlId}-html-uploader"></div>
	<div id="${controlId}-flash-uploader"></div>
	<div id="${controlId}-file-uploader"></div>
</div>