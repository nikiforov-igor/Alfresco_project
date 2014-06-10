<#include "/org/alfresco/include/alfresco-template.ftl" />

<@templateHeader "transitional">
<#-- Скрипты, необходимые для диалоговых форм -->
<#include "/org/alfresco/components/form/form.dependencies.inc">

<#-- Custom stylesheets -->
<@link rel="stylesheet" type="text/css" href="${url.context}/res/css/lecm-calendar/wcalendar-calendar.css" />
<@link rel="stylesheet" type="text/css" href="${url.context}/res/css/lecm-base/components/base-menu/base-menu.css"/>
<@link rel="stylesheet" type="text/css" href="${url.context}/res/css/lecm-calendar/wcalendar-menu.css"/>
<@link rel="stylesheet" type="text/css" href="${url.context}/res/components/data-lists/toolbar.css"/>
<@link rel="stylesheet" type="text/css" href="${url.context}/res/css/lecm-calendar/wcalendar-toolbar.css"/>

<#-- Скрипты меню, тулбара и датагрида -->
<@script type="text/javascript" src="${url.context}/res/jquery/jquery-1.6.2.js"/>
<@script type="text/javascript" src="${url.context}/res/modules/simple-dialog.js"/>
<#--загрузка base-utils.js вынесена в base-share-config-custom.xml-->
<#--<@script type="text/javascript" src="${url.context}/res/scripts/lecm-base/components/base-utils.js"></@script>-->
<@script type="text/javascript" src="${url.context}/res/scripts/lecm-base/components/advsearch.js"></@script>
<@script type="text/javascript" src="${url.context}/res/scripts/lecm-base/components/lecm-datagrid.js"></@script>
<@script type="text/javascript" src="${url.context}/res/scripts/lecm-calendar/calendar/non-working-days-datagrid.js"/>
<@script type="text/javascript" src="${url.context}/res/scripts/lecm-calendar/calendar/working-days-datagrid.js"/>
<@script type="text/javascript" src="${url.context}/res/scripts/lecm-calendar/calendar/years-datagrid.js"/>
<@script type="text/javascript" src="${url.context}/res/scripts/lecm-calendar/calendar/special-days-toolbar.js"/>
<@script type="text/javascript" src="${url.context}/res/scripts/lecm-calendar/calendar/years-toolbar.js"/>
<@script type="text/javascript" src="${url.context}/res/scripts/lecm-calendar/menu.js"/>
<#-- Валидаторы -->
<@script type="text/javascript" src="${url.context}/res/scripts/lecm-calendar/calendar/date-existence-validation.js" />

<script type="text/javascript">//<![CDATA[
if (typeof LogicECM == "undefined" || !LogicECM) {
	var LogicECM = {};
}

LogicECM.module = LogicECM.module || {};
LogicECM.module.WCalendar = LogicECM.module.WCalendar || {};
LogicECM.module.WCalendar.Calendar = LogicECM.module.WCalendar.Calendar || {};
LogicECM.module.WCalendar.Const = LogicECM.module.WCalendar.Const || {};

(function() {
	var calendarContainer = ${calendarContainer};
	var calendarRoles = ${calendarRoles}

	LogicECM.module.WCalendar.Const.ROLES = LogicECM.module.WCalendar.Const.ROLES || calendarRoles;

	LogicECM.module.WCalendar.Calendar.CALENDAR_CONTAINER = LogicECM.module.WCalendar.Calendar.CALENDAR_CONTAINER || calendarContainer;
	LogicECM.module.WCalendar.Calendar.YEARS_LABEL = LogicECM.module.WCalendar.Calendar.YEARS_LABEL || "wcalendarYears";
	LogicECM.module.WCalendar.Calendar.WORKING_DAYS_LABEL = LogicECM.module.WCalendar.Calendar.WORKING_DAYS_LABEL || "wcalendarWorkingDays";
	LogicECM.module.WCalendar.Calendar.NON_WORKING_DAYS_LABEL = LogicECM.module.WCalendar.Calendar.NON_WORKING_DAYS_LABEL || "wcalendarNonWorkingDays";
})();
//]]></script>
</@>

<#import "/ru/it/lecm/base/base-page.ftl" as bpage/>
<#import "/ru/it/lecm/base-share/components/2-panels-with-splitter.ftl" as panels/>
<@bpage.basePage showToolbar=false>
	<#if isEngineer>
            <div class="yui-t1" id="wcalendar-calendar">
                <@panels.twoPanels initialWidth=500 leftRegions=["wcalendar-years-toolbar","wcalendar-years-grid"] leftPanelId="wcalendar-calendar-years" rightPanelId="wcalendar-calendar-days">
                    <@region id="wcalendar-special-days-toolbar" scope="template" />
                    <@region id="wcalendar-working-days-grid" scope="template" />
                    <@region id="wcalendar-non-working-days-grid" scope="template" />
                </@panels.twoPanels>
            </div>
	<#else/>
		<@region id="forbidden" scope="template"/>
	</#if>
</@bpage.basePage>
