<#include "/org/alfresco/components/form/form.dependencies.inc">

<@markup id="css">
	<@link rel="stylesheet" type="text/css" href="${url.context}/res/css/lecm-statemachine-editor/main.css" />
</@>

<script type="text/javascript">
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
</script>
<div id="deploy-control"></div>
<span id="deploy-control-button" class="yui-button yui-push-button">
	<span class="first-child">
		<button type="button">Развернуть</button>
	</span>
</span>
