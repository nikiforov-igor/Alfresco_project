<#assign containerId = fieldHtmlId + "-container"/>
<div id="${containerId}" class="set-bordered-panel actions-panel hidden">
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
			YAHOO.util.Dom.removeClass("${containerId}", "hidden");
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

		function isCurrentUserArchivist() {
            Alfresco.util.Ajax.jsonGet({
                url: Alfresco.constants.PROXY_URI + "lecm/orgstructure/isCurrentEmployeeHasBusinessRole",
                dataObj: {
                    roleId: "DA_ARCHIVISTS"
                },
                successCallback: {
                    scope: this,
                    fn: function (response) {
                        if (response.json) {
                            init();
                        }
                    }
                },
                failureCallback: {
                    scope: this,
                    fn: function (response) {
                        YAHOO.log("Failed to process XHR transaction.", "info", "example");
                    }
                }
            });
		}

		YAHOO.util.Event.onContentReady("${fieldHtmlId}-actions-container", isCurrentUserArchivist);

	})();
</script>