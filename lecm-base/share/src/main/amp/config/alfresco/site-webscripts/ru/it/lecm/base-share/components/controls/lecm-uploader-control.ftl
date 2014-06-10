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
	function init() {
        LogicECM.module.Base.Util.loadScripts([
            'components/upload/dnd-upload.js',
            'components/upload/html-upload.js',
            'components/upload/file-upload.js',
            'components/upload/flash-upload.js',
            'scripts/lecm-base/components/lecm-uploader-initializer.js',
            '/scripts/lecm-base/components/lecm-uploader-control.js'
		], createUploader);
	}
	function createUploader(){
		var control = new LogicECM.control.Uploader("${fieldHtmlId}").setMessages(${messages});
		control.setOptions({
			<#if disabled>
				disabled: true,
			</#if>
            <#if showImage>
                showImage: true,
            </#if>
            <#if multiple>
                multiple: true,
            </#if>
            <#if field.mandatory??>
                mandatory: ${field.mandatory?string},
            </#if>
			<#if field.control.params.uploadDirectoryPath??>
				uploadDirectoryPath: "${field.control.params.uploadDirectoryPath}",
			</#if>
			currentValue: "${field.value!''}"
		});
	}
	YAHOO.util.Event.onDOMReady(init);
})();
//]]></script>

<div class="control uploader-control <#if disabled>viewmode<#else>editmode</#if>">
    <#if showImage>
        <div class="thumbnail-container" id="${controlId}-container"></div>
    </#if>
	<#if disabled>
        <#if !hideItem>
	        <div class="label-div">
		        <#if showViewIncompleteWarning?? && field.mandatory && !(fieldValue?is_number) && fieldValue?string == "">
		            <span class="incomplete-warning"><img src="${url.context}/res/components/form/images/warning-16.png" title="${msg("form.field.incomplete")}"/><span>
		        </#if>
		        <label>${field.label?html}:</label>
	        </div>
	        <div class="container">
		        <div class="value-div">
			        <input type="hidden" id="${fieldHtmlId}" name="${field.name}" value="${field.value?html}" />
			        <span id="${controlId}-currentValueDisplay" class="mandatory-highlightable"></span>
		        </div>
	        </div>
        <#else>
	        <input type="hidden" id="${fieldHtmlId}" name="${field.name}" value="${field.value?html}" />
        </#if>
	<#else>
		<div class="label-div <#if showImage>hidden1</#if>">
			<label for="${controlId}">
			${field.label?html}:
				<#if field.endpointMandatory!false || field.mandatory!false><span class="mandatory-indicator">${msg("form.required.fields.marker")}</span></#if>
			</label>
		</div>
		<div class="container" id="${controlId}">
			<input type="hidden" id="${controlId}-added" name="${field.name}_added"/>
			<input type="hidden" id="${controlId}-removed" name="${field.name}_removed"/>
			<input type="hidden" id="${controlId}-selectedItems"/>

			<div class="buttons-div">
				<span class="file-upload-button">
                    <input type="button" id="${controlId}-file-upload-button" name="-" value="${msg("lecm.form.upload")}"/>
                </span>
			</div>
			<div class="value-div">
				<input type="hidden" id="${fieldHtmlId}" name="${field.name}" value="${field.value?html}" />
				<div id="${controlId}-currentValueDisplay" class="control-selected-values mandatory-highlightable <#if showImage>hidden1</#if>"></div>
			</div>
		</div>
	</#if>
</div>
<div class="clear"></div>
