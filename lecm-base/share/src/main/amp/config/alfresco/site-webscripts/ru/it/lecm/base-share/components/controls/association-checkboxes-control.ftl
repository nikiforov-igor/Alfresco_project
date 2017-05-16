<#--This association-checkboxes-control.ftl is deprecated!-->

<#include "/org/alfresco/components/form/controls/common/utils.inc.ftl" />

<#assign fieldValue=field.value!"">
<#assign controlId = fieldHtmlId + "-cntrl">

<#assign disabled = form.mode == "view" || (field.disabled && !(field.control.params.forceEditable?? && field.control.params.forceEditable == "true"))>

<script type="text/javascript">//<![CDATA[
(function()
{
	function init() {
        LogicECM.module.Base.Util.loadScripts([
            'scripts/lecm-base/components/lecm-association-checkboxes.js'],
                createAssociationCheckboxes);
	}
	function createAssociationCheckboxes(){
		var control = new LogicECM.module.AssociationCheckboxes("${fieldHtmlId}").setMessages(${messages});
		control.setOptions({
			<#if disabled>
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

			mode: "${form.mode}",
			itemType: "${field.endpointType}",
			maxSearchResults: ${field.control.params.maxSearchResults!'1000'},
			currentValue: "${field.value!''}",
			<#if field.control.params.defaultValuesDataSource??>
				defaultValuesDataSource: "${field.control.params.defaultValuesDataSource}",
			</#if>
			nameSubstituteString: "${field.control.params.nameSubstituteString!'{cm:name}'}",
			sortProp: "${field.control.params.sortProp!'cm:name'}"
		});
	}
	YAHOO.util.Event.onDOMReady(init);
})();
//]]></script>

<div class="control association-checkboxes <#if disabled>viewmode<#else>editmode</#if>">
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
	        <input type="hidden" id="${controlId}-removed" name="${field.name}_removed"/>
	        <input type="hidden" id="${controlId}-added" name="${field.name}_added"/>
	    </div>
	</#if>
	<div class="container">
	    <ul id="${controlId}-checkboxes"></ul>
	</div>
	<input type="hidden" id="${fieldHtmlId}" name="${field.name}" value="${field.value?html}"/>
</div>
<div class="clear"></div>