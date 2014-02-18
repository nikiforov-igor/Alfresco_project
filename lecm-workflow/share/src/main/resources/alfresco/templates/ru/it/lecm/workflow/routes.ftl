<#include "/org/alfresco/include/alfresco-template.ftl"/>

<@templateHeader>

<#include "/org/alfresco/components/form/form.get.head.ftl">
<@link rel="stylesheet" type="text/css" href="${page.url.context}/css/lecm-base/components/base-menu/base-menu.css"/>

<@script type="text/javascript" src="${page.url.context}/res/scripts/lecm-workflow/routes/routes-datagrid.js"/>
<@script type="text/javascript" src="${page.url.context}/res/scripts/lecm-workflow/routes/routes-toolbar.js"/>

<script type="text/javascript">
	if (typeof LogicECM == "undefined" || !LogicECM) {
		var LogicECM = {};
	}

	LogicECM.module = LogicECM.module || {};
	LogicECM.module.Routes = LogicECM.module.Routes || {};
	LogicECM.module.Routes.Const = LogicECM.module.Routes.Const || {};

	LogicECM.module.Routes.Const.ROUTES_CONTAINER = LogicECM.module.Routes.Const.ROUTES_CONTAINER || ${routesContainer};
	LogicECM.module.Routes.Const.ROUTES_DATAGRID_LABEL = LogicECM.module.Routes.Const.ROUTES_DATAGRID_LABEL || "routesDatagrid";
</script>
</@>

<#import "/ru/it/lecm/base/base-page.ftl" as bpage/>
<@bpage.basePage showToolbar=true>
	<@region id="routes-grid" scope="template"/>
</@bpage.basePage>
