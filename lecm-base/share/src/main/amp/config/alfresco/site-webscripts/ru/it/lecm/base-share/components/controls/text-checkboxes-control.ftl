<#include "/org/alfresco/components/form/controls/common/utils.inc.ftl" />

<#assign fieldValue=field.value!"">
<#assign controlId = fieldHtmlId + "-cntrl">

<#assign disabled = form.mode == "view" || (field.disabled && !(field.control.params.forceEditable?? && field.control.params.forceEditable == "true"))>

<script type="text/javascript">//<![CDATA[
(function()
{
	function init() {
        LogicECM.module.Base.Util.loadScripts([
            'scripts/lecm-base/components/lecm-text-checkboxes-control.js'
		], createControls);
	}

	function createControls() {
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
	}

	YAHOO.util.Event.onDOMReady(init);
})();
//]]></script>

<div class="control text-checkboxes <#if disabled>viewmode<#else>editmode</#if>">
	<#if disabled>
		<div class="label-div">
			<#if field.mandatory && !(fieldValue?is_number) && fieldValue?string == "">
				<span class="incomplete-warning">
					<img src="${url.context}/res/components/form/images/warning-16.png" title="${msg("form.field.incomplete")}" />
				<span>
			</#if>
			<label>${field.label?html}:</label>
		</div>
	<#else>
		<div class="label-div">
			<label for="${controlId}-checkboxes">
			${field.label?html}:
				<#if field.mandatory>
					<span class="mandatory-indicator">
					${msg("form.required.fields.marker")}
					</span>
				</#if>
			</label>
		</div>
	</#if>
	<div class="container">
		<ul id="${controlId}-checkboxes"></ul>
	</div>
	<input type="hidden" id="${fieldHtmlId}" name="${field.name}" value="${field.value?html}"/>
</div>
<div class="clear"></div>

