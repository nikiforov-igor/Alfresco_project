<#include "/org/alfresco/include/alfresco-template.ftl"/>

<@templateHeader>
<#-- подключить все скрипты необходимые для диалоговых форм -->
	<#include "/org/alfresco/components/form/form.dependencies.inc">

<#-- Далее перечисляются самописные скрипты LogicECM  -->
<#-- Стили меню для страницы delegation-opts -->
	<@link rel="stylesheet" type="text/css" href="${url.context}/res/css/lecm-base/components/base-menu/base-menu.css"/>
	<@link rel="stylesheet" type="text/css" href="${url.context}/res/css/lecm-user-profile/user-profile-menu.css"/>

<#-- скрипты меню -->
	<@script type="text/javascript" src="${url.context}/res/scripts/lecm-calendar/absence/instant-absence-page.js"/>
	<@script type="text/javascript" src="${url.context}/res/scripts/lecm-user-profile/menu.js"/>
</@>

<#import "/ru/it/lecm/base/base-page.ftl" as bpage/>
<@bpage.basePage>
	<@region id="applet-settings" scope="template"/>
</@bpage.basePage>
