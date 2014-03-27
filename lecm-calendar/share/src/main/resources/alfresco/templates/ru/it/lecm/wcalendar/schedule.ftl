<#include "/org/alfresco/include/alfresco-template.ftl"/>

<@templateHeader>
<#-- Скрипты, необходимые для диалоговых форм -->
<#include "/org/alfresco/components/form/form.get.head.ftl">

<#-- Custom stylesheets -->
<@link rel="stylesheet" type="text/css" href="${page.url.context}/res/components/data-lists/toolbar.css"/>
<@link rel="stylesheet" type="text/css" href="${page.url.context}/res/css/lecm-calendar/wcalendar-toolbar.css"/>
<@link rel="stylesheet" type="text/css" href="${page.url.context}/css/lecm-base/components/base-menu/base-menu.css"/>
<@link rel="stylesheet" type="text/css" href="${page.url.context}/res/css/lecm-calendar/wcalendar-menu.css"/>
<@link rel="stylesheet" type="text/css" href="${page.url.context}/res/css/lecm-calendar/reiteration-control.css"/>
<@link rel="stylesheet" type="text/css" href="${page.url.context}/res/css/lecm-calendar/jquery-ui-1.10.3.custom.css"/>
<@link rel="stylesheet" type="text/css" href="${page.url.context}/res/css/lecm-calendar/jquery-ui-timepicker-addon.css"/>

<#-- Скрипты меню, тулбара и датагрида -->
<@script type="text/javascript" src="${page.url.context}/res/scripts/lecm-calendar/schedule/schedule-datagrid.js"/>
<@script type="text/javascript" src="${page.url.context}/res/scripts/lecm-calendar/schedule/schedule-toolbar.js"/>
<@script type="text/javascript" src="${page.url.context}/res/scripts/lecm-calendar/menu.js"/>
<#-- Валидаторы -->
<@script type="text/javascript" src="${page.url.context}/res/scripts/lecm-calendar/schedule/schedule-limit-validation.js" />
<@script type="text/javascript" src="${page.url.context}/res/scripts/lecm-calendar/schedule/reiteration-rules-validation.js" />
<@script type="text/javascript" src="${page.url.context}/res/scripts/lecm-calendar/schedule/time-validation.js" />

<script type="text/javascript">//<![CDATA[

var scheduleContainer = ${scheduleContainer};
var scheduleRoles = ${calendarRoles};

if (typeof LogicECM == "undefined" || !LogicECM) {
	var LogicECM = {};
}

LogicECM.module = LogicECM.module || {};
LogicECM.module.WCalendar = LogicECM.module.WCalendar || {};
LogicECM.module.WCalendar.Schedule = LogicECM.module.WCalendar.Schedule || {};
LogicECM.module.WCalendar.Const = LogicECM.module.WCalendar.Const || {};

LogicECM.module.WCalendar.Const.ROLES = LogicECM.module.WCalendar.Const.ROLES || scheduleRoles;

LogicECM.module.WCalendar.Schedule.SCHEDULE_CONTAINER = LogicECM.module.WCalendar.Schedule.SCHEDULE_CONTAINER || scheduleContainer;
LogicECM.module.WCalendar.Schedule.SCHEDULE_LABEL = LogicECM.module.WCalendar.Schedule.SCHEDULE_LABEL || "scheduleDatagrid";

LogicECM.module.WCalendar.Schedule.ORGANIZATION_NODE_REF = "${orgNodeRef}"
//]]></script>
</@>

<#assign showContent = isEngineer || isBoss>

<#import "/ru/it/lecm/base/base-page.ftl" as bpage/>
<@bpage.basePage showToolbar=showContent>
	<#if showContent>
		<@region id="schedule" scope="template"/>
	<#else/>
		<@region id="forbidden" scope="template"/>
	</#if>
</@bpage.basePage>
