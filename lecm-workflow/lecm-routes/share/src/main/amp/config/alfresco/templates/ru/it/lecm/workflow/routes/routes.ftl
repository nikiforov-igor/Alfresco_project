<script type="text/javascript">
	if (typeof LogicECM == "undefined" || !LogicECM) {
		var LogicECM = {};
	}

	LogicECM.module = LogicECM.module || {};
	LogicECM.module.Routes = LogicECM.module.Routes || {};
	LogicECM.module.Routes.Const = LogicECM.module.Routes.Const || {};

	LogicECM.module.Routes.Const.ROUTES_CONTAINER = LogicECM.module.Routes.Const.ROUTES_CONTAINER || ${routesContainer};
	LogicECM.module.Routes.Const.ROUTES_DATAGRID_LABEL = LogicECM.module.Routes.Const.ROUTES_DATAGRID_LABEL || "routesDatagrid";

    //]]></script>
</script>

<#import "/ru/it/lecm/base/base-page.ftl" as bpage/>
<@bpage.basePageSimple>
	<@region id="routes-grid" scope="template"/>
</@bpage.basePageSimple>
