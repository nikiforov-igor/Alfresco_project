<#import "/ru/it/lecm/base-share/components/base-components.ftl" as comp/>

<#assign toolbarId = args.htmlid/>

<#assign pageId = page.id/>

<script type="text/javascript">
	(function () {
		LogicECM.module.Routes.isEngineer = ${isEngineer?string};

		var routesToolbar = new LogicECM.module.Routes.Toolbar("${toolbarId}");
		routesToolbar.setMessages(${messages});
		routesToolbar.setOptions ({
			pageId: "${pageId}",
			inEngineer: LogicECM.module.Routes.isEngineer
		});
	})();
</script>

<@comp.baseToolbar toolbarId true false false>
	<#if isEngineer>
		<div id="${toolbarId}-btnCreateNewRoute"></div>
	</#if>
</@comp.baseToolbar>
