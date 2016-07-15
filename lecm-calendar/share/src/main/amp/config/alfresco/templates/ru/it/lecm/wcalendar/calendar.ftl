<#import "/ru/it/lecm/base/base-page.ftl" as bpage/>
<#-- Скрипты, необходимые для диалоговых форм -->
<#include "/org/alfresco/components/form/form.dependencies.inc">
<script type="text/javascript">//<![CDATA[
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


<#import "/ru/it/lecm/base-share/components/2-panels-with-splitter.ftl" as panels/>
<@bpage.basePageSimple>
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
</@bpage.basePageSimple>
