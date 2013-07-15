<#include "/org/alfresco/components/form/controls/common/utils.inc.ftl" />
<#include "lecm-dnd-uploader-container.ftl">

<#assign disabled = form.mode == "view">

<div class="form-field">
	<input id="${fieldHtmlId}" type="hidden" class="autocomplete-input" name="${field.name}" value="${field.value?html}"/>
	<input type="hidden" id="${fieldHtmlId}-removed" name="${field.name}_removed"/>
	<input type="hidden" id="${fieldHtmlId}-added" name="${field.name}_added"/>

	<label>${field.label?html}:<#if field.mandatory><span class="mandatory-indicator">${msg("form.required.fields.marker")}</span></#if></label>

	<ul id="${fieldHtmlId}-attachments" style="float: left; width: 200px; margin-left: 0;">

	</ul>

	<#assign uploadDirectoryPath = field.control.params.uploadDirectoryPath>
	<#assign directoryName = msg(field.control.params.directoryNameCode)>
	<@renderDndUploaderContainerHTML fieldHtmlId uploadDirectoryPath directoryName disabled field.endpointMany/>
</div>