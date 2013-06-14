<#include "/org/alfresco/components/form/controls/common/utils.inc.ftl" />

<#assign fieldValue=field.value!"">
<#assign controlId = fieldHtmlId + "-cntrl">
<#assign showImage = false>
<#assign hideItem = false>
<#assign multiple = false>

<#if fieldValue?string == "" && field.control.params.defaultValueContextProperty??>
	<#if context.properties[field.control.params.defaultValueContextProperty]??>
		<#assign fieldValue = context.properties[field.control.params.defaultValueContextProperty]>
	<#elseif args[field.control.params.defaultValueContextProperty]??>
		<#assign fieldValue = args[field.control.params.defaultValueContextProperty]>
	</#if>
</#if>
<#assign disabled = form.mode == "view">
<#if field.control.params.showImage?? && field.control.params.showImage=="true">
    <#assign showImage = true>
</#if>
<#if field.control.params.hideItem?? && field.control.params.hideItem=="true">
    <#assign hideItem = true>
</#if>
<#if field.control.params.multiple?? && field.control.params.multiple=="true">
    <#assign multiple = true>
</#if>

<script type="text/javascript">//<![CDATA[
(function()
{
	var control = new LogicECM.control.Uploader("${fieldHtmlId}").setMessages(${messages});
	control.setOptions(
			{
				<#if disabled>
					disabled: true,
				</#if>
                <#if showImage>
                    showImage: true,
                </#if>
                <#if disabled>
                    multiple: true,
                </#if>

				<#if field.control.params.uploadDirectoryPath??>
					uploadDirectoryPath: "${field.control.params.uploadDirectoryPath}",
				</#if>
				currentValue: "${field.value!''}"
			});
})();
//]]></script>

<div class="form-field">
    <#if showImage>
        <div class="yui-dt45-col-thumbnail yui-dt-col-thumbnail" style="width: 100px;">
            <div class="yui-dt-liner" style="width: 100px;" id="${controlId}-container"></div>
        </div>
    </#if>
	<#if disabled>
        <#if !hideItem>
            <div class="viewmode-field">
                <#if showViewIncompleteWarning?? && showViewIncompleteWarning && field.mandatory && !(fieldValue?is_number) && fieldValue?string == "">
                    <span class="incomplete-warning"><img src="${url.context}/res/components/form/images/warning-16.png" title="${msg("form.field.incomplete")}"/><span>
                </#if>
                <span class="viewmode-label">${field.label?html}:</span>
                <span id="${controlId}-currentValueDisplay" class="viewmode-value"></span>
            </div>
        </#if>
	<#else>
		<label for="${controlId}">${field.label?html}:<#if field.endpointMandatory!false || field.mandatory!false><span class="mandatory-indicator">${msg("form.required.fields.marker")}</span></#if></label>
		<div id="${controlId}" class="object-finder">
			<div id="${controlId}-currentValueDisplay" class="current-values"></div>
			<input type="hidden" id="${controlId}-added" name="${field.name}_added"/>
			<input type="hidden" id="${controlId}-removed" name="${field.name}_removed"/>
			<input type="hidden" id="${controlId}-selectedItems"/>

			<div id="${controlId}-itemGroupActions" class="show-picker">
                <span class="file-upload-button">
                    <input type="button" id="${controlId}-file-upload-button" name="-" value="${msg("lecm.form.upload")}"/>
                </span>
			</div>
			<div class="clear"></div>
		</div>
	</#if>
	<input type="hidden" id="${fieldHtmlId}" name="${field.name}" value="${field.value?html}" />
</div>