<#include "/org/alfresco/components/form/controls/common/utils.inc.ftl" />
<#include "lecm-dnd-uploader-container.ftl">

<#assign disabled = form.mode == "view">
<#assign params = field.control.params/>

<div class="form-field dnd-uploader">
	<input id="${fieldHtmlId}" type="hidden" class="autocomplete-input" name="${field.name}" value="${field.value?html}"/>
	<input type="hidden" id="${fieldHtmlId}-removed" name="${field.name}_removed"/>
	<input type="hidden" id="${fieldHtmlId}-added" name="${field.name}_added"/>

    <#assign showAttsLabel = true,
        showAttsList = true,
		autoSubmit = false/>
    <#if params.showAttsLabel?? && params.showAttsLabel == "false">
        <#assign showAttsLabel = false/>
    </#if>
	<#if params.autoSubmit?? && params.autoSubmit == "true">
        <#assign autoSubmit = true/>
    </#if>
    <#if params.showAttsList?? && params.showAttsList == "false">
        <#assign showAttsList = false/>
    </#if>
    <#if showAttsLabel>
        <label>${field.label?html}:<#if field.mandatory><span class="mandatory-indicator">${msg("form.required.fields.marker")}</span></#if></label>
    </#if>
    <#if showAttsList>
        <ul id="${fieldHtmlId}-attachments" class="attachments-list"></ul>
    </#if>

	<#assign uploadDirectoryPath = params.uploadDirectoryPath>
	<#assign directoryName = msg(params.directoryNameCode)>
	<@renderDndUploaderContainerHTML fieldHtmlId uploadDirectoryPath directoryName disabled field.endpointMany autoSubmit/>
</div>