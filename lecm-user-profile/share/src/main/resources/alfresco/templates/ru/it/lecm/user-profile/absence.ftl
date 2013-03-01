
<#include "/org/alfresco/include/alfresco-template.ftl"/>

<@templateHeader>
<#-- Скрипты, необходимые для диалоговых форм -->
<#include "/org/alfresco/components/form/form.get.head.ftl">

<#-- Custom stylesheets -->
<@link rel="stylesheet" type="text/css" href="${page.url.context}/css/lecm-base/components/base-menu/base-menu.css"/>
<@link rel="stylesheet" type="text/css" href="${page.url.context}/res/css/lecm-user-profile/user-profile-menu.css"/>

<#-- Скрипты меню, тулбара и датагрида -->
<@script type="text/javascript" src="${page.url.context}/res/scripts/lecm-calendar/utils.js"/>
<@script type="text/javascript" src="${page.url.context}/res/scripts/lecm-calendar/absence/absence-datagrid.js"/>
<@script type="text/javascript" src="${page.url.context}/res/scripts/lecm-calendar/absence/absence-profile-toolbar.js"/>
<@script type="text/javascript" src="${page.url.context}/res/scripts/lecm-user-profile/menu.js"/>
<#-- Валидаторы -->
<@script type="text/javascript" src="${page.url.context}/res/scripts/lecm-calendar/absence/date-interval-validation.js"/>
<#-- jQuery нужен для синхронных запросов в date-interval-validation.js -->
<@script type="text/javascript" src="${page.url.context}/res/jquery/jquery-1.6.2.js"/>

<script type="text/javascript">//<![CDATA[

var absenceContainer = ${absenceContainer};

if (typeof LogicECM == "undefined" || !LogicECM) {
	var LogicECM = {};
}

LogicECM.module = LogicECM.module || {};
LogicECM.module.WCalendar = LogicECM.module.WCalendar || {};
LogicECM.module.WCalendar.Absence = LogicECM.module.WCalendar.Absence || {};
LogicECM.module.WCalendar.Const = LogicECM.module.WCalendar.Const || {};

LogicECM.module.WCalendar.Absence.ABSENCE_CONTAINER = LogicECM.module.WCalendar.Absence.ABSENCE_CONTAINER || absenceContainer;
LogicECM.module.WCalendar.Absence.ABSENCE_PROFILE_LABEL = LogicECM.module.WCalendar.Absence.ABSENCE_PROFILE_LABEL || "absenceProfileDatagrid";

//]]></script>
</@>

<#import "/ru/it/lecm/base/base-page.ftl" as bpage/>
<@bpage.basePage showToolbar=true>
		<@region id="absence-profile" scope="template"/>
</@bpage.basePage>

