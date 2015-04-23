<#assign params = field.control.params/>

<#assign controlId = fieldHtmlId + "-cntrl">

<#assign isFieldMandatory = false>
<#if field.control.params.mandatory??>
    <#if field.control.params.mandatory == "true">
        <#assign isFieldMandatory = true>
    </#if>
<#elseif field.mandatory??>
    <#assign isFieldMandatory = field.mandatory>
<#elseif field.endpointMandatory??>
    <#assign isFieldMandatory = field.endpointMandatory>
</#if>

<#assign editable = ((params.editable!"true") == "true") && !(field.disabled) && (form.mode?string=="edit") >

<script type="text/javascript">//<![CDATA[
(function() {
	function drawForm(){
		var control = new LogicECM.module.Calendar.MembersView("${fieldHtmlId}").setMessages(${messages});
		control.setOptions(
				{
					currentValue: "${field.value!""}",
					mode: "${form.mode?string}",
					disabled: ${field.disabled?string},
					eventNodeRef: "${form.arguments.itemId}"
				});
	}
	function init() {
		LogicECM.module.Base.Util.loadResources([
			'scripts/lecm-events/controls/lecm-events-members-view-control.js'
        ], [
            'css/lecm-base/components/lecm-events-members-view-control.css'
        ], drawForm);
	}
	YAHOO.util.Event.onDOMReady(init);
})();
//]]></script>

<div class="control events-members-view-control with-grid">
	<div class="label-div">
		<label for="${controlId}">
        ${field.label?html}:
        <#if field.endpointMandatory!false || field.mandatory!false>
			<span class="mandatory-indicator">${msg("form.required.fields.marker")}</span>
        </#if>
		</label>
	</div>
	<div class="container">
		<div class="value-div">
			<div id="${controlId}">
				<input type="hidden" id="${fieldHtmlId}" name="${field.name}" value="${field.value?html}"/>
				<div id="${controlId}-currentValueDisplay" class="control-selected-values mandatory-highlightable"></div>
			</div>
		</div>
	</div>
</div>
<div class="clear"></div>