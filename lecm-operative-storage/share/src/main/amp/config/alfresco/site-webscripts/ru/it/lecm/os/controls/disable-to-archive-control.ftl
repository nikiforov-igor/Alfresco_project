<#include "/ru/it/lecm/base-share/components/controls/hidden.ftl">

<script type="text/javascript">
	(function() {
		function process() {
			debugger;
			var formId = '${args.htmlid}';
			var status = document.getElementById('${fieldHtmlId}-added').value;
			if("CLOSED" != status) {
				LogicECM.module.Base.Util.disableControl("${args.htmlid}", "lecm-os:nomenclature-case-to-archive");
			}
		}

	YAHOO.util.Event.onContentReady('${fieldHtmlId}-added', process);
	})();
</script>
