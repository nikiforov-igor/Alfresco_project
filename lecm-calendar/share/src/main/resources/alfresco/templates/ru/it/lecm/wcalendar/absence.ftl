
<#include "/org/alfresco/include/alfresco-template.ftl"/>

<@templateHeader>

<#-- подключить все скрипты необходимые для диалоговых форм -->
<#include "/org/alfresco/components/form/form.get.head.ftl">

<#-- Далее перечисляются самописные скрипты LogicECM  -->

<@link rel="stylesheet" type="text/css" href="${page.url.context}/css/lecm-base/components/base-menu/base-menu.css"/>
<@link rel="stylesheet" type="text/css" href="${page.url.context}/res/css/lecm-calendar/wcalendar-menu.css"/>
<@link rel="stylesheet" type="text/css" href="${page.url.context}/res/css/lecm-calendar/absence-summary-table.css"/>

<@script type="text/javascript" src="${page.url.context}/res/scripts/lecm-calendar/menu.js"/>

<#-- Служебные функции -->
<@script type="text/javascript" src="${page.url.context}/res/scripts/lecm-calendar/utils.js"/>
<@script type="text/javascript" src="${page.url.context}/res/scripts/lecm-calendar/absence/absence-datagrid.js"/>
<@script type="text/javascript" src="${page.url.context}/res/scripts/lecm-calendar/absence/absence-toolbar.js"/>
<@script type="text/javascript" src="${page.url.context}/res/scripts/lecm-calendar/absence/date-interval-validation.js"/>
<@script type="text/javascript" src="${page.url.context}/res/jquery/jquery-1.6.2.js"/>

<script type="text/javascript">//<![CDATA[

var absenceContainer = ${absenceContainer};
var absenceRoles = ${absenceRoles};

if (typeof LogicECM == "undefined" || !LogicECM) {
	var LogicECM = {};
}

LogicECM.module = LogicECM.module || {};
LogicECM.module.WCalendar = LogicECM.module.WCalendar || {};
LogicECM.module.WCalendar.Absence = LogicECM.module.WCalendar.Absence || {};
LogicECM.module.WCalendar.Const = LogicECM.module.WCalendar.Const || {};

LogicECM.module.WCalendar.Const.ROLES = LogicECM.module.WCalendar.Const.ROLES || absenceRoles;

LogicECM.module.WCalendar.Absence.ABSENCE_CONTAINER = LogicECM.module.WCalendar.Absence.ABSENCE_CONTAINER || absenceContainer;
LogicECM.module.WCalendar.Absence.ABSENCE_LABEL = LogicECM.module.WCalendar.Absence.ABSENCE_LABEL || "absenceDatagrid";

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

