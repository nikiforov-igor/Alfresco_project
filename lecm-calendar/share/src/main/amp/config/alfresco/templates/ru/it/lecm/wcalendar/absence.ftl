<#include "/org/alfresco/include/alfresco-template.ftl"/>

<@templateHeader>
<#-- Скрипты, необходимые для диалоговых форм -->
<#include "/org/alfresco/components/form/form.dependencies.inc">

<#-- Custom stylesheets -->
<@link rel="stylesheet" type="text/css" href="${url.context}/res/css/lecm-base/components/base-menu/base-menu.css"/>
<@link rel="stylesheet" type="text/css" href="${url.context}/res/css/lecm-calendar/wcalendar-menu.css"/>
<@link rel="stylesheet" type="text/css" href="${url.context}/res/css/lecm-calendar/absence-summary-table.css"/>

<#-- Скрипты меню, тулбара и датагрида -->
<@script type="text/javascript" src="${url.context}/res/jquery/jquery-1.6.2.js"/>
<@script type="text/javascript" src="${url.context}/res/modules/simple-dialog.js"/>
<#--загрузка base-utils.js вынесена в base-share-config-custom.xml-->
<#--<@script type="text/javascript" src="${url.context}/res/scripts/lecm-base/components/base-utils.js"></@script>-->
<@script type="text/javascript" src="${url.context}/res/scripts/lecm-base/components/advsearch.js"></@script>
<@script type="text/javascript" src="${url.context}/res/scripts/lecm-base/components/lecm-datagrid.js"></@script>
<@script type="text/javascript" src="${url.context}/res/scripts/lecm-calendar/absence/absence-datagrid.js"/>
<@script type="text/javascript" src="${url.context}/res/scripts/lecm-calendar/absence/absence-toolbar.js"/>
<@script type="text/javascript" src="${url.context}/res/scripts/lecm-calendar/menu.js"/>
<#-- Валидаторы -->
<@script type="text/javascript" src="${url.context}/res/scripts/lecm-calendar/absence/date-interval-validation.js"/>

<script type="text/javascript">//<![CDATA[
if (typeof LogicECM == "undefined" || !LogicECM) {
	var LogicECM = {};
}

LogicECM.module = LogicECM.module || {};
LogicECM.module.WCalendar = LogicECM.module.WCalendar || {};
LogicECM.module.WCalendar.Absence = LogicECM.module.WCalendar.Absence || {};
LogicECM.module.WCalendar.Const = LogicECM.module.WCalendar.Const || {};
(function(){
	var absenceContainer = ${absenceContainer};
	var absenceRoles = ${absenceRoles};
	LogicECM.module.WCalendar.Const.ROLES = LogicECM.module.WCalendar.Const.ROLES || absenceRoles;
	LogicECM.module.WCalendar.Absence.ABSENCE_CONTAINER = LogicECM.module.WCalendar.Absence.ABSENCE_CONTAINER || absenceContainer;
	LogicECM.module.WCalendar.Absence.ABSENCE_LABEL = LogicECM.module.WCalendar.Absence.ABSENCE_LABEL || "absenceDatagrid";
})();
//]]></script>
</@>

<#assign showContent = isEngineer || isBoss>

<#import "/ru/it/lecm/base/base-page.ftl" as bpage/>
<@bpage.basePage showToolbar=showContent>
	<#if showContent>
		<@region id="absence-summary-table-wrapper" scope="template"/>
		<@region id="absence" scope="template"/>
	<#else/>
		<@region id="forbidden" scope="template"/>
	</#if>
</@bpage.basePage>

