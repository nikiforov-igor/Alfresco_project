<div id="deploy-control"></div>
<span id="deploy-control-button" class="yui-button yui-push-button">
	<span class="first-child">
		<button type="button">${msg('btn.deploy')}</button>
	</span>
</span>
<@inlineScript group="lecm-statemachine-editor">
(function() {
	function init() {
		LogicECM.module.Base.Util.loadScripts([
			'scripts/lecm-statemachine-editor/lecm-statemachine-deploy.js'
		], createControl, []);
	}
	function createControl(){
		new LogicECM.module.StatemachineEditor.Deploy("deploy-control");
	}
	YAHOO.util.Event.onDOMReady(init);
})();
</@>
