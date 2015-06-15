<#include "/ru/it/lecm/base-share/components/controls/association-control.ftl">
<#assign dialogId = args.htmlid>
<script type="text/javascript">

	(function() {

		function init() {
			LogicECM.module.Base.Util.loadResources([
					'scripts/lecm-os/controls/org-unit-validator.js'
				], [
				],
				createControl
			);
		}

		function createControl() {
			var control = new LogicECM.module.OS.OrgUnitValidator("${fieldHtmlId}");
			control.setOptions({
				<#if form.arguments.itemId??>
				nodeRef: '${form.arguments.itemId}',
				</#if>
				<#if field.value??>
				currentValue: '${field.value}',
				</#if>
				errorContainer: '${dialogId}_assoc_lecm-os_nomenclature-unit-section-unit-assoc-cntrl-autocomplete-input'
			});

			control.registerValidator();
		}

		YAHOO.util.Event.onContentReady("${fieldHtmlId}", init);

	})();

</script>