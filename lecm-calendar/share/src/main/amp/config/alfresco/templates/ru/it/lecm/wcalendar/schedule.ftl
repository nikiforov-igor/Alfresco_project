<#include "/org/alfresco/include/alfresco-template.ftl"/>
<#-- Скрипты, необходимые для диалоговых форм -->
<#include "/org/alfresco/components/form/form.dependencies.inc">

<script type="text/javascript">//<![CDATA[
LogicECM.module = LogicECM.module || {};
LogicECM.module.WCalendar = LogicECM.module.WCalendar || {};
LogicECM.module.WCalendar.Schedule = LogicECM.module.WCalendar.Schedule || {};
LogicECM.module.WCalendar.Const = LogicECM.module.WCalendar.Const || {};

(function() {
	var scheduleContainer = ${scheduleContainer};
	var scheduleRoles = ${calendarRoles};

	LogicECM.module.WCalendar.Const.ROLES = LogicECM.module.WCalendar.Const.ROLES || scheduleRoles;

	LogicECM.module.WCalendar.Schedule.SCHEDULE_CONTAINER = LogicECM.module.WCalendar.Schedule.SCHEDULE_CONTAINER || scheduleContainer;
	LogicECM.module.WCalendar.Schedule.SCHEDULE_LABEL = LogicECM.module.WCalendar.Schedule.SCHEDULE_LABEL || "scheduleDatagrid";

	LogicECM.module.WCalendar.Schedule.ORGANIZATION_NODE_REF = "${orgNodeRef}";
})();
//]]></script>

<#assign showContent = isEngineer || isBoss>

<#import "/ru/it/lecm/base/base-page.ftl" as bpage/>
<@bpage.basePageSimple showToolbar=showContent>
	<#if showContent>
		<@region id="schedule" scope="template"/>
	<#else/>
		<@region id="forbidden" scope="template"/>
	</#if>
</@bpage.basePageSimple>
