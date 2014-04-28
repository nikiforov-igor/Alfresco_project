<#include "/org/alfresco/components/component.head.inc">
<@script type="text/javascript" src="${page.url.context}/scripts/lecm-user-profile/distribution-tasks-toolbar.js"/>


<#import "/ru/it/lecm/base-share/components/base-components.ftl" as comp/>
<#assign toolbarId = args.htmlid/>

<script type="text/javascript"> //<![CDATA[
	(function () {
		new LogicECM.TaskDistribution.Toolbar("${toolbarId}").setMessages(${messages});
	})();
//]]>
</script>

<@comp.baseToolbar toolbarId true false false>
	<div id="${toolbarId}-btnReassignAllTasks"></div>
</@comp.baseToolbar>
