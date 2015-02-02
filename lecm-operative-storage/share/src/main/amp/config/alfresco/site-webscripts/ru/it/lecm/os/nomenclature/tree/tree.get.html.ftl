<#assign id = args.htmlid>

<script type="text/javascript">
(function() {
	YAHOO.util.Event.onDOMReady(function() {
        LogicECM.CurrentModules = LogicECM.CurrentModules || {};
		LogicECM.CurrentModules["${id}"] = new LogicECM.module.Nomenclature.Tree("${id}").setMessages(${messages});
	});
})();
</script>

<div id="${id}-body" class="datalists tree">
	<div id="${id}-headerBar" class="header-bar toolbar flat-button theme-bg-2">
		<div class="left"></div>
	</div>
	<div id="dictionary" class="ygtv-highlight"></div>
</div>
