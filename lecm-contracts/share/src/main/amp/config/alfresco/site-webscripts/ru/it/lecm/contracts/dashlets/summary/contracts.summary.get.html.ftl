<#assign id = args.htmlid>
<#assign formId = "form-members-list">

<div class="dashlet contracts-summary bordered">
	<div class="title dashlet-title">
		<span>${msg("label.title")}</span>
	</div>
	<div class="body scrollableList dashlet-body" id="${id}_results"></div>
</div>
<div id="${formId}" class="yui-panel hidden1">
	<div id="${formId}-head" class="hd">${msg("label.info.panel.title")}</div>
	<div id="${formId}-body" class="bd">
		<div id="${formId}-content"></div>
		<div class="bdft">
			<span id="${formId}-cancel" class="yui-button yui-push-button">
				<span class="first-child">
					<button type="button" tabindex="0" onclick="LogicECM.module.Contracts.dashlet.Summary.instance.hideViewDialog();">${msg("button.close")}</button>
				</span>
			</span>
		</div>
	</div>
</div>

<script type="text/javascript">
(function() {
	LogicECM.module.Base.Util.loadResources(
		['scripts/lecm-contracts/contracts-summary.js'],
		['css/lecm-contracts/contracts-summary.css'],
		function() {
			var info = new LogicECM.module.Contracts.dashlet.Summary("${id}").setOptions({
				formId: "${formId}"
			}).setMessages(${messages});
			LogicECM.module.Contracts.dashlet.Summary.instance=info;
		});
})();
</script>
