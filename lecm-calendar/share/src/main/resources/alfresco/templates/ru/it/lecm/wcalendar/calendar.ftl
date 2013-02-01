<#include "/org/alfresco/include/alfresco-template.ftl" />
<#--<#include "/org/alfresco/include/documentlibrary.inc.ftl" />-->

<@templateHeader "transitional">
<#include "/org/alfresco/components/form/form.get.head.ftl">

<#-- Custom calendar stylesheet -->
<@link rel="stylesheet" type="text/css" href="${page.url.context}/css/lecm-calendar/wcalendar-calendar.css" />
<@link rel="stylesheet" type="text/css" href="${page.url.context}/css/lecm-base/components/base-menu/base-menu.css"/>
<@link rel="stylesheet" type="text/css" href="${page.url.context}/res/css/lecm-calendar/wcalendar-menu.css"/>
<@link rel="stylesheet" type="text/css" href="${page.url.context}/res/components/data-lists/toolbar.css"/>
<@link rel="stylesheet" type="text/css" href="${page.url.context}/res/css/lecm-calendar/wcalendar-toolbar.css"/>

<#-- Служебные функции -->
<@script type="text/javascript" src="${page.url.context}/res/scripts/lecm-calendar/utils.js"/>
<#-- Скрипт для валидации формы. Определяет, нет ли уже в списке дня, который пытаются добавить -->
<@script type="text/javascript" src="${page.url.context}/res/scripts/lecm-calendar/calendar/date-existence-validation.js" />
<#-- Custom Data Grid javascript-->
<@script type="text/javascript" src="${page.url.context}/res/scripts/lecm-calendar/calendar/non-working-days-datagrid.js"/>
<@script type="text/javascript" src="${page.url.context}/res/scripts/lecm-calendar/calendar/working-days-datagrid.js"/>
<@script type="text/javascript" src="${page.url.context}/res/scripts/lecm-calendar/calendar/years-datagrid.js"/>
<#-- Custom Toolbar javascript-->
<@script type="text/javascript" src="${page.url.context}/res/scripts/lecm-calendar/calendar/special-days-toolbar.js"/>
<@script type="text/javascript" src="${page.url.context}/res/scripts/lecm-calendar/calendar/years-toolbar.js"/>
<#-- Side menu javascript-->
<@script type="text/javascript" src="${page.url.context}/res/scripts/lecm-calendar/menu.js"/>

<script type="text/javascript">//<![CDATA[
<#if isEngineer>
      function init() {
            var resizer = new LogicECM.module.Base.Resizer('wcalendarCalendarResizer');

            resizer.setOptions({
                initialWidth: 500,
                divLeft: "wcalendar-calendar-years",
                divRight: "wcalendar-calendar-days"
            });
        }
        YAHOO.util.Event.onDOMReady(init);
</#if>

var calendarContainer = ${calendarContainer};
var calendarRoles = ${calendarRoles}

if (typeof LogicECM == "undefined" || !LogicECM) {
	var LogicECM = {};
}

LogicECM.module = LogicECM.module || {};
LogicECM.module.WCalendar = LogicECM.module.WCalendar || {};
LogicECM.module.WCalendar.Calendar = LogicECM.module.WCalendar.Calendar || {};
LogicECM.module.WCalendar.Const = LogicECM.module.WCalendar.Const || {};

LogicECM.module.WCalendar.Const.ROLES = LogicECM.module.WCalendar.Const.ROLES || calendarRoles;

LogicECM.module.WCalendar.Calendar.CALENDAR_CONTAINER = LogicECM.module.WCalendar.Calendar.CALENDAR_CONTAINER || calendarContainer;
LogicECM.module.WCalendar.Calendar.YEARS_LABEL = LogicECM.module.WCalendar.Calendar.YEARS_LABEL || "wcalendarYears";
LogicECM.module.WCalendar.Calendar.WORKING_DAYS_LABEL = LogicECM.module.WCalendar.Calendar.WORKING_DAYS_LABEL || "wcalendarWorkingDays";
LogicECM.module.WCalendar.Calendar.NON_WORKING_DAYS_LABEL = LogicECM.module.WCalendar.Calendar.NON_WORKING_DAYS_LABEL || "wcalendarNonWorkingDays";

//]]></script>
</@>

<#import "/ru/it/lecm/base/base-page.ftl" as bpage/>
<@bpage.basePage showToolbar=false>
	<#if isEngineer>
		<div class="yui-t1" id="wcalendar-calendar">
		<div id="yui-main">
			<div class="yui-b" id="wcalendar-calendar-days">
				<@region id="wcalendar-special-days-toolbar" scope="template" />
				<@region id="wcalendar-working-days-grid" scope="template" />
				<@region id="wcalendar-non-working-days-grid" scope="template" />
			</div>
		</div>
		<div id="wcalendar-calendar-years">
			<@region id="wcalendar-years-toolbar" scope="template" />
			<@region id="wcalendar-years-grid" scope="template" />
		</div>
	</div>
	<#else/>
		<@region id="forbidden" scope="template"/>
	</#if>
</@bpage.basePage>
