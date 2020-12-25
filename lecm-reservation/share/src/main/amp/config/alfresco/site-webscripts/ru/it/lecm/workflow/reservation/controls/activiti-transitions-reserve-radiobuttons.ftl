<#assign containerId = fieldHtmlId + "-container">
<#assign hiddenFieldId = fieldHtmlId>
<#assign hiddenFieldName = field.name>

<#assign hiddenOnLoad = "">
<#if field.control.params.hiddenOnLoad?? && field.control.params.hiddenOnLoad == "true">
	<#assign  hiddenOnLoad="hidden">
</#if>

<#assign showLabelBlock = false>
<#if field.control.params.showLabelBlock?? && field.control.params.showLabelBlock == "true">
	<#assign  showLabelBlock = true>
</#if>

<script type='text/javascript'>//<![CDATA[
(function() {
function init() {
	LogicECM.module.Base.Util.loadResources([
			'scripts/lecm-workflow/reservation/activiti-transitions-reserve-radiobuttons.js'
		],
		[
			'css/lecm-base/components/activiti-transitions-radiobuttons.css'
		], createControls);
	}
	function createControls(){
		new LogicECM.module.ActivitiTransitionReserveRadiobuttons('${containerId}').setOptions({
			currentValue: '${field.control.params.options?js_string}',
			hiddenFieldName: '${hiddenFieldName}',
		<#if field.control.params.decisionProp??>
            decisionProp: '${field.control.params.decisionProp}',
		</#if>
		<#if field.control.params.decisionProp??>
            rejectReasonProp: '${field.control.params.rejectReasonProp}',
		</#if>
		<#if field.control.params.decisionProp??>
            positiveDecision: '${field.control.params.positiveDecision}',
		</#if>
			hiddenFieldId: '${hiddenFieldId}'
		}).setMessages(${messages});
	}

	YAHOO.util.Event.onDOMReady(init);
})();
//]]></script>

<div class='control form-field activiti-transitions-radiobuttons ${hiddenOnLoad}'>
<#if showLabelBlock>
    <div class="label-div"></div>
</#if>
    <div class="container">
        <div id='${containerId}'>
            <input id='${hiddenFieldId}' type='hidden' name='${hiddenFieldName}'>
        </div>
    </div>
</div>
<div class="clear"></div>
