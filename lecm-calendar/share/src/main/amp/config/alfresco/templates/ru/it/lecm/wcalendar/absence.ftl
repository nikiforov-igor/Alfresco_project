<#include "/org/alfresco/include/alfresco-template.ftl"/>

<#-- Скрипты, необходимые для диалоговых форм -->
<#include "/org/alfresco/components/form/form.dependencies.inc">

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

<#assign showContent = isEngineer || isBoss>

<#import "/ru/it/lecm/base/base-page.ftl" as bpage/>
<@bpage.basePageSimple showToolbar=showContent>
	<#if showContent>
		<@region id="absence-summary-table-wrapper" scope="template"/>
		<@region id="absence" scope="template"/>
	<#else>
		<@region id="forbidden" scope="template"/>
	</#if>
</@bpage.basePageSimple>

