
<#include "/org/alfresco/include/alfresco-template.ftl"/>

<@templateHeader>

<#-- подключить все скрипты необходимые для диалоговых форм -->
<#include "/org/alfresco/components/form/form.get.head.ftl">

<#-- Custom toolbar stylesheet -->
<@link rel="stylesheet" type="text/css" href="${page.url.context}/res/components/data-lists/toolbar.css"/>
<@link rel="stylesheet" type="text/css" href="${page.url.context}/res/css/lecm-calendar/wcalendar-toolbar.css"/>
<@link rel="stylesheet" type="text/css" href="${page.url.context}/css/lecm-base/components/base-menu/base-menu.css"/>
<@link rel="stylesheet" type="text/css" href="${page.url.context}/res/css/lecm-calendar/wcalendar-menu.css"/>
<@link rel="stylesheet" type="text/css" href="${page.url.context}/res/css/lecm-calendar/reiteration-control.css"/>

<@script type="text/javascript" src="${page.url.context}/res/scripts/lecm-calendar/shedule/shedule-datagrid.js"/>
<#-- Custom Toolbar javascript-->
<@script type="text/javascript" src="${page.url.context}/res/scripts/lecm-calendar/shedule/shedule-toolbar.js"/>
<#-- Служебные функции -->
<@script type="text/javascript" src="${page.url.context}/res/scripts/lecm-calendar/utils.js"/>
<#-- Валидаторы -->
<@script type="text/javascript" src="${page.url.context}/res/scripts/lecm-calendar/shedule/shedule-limit-validation.js" />
<@script type="text/javascript" src="${page.url.context}/res/scripts/lecm-calendar/shedule/reiteration-rules-validation.js" />
<@script type="text/javascript" src="${page.url.context}/res/scripts/lecm-calendar/shedule/time-validation.js" />
<#-- Side menu javascript-->
<@script type="text/javascript" src="${page.url.context}/res/scripts/lecm-calendar/menu.js"/>


<script type="text/javascript">//<![CDATA[

var sheduleContainer = ${sheduleContainer};
var sheduleRoles = ${calendarRoles};

if (typeof LogicECM == "undefined" || !LogicECM) {
	var LogicECM = {};
}

LogicECM.module = LogicECM.module || {};
LogicECM.module.WCalendar = LogicECM.module.WCalendar || {};
LogicECM.module.WCalendar.Shedule = LogicECM.module.WCalendar.Shedule || {};
LogicECM.module.WCalendar.Const = LogicECM.module.WCalendar.Const || {};

LogicECM.module.WCalendar.Const.ROLES = LogicECM.module.WCalendar.Const.ROLES || sheduleRoles;

LogicECM.module.WCalendar.Shedule.SHEDULE_CONTAINER = LogicECM.module.WCalendar.Shedule.SHEDULE_CONTAINER || sheduleContainer;
LogicECM.module.WCalendar.Shedule.SHEDULE_LABEL = LogicECM.module.WCalendar.Shedule.SHEDULE_LABEL || "sheduleDatagrid";

//]]></script>

</@>

<#assign showContent = isEngineer || isBoss>

<#import "/ru/it/lecm/base/base-page.ftl" as bpage/>
<@bpage.basePage showToolbar=showContent>
	<#if showContent>
		<@region id="shedule" scope="template"/>
	<#else/>
		<@region id="forbidden" scope="template"/>
	</#if>
</@bpage.basePage>
