<script type="text/javascript">//<![CDATA[
	LogicECM.module = LogicECM.module || {};
	LogicECM.module.Routes = LogicECM.module.Routes || {};
	LogicECM.module.Routes.Const = LogicECM.module.Routes.Const || {};

	LogicECM.module.Routes.Const.ROUTES_CONTAINER = LogicECM.module.Routes.Const.ROUTES_CONTAINER || ${routesContainer};
	LogicECM.module.Routes.Const.ROUTES_DATAGRID_LABEL = LogicECM.module.Routes.Const.ROUTES_DATAGRID_LABEL || "routesDatagrid";
//]]></script>

<#import "/ru/it/lecm/base/base-page.ftl" as bpage/>
<@bpage.basePageSimple>
	<#if allowEdit>
		<@region id="routes-grid" scope="template"/>
	<#else>
		<@region id="forbidden" scope="template"/>
	</#if>
</@bpage.basePageSimple>
