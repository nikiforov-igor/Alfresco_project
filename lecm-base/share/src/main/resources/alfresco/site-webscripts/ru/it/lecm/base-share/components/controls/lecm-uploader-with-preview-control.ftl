<#include "/org/alfresco/components/form/controls/common/utils.inc.ftl" />
<#include "lecm-dnd-uploader-container.ftl">

<#assign params = field.control.params/>

<div class="form-field dnd-uploader">
	<input id="${fieldHtmlId}" type="hidden" class="autocomplete-input" name="${field.name}" value="${field.value?html}"/>
	<input type="hidden" id="${fieldHtmlId}-removed" name="${field.name}_removed"/>
	<input type="hidden" id="${fieldHtmlId}-added" name="${field.name}_added"/>

	<#assign disabled = form.mode == "view">
	<#assign showUploadNewVersion = false/>
	<#if params.showUploadNewVersion?? && params.showUploadNewVersion == "true">
		<#assign showUploadNewVersion = true/>
	</#if>

	<script type="text/javascript">//<![CDATA[
	(function() {
		var control = new LogicECM.control.UploaderWithPreviw("${fieldHtmlId}").setMessages(${messages});
		control.setOptions(
				{
					uploadDirectoryPath: "${params.uploadDirectoryPath}",
					disabled: ${disabled?string},
					multipleMode: ${field.endpointMany?string},
					showUploadNewVersion: ${showUploadNewVersion?string},
					directoryName: "${msg(params.directoryNameCode)}",
					currentValue: "${field.value!""}"
				});
	})();
	//]]></script>

	<#if !disabled>
		<div id="${fieldHtmlId}-uploader-block" class="big-uploader-block">
			<fieldset>
				<legend>${msg("label.add-file")}</legend>
				<img id="${fieldHtmlId}-uploader-button" src="/share/res/images/lecm-base/components/plus-big.png" alt="" class="uploader-button">  <br/>
				<span class="drag-tip">${msg("label.drag-file")}</span>
			</fieldset>
		</div>
	</#if>
	<div id="${fieldHtmlId}-uploader-preview-container">

	</div>
</div>