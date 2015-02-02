<#assign id = args.htmlid>

<script type="text/javascript">
(function() {
	YAHOO.util.Event.onDOMReady(function() {
        LogicECM.CurrentModules = LogicECM.CurrentModules || {};
		LogicECM.CurrentModules["${id}"] = new LogicECM.module.Nomenclature.Node("${id}").setMessages(${messages});
	});
})();
</script>

<div id="${id}-body" class="arm-metadata"></div>
