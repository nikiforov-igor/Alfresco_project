<#include "/ru/it/lecm/base-share/components/controls/association-control.ftl">
<script type="text/javascript">
(function(){
	
	function process() {
		Alfresco.util.Ajax.jsonGet(
			{
				url: Alfresco.constants.PROXY_URI + "/lecm/operative-storage/checkCentralized",
				successCallback:
				{
					fn: function (response) {
						var oResults = response.json;
						if (oResults != null && !oResults.isCentralized) {
							LogicECM.module.Base.Util.disableControl("${args.htmlid}", "lecm-os:nomenclature-organization-assoc");
						}
					}
				}
			});
	}

	YAHOO.util.Event.onDOMReady(process);
})();
</script>