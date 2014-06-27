<#include "picker.inc.ftl" />

<#assign controlId = fieldHtmlId + "-cntrl">

<script type="text/javascript">//<![CDATA[
(function()
{
    function init() {
        LogicECM.module.Base.Util.loadScripts([
            'scripts/lecm-base/components/object-finder/lecm-object-finder.js'
        ], createPicker);
    }
    function createPicker(){
       <@renderPickerJS field "picker" />
       picker.setOptions(
       {
          itemType: "${field.endpointType}",
          multipleSelectMode: ${field.endpointMany?string},
          itemFamily: "authority",
          nameSubstituteString: "{cm:firstName} {cm:lastName} ({cm:userName})"
       });
    }
    YAHOO.util.Event.onDOMReady(init);

})();
//]]></script>

<#if form.mode == "view">
	<div class="control authority viewmode">
		<div class="label-div">
			<#if field.endpointMandatory && field.value == "">
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
	</div>
<#else>
	<div class="control authority editmode">
		<div class="label-div">
			<label for="${controlId}-autocomplete-input">
			${field.label?html}:
				<#if field.endpointMandatory><span class="mandatory-indicator">${msg("form.required.fields.marker")}</span></#if>
			</label>
			<input type="hidden" id="${controlId}-removed" name="${field.name}_removed"/>
			<input type="hidden" id="${controlId}-added" name="${field.name}_added"/>
			<input type="hidden" id="${controlId}-selectedItems"/>
		</div>
		<div id="${controlId}" class="container">
			<#if field.disabled == false>
				<input type="hidden" id="${fieldHtmlId}" name="-" value="${field.value?html}" />
				<input type="hidden" id="${controlId}-added" name="${field.name}_added" />
				<input type="hidden" id="${controlId}-removed" name="${field.name}_removed" />
				<@renderPickerHTML controlId />
				<div class="buttons-div">
					<div id="${controlId}-itemGroupActions" class="show-picker"></div>
				</div>
			</#if>
			<div class="value-div">
				<input type="hidden" id="${fieldHtmlId}" name="${field.name}" value="${field.value?html}" />
				<div id="${controlId}-currentValueDisplay" class="control-selected-values mandatory-highlightable"></div>
			</div>
		</div>
	</div>
</#if>
<div class="clear"></div>
