<#assign containerId = fieldHtmlId + "-container">
<#assign hiddenFieldId = fieldHtmlId>
<#assign hiddenFieldName = field.name>

<script type='text/javascript'>//<![CDATA[
(function() {
function init() {
    LogicECM.module.Base.Util.loadScripts([
            'scripts/lecm-workflow/reservation/activiti-transitions-reservation-radiobuttons.js'
		], createControls);
	}
	function createControls(){
		new LogicECM.module.ActivitiTransitionReservationRadiobuttons('${containerId}').setOptions({
			currentValue: '${field.control.params.options?js_string}',
			hiddenFieldName: '${hiddenFieldName}',
			hiddenFieldId: '${hiddenFieldId}'
		}).setMessages(${messages});
	}

	console.log('in contol "activiti-transitions-reservation-radiobuttons"');
	YAHOO.util.Event.onDOMReady(init);
})();
//]]></script>

<div class='form-field'>
	<div id='${containerId}'>
		<input id='${hiddenFieldId}' type='hidden' name='${hiddenFieldName}'>
	</div>
</div>
