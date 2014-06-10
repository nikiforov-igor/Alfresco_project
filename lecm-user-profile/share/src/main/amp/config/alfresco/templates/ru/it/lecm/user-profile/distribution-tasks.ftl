<#include "/org/alfresco/include/alfresco-template.ftl"/>

<@templateHeader>
<#-- подключить все скрипты необходимые для диалоговых форм -->
	<#include "/org/alfresco/components/form/form.dependencies.inc">

<#-- Далее перечисляются самописные скрипты LogicECM  -->
<#-- Стили меню для страницы delegation-opts -->
	<@link rel="stylesheet" type="text/css" href="${url.context}/res/css/lecm-base/components/base-menu/base-menu.css"/>
	<@link rel="stylesheet" type="text/css" href="${url.context}/res/css/lecm-user-profile/user-profile-menu.css"/>
	<@link rel="stylesheet" type="text/css" href="${url.context}/res/components/workflow/task-list.css" />

<#-- Скрипты меню, тулбара и датагрида -->
	<@script type="text/javascript" src="${url.context}/res/jquery/jquery-1.6.2.js"></@script>
	<@script type="text/javascript" src="${url.context}/res/modules/simple-dialog.js"></@script>

<#-- скрипты меню -->
	<@script type="text/javascript" src="${url.context}/res/scripts/lecm-user-profile/menu.js"/>
</@>

<#import "/ru/it/lecm/base/base-page.ftl" as bpage/>
<@bpage.basePage showToolbar=true>
	<@region id="content" scope="template"/>
</@bpage.basePage>
