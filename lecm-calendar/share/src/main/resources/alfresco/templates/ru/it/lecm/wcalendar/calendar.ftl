<#include "/org/alfresco/include/alfresco-template.ftl" />
<#--<#include "/org/alfresco/include/documentlibrary.inc.ftl" />-->

<@templateHeader "transitional">
<#include "/org/alfresco/components/form/form.get.head.ftl">
<@script type="text/javascript" src="${page.url.context}/res/modules/simple-dialog.js"></@script>

<#-- Data Grid stylesheet -->
<@link rel="stylesheet" type="text/css" href="${page.url.context}/res/components/data-lists/datagrid.css"/>
<#-- Custom calendar stylesheet -->
<@link rel="stylesheet" type="text/css" href="${page.url.context}/css/lecm-calendar/wcalendar-calendar.css" />

<#-- Data Grid javascript-->
<@script type="text/javascript" src="${page.url.context}/res/scripts/lecm-base/components/lecm-datagrid.js"/>
<#-- Advanced search -->
<@script type="text/javascript" src="${page.url.context}/res/scripts/lecm-base/components/advsearch.js"/>
<#-- Служебные функции -->
<@script type="text/javascript" src="${page.url.context}/res/scripts/lecm-calendar/utils.js"/>
<#-- Скрипт для валидации формы. Определяет, нет ли уже в списке дня, который пытаются добавить -->
<@script type="text/javascript" src="${page.url.context}/res/scripts/lecm-calendar/calendar/date-existence-validation.js" />

<script type="text/javascript">//<![CDATA[
      function init() {
            var resizer = new LogicECM.module.Base.Resizer('wcalendarCalendarResizer');

            resizer.setOptions({
                initialWidth: 500,
                divLeft: "wcalendar-calendar-years",
                divRight: "wcalendar-calendar-days"
            });
        }
        YAHOO.util.Event.onDOMReady(init);

var calendarContainer = ${calendarContainer};

if (typeof LogicECM == "undefined" || !LogicECM) {
	var LogicECM = {};
}

LogicECM.module = LogicECM.module || {};
LogicECM.module.WCalendar = LogicECM.module.WCalendar || {};
LogicECM.module.WCalendar.Calendar = LogicECM.module.WCalendar.Calendar || {};

LogicECM.module.WCalendar.Calendar.CALENDAR_CONTAINER = LogicECM.module.WCalendar.Calendar.CALENDAR_CONTAINER || calendarContainer;
LogicECM.module.WCalendar.Calendar.YEARS_LABEL = LogicECM.module.WCalendar.Calendar.YEARS_LABEL || "wcalendarYears";
LogicECM.module.WCalendar.Calendar.WORKING_DAYS_LABEL = LogicECM.module.WCalendar.Calendar.WORKING_DAYS_LABEL || "wcalendarWorkingDays";
LogicECM.module.WCalendar.Calendar.NON_WORKING_DAYS_LABEL = LogicECM.module.WCalendar.Calendar.NON_WORKING_DAYS_LABEL || "wcalendarNonWorkingDays";

//]]></script>
</@>

<#import "/ru/it/lecm/base/base-page.ftl" as bpage/>
<@bpage.basePage showToolbar=false>

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


</@bpage.basePage>
