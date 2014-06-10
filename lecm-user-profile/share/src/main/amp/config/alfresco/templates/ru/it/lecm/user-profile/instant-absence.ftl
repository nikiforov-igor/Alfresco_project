<#include "/org/alfresco/include/alfresco-template.ftl"/>

<@templateHeader>
<#-- Скрипты, необходимые для диалоговых форм -->
<#include "/org/alfresco/components/form/form.dependencies.inc">

<#-- Custom stylesheets -->
<@link rel="stylesheet" type="text/css" href="${url.context}/res/css/lecm-base/components/base-menu/base-menu.css"/>
<@link rel="stylesheet" type="text/css" href="${url.context}/res/css/lecm-user-profile/user-profile-menu.css"/>

<#-- Скрипты меню, тулбара и датагрида -->
<@script type="text/javascript" src="${url.context}/res/scripts/lecm-calendar/absence/instant-absence-page.js"/>
<@script type="text/javascript" src="${url.context}/res/scripts/lecm-user-profile/menu.js"/>
<#-- Скрипт для страницы "Меня нет в офисе" -->

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
(function () {
	var absenceContainer = ${absenceContainer};
	LogicECM.module.WCalendar.Absence.ABSENCE_CONTAINER = LogicECM.module.WCalendar.Absence.ABSENCE_CONTAINER || absenceContainer;
})();
//]]></script>
</@>

<#import "/ru/it/lecm/base/base-page.ftl" as bpage/>
<@bpage.basePage showToolbar=false>
	<@region id="absence-instant" scope="template"/>
</@bpage.basePage>