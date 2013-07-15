<#include "/org/alfresco/components/form/controls/common/utils.inc.ftl" />

<#assign controlId = fieldHtmlId + "-cntrl">
<#assign disabled = form.mode == "view">

<style type="text/css">
	.dndDocListHighlight
	{
		outline: 2px solid #4F94C9;
		outline-offset: -2px;
	}
</style>

<script type="text/javascript">//<![CDATA[
(function()
{
	var control = new LogicECM.control.DndUploader("${fieldHtmlId}").setMessages(${messages});
	control.setOptions(
			{
			<#if disabled>
				disabled: true,
			</#if>
			<#if field.control.params.uploadDirectoryPath??>
				uploadDirectoryPath: "${field.control.params.uploadDirectoryPath}",
			</#if>
				multipleMode: true
			});
})();
//]]></script>

<div class="form-field">
	<input id="${fieldHtmlId}" type="hidden" class="autocomplete-input" name="${field.name}" value="${field.value?html}"/>
	<input type="hidden" id="${controlId}-removed" name="${field.name}_removed"/>
	<input type="hidden" id="${controlId}-added" name="${field.name}_added"/>

	<label>${field.label?html}:<#if field.mandatory><span class="mandatory-indicator">${msg("form.required.fields.marker")}</span></#if></label>

	<ul id="${controlId}-attachments" style="float: left; width: 200px; margin-left: 0;">

	</ul>

	<div id="${controlId}-uploader-block" style="width: 250px; border: 1px solid; margin-left: 450px; height: 100px; text-align: center;">
		${msg("label.add-file")}
	</div>
</div>