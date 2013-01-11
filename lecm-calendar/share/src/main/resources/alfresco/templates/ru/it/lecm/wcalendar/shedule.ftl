
<#include "/org/alfresco/include/alfresco-template.ftl"/>

<@templateHeader>

<#-- подключить все скрипты необходимые для диалоговых форм -->
<#include "/org/alfresco/components/form/form.get.head.ftl">

<#-- Далее перечисляются стандартные скрипты из Alfresco -->
<@script type="text/javascript" src="${page.url.context}/res/modules/simple-dialog.js"/>

<#-- Data Grid stylesheet -->
<@link rel="stylesheet" type="text/css" href="${page.url.context}/res/components/data-lists/datagrid.css"/>
<#-- Custom calendar stylesheet -->
<@link rel="stylesheet" type="text/css" href="${page.url.context}/css/lecm-calendar/wcalendar-shedule.css" />

<#-- Data Grid javascript-->
<@script type="text/javascript" src="${page.url.context}/res/scripts/lecm-base/components/lecm-datagrid.js"/>
<#-- Advanced search -->
<@script type="text/javascript" src="${page.url.context}/res/scripts/lecm-base/components/advsearch.js"/>
<#-- Служебные функции -->
<@script type="text/javascript" src="${page.url.context}/res/scripts/lecm-calendar/utils.js"/>

<@script type="text/javascript" src="${page.url.context}/res/scripts/lecm-calendar/shedule/time-validation.js" />

<script type="text/javascript">//<![CDATA[

var sheduleContainer = ${sheduleContainer};

if (typeof LogicECM == "undefined" || !LogicECM) {
	var LogicECM = {};
}

LogicECM.module = LogicECM.module || {};
LogicECM.module.WCalendar = LogicECM.module.WCalendar || {};
LogicECM.module.WCalendar.Shedule = LogicECM.module.WCalendar.Shedule || {};

LogicECM.module.WCalendar.Shedule.SHEDULE_CONTAINER = LogicECM.module.WCalendar.Shedule.SHEDULE_CONTAINER || sheduleContainer;
LogicECM.module.WCalendar.Shedule.SHEDULE_LABEL = LogicECM.module.WCalendar.Shedule.SHEDULE_LABEL || "sheduleDatagrid";

//]]></script>

</@>

<#import "/ru/it/lecm/base/base-page.ftl" as bpage/>
<@bpage.basePage>
    <@region id="shedule" scope="template"/>
</@bpage.basePage>
