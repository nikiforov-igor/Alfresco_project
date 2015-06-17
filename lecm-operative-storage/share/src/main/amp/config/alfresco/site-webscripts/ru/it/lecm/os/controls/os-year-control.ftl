<#include "/ru/it/lecm/base-share/components/controls/number.ftl">

<script>

	(function() {

		function init() {
			LogicECM.module.Base.Util.loadResources([
					'scripts/lecm-os/controls/year-section-control.js'
				], [
				],
				createControl
			);
		}

		function createControl() {
			var control = new LogicECM.module.OS.YearSectionControl("${fieldHtmlId}");
			control.setOptions({
				<#if field.value?? && field.value?is_number>
				currentValue: '${field.value?c}'
				</#if>
			});

			control.registerValidator();
		}

		YAHOO.util.Event.onContentReady("${fieldHtmlId}", init);

	})();


</script>