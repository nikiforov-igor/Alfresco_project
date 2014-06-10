<#import "/ru/it/lecm/base-share/components/base-components.ftl" as comp/>

<#assign toolbarId = args.htmlid/>

<#assign pageId = page.id/>

<script type="text/javascript">
	(function () {
		var routesToolbar = new LogicECM.module.Routes.Toolbar("${toolbarId}");
		routesToolbar.setMessages(${messages});
		routesToolbar.setOptions ({
			pageId: "${pageId}",
			inEngineer: ${isEngineer?string}
		});
	})();
</script>

<@comp.baseToolbar toolbarId true false false>
	<div id="${toolbarId}-btnCreateNewPrivateRoute"></div>
	<#if isEngineer>
		<div id="${toolbarId}-btnCreateNewCommonRoute"></div>
	</#if>
</@comp.baseToolbar>
