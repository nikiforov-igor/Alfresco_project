<div class="set-bordered-panel actions-panel">
	<div class="set-bordered-panel-heading">Доступные действия</div>
	<div class="set-bordered-panel-body">
		<div class="control status editmode">
			<div class="container">
				<div class="value-div">
					<div id="${fieldHtmlId}-actions-container" class="case-status-buttons"></div>
				</div>
			</div>
		</div>
	</div>
</div>

<script>
	(function() {

		function init() {
			LogicECM.module.Base.Util.loadResources([
					'scripts/lecm-os/controls/case-status-control.js'
				], [
					'css/lecm-os/controls/case-status-control.css'
				],
				createControl
			);
		}

		function createControl() {
			var control = new LogicECM.module.OS.StatusControl("${fieldHtmlId}");
			control.setOptions({
				fieldId: "${field.configName}",
				formId: "${args.htmlid}",
				value: "${field.value}",
				itemId: "${form.arguments.itemId}",
				excludeActions: [
					"onActionEdit"
				]
			});

			control.prepare();
			control.updateArchiveCheckBox();
		}

		YAHOO.util.Event.onContentReady("${fieldHtmlId}-actions-container", init);

	})();
</script>