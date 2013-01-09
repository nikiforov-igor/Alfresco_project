<#include "/org/alfresco/components/form/controls/common/utils.inc.ftl" />

<#assign fieldValue=field.value!"">
<#assign controlId = fieldHtmlId + "-cntrl">

<script type="text/javascript">//<![CDATA[
(function()
{
	var control = new LogicECM.module.AssociationCheckboxes("${fieldHtmlId}").setMessages(${messages});
	control.setOptions(
			{
				<#if form.mode == "view" || field.disabled>
					disabled: true,
				</#if>
				<#if field.control.params.parentNodeRef??>
					parentNodeRef: "${field.control.params.parentNodeRef}",
				</#if>
				<#if field.control.params.startLocation??>
					startLocation: "${field.control.params.startLocation}",
				</#if>
				<#if field.mandatory??>
					mandatory: ${field.mandatory?string},
				<#elseif field.endpointMandatory??>
					mandatory: ${field.endpointMandatory?string},
				</#if>

				itemType: "${field.endpointType}",
				maxSearchResults: ${field.control.params.maxSearchResults!'1000'},
				currentValue: "${field.value!''}",
				nameSubstituteString: "${field.control.params.nameSubstituteString!'{cm:name}'}"
			});
})();
//]]></script>

<div class="form-field">
	<#if form.mode == "view">
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
		<input type="hidden" id="${controlId}-removed" name="${field.name}_removed"/>
		<input type="hidden" id="${controlId}-added" name="${field.name}_added"/>
	</#if>
	<ul id="${controlId}-checkboxes"></ul>
	<input type="hidden" id="${fieldHtmlId}" name="-" value="${field.value?html}"/>
</div>