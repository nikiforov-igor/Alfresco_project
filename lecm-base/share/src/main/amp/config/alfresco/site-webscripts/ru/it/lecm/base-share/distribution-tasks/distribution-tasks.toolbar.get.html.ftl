<#assign el=args.htmlid?html>

<script type="text/javascript">//<![CDATA[
(function () {
	function init() {
		LogicECM.module.Base.Util.loadScripts([
					'/scripts/lecm-base/distribution-tasks/distribution-tasks-toolbar.js'
				], createObject);
	}

	function createObject() {
		new LogicECM.TaskDistributionToolbar("${el}").setMessages(${messages});
	}

	YAHOO.util.Event.onDOMReady(init);
})();
//]]></script>

<#import "/ru/it/lecm/base-share/components/base-components.ftl" as comp/>
<@comp.baseToolbar el true false false>
	<div id="${el}-btnReassignAllTasks"></div>
</@comp.baseToolbar>
