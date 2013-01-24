<#include "/org/alfresco/include/alfresco-template.ftl"/>

<@templateHeader>
<script type="text/javascript">//<![CDATA[

LogicECM.module.Delegation.Const.nodeRef = "${nativeObject.nodeRef}";
LogicECM.module.Delegation.Const.itemType = "${nativeObject.itemType}";
LogicECM.module.Delegation.Const.isBoss = ${nativeObject.isBoss?string};
LogicECM.module.Delegation.Const.isEngineer = ${nativeObject.isEngineer?string};
//]]>
</script>

<#-- подключить все скрипты необходимые для диалоговых форм -->
<#include "/org/alfresco/components/form/form.get.head.ftl">

<#-- Далее перечисляются стандартные скрипты из Alfresco -->
<@script type="text/javascript" src="${page.url.context}/res/modules/simple-dialog.js"/>

<#-- Далее перечисляются самописные скрипты LogicECM  -->
<#-- Стили меню для страницы delegation-list -->
<@link rel="stylesheet" type="text/css" href="${page.url.context}/css/lecm-base/components/base-menu/base-menu.css"/>
<@link rel="stylesheet" type="text/css" href="${page.url.context}/res/css/lecm-delegation/delegation-menu.css"/>
<#-- Стили тулбара для страницы delegation-list -->
<@link rel="stylesheet" type="text/css" href="${page.url.context}/res/components/data-lists/toolbar.css"/>
<@link rel="stylesheet" type="text/css" href="${page.url.context}/res/css/lecm-delegation/delegation-toolbar.css"/>
<#-- Data Grid stylesheet -->
<@link rel="stylesheet" type="text/css" href="${page.url.context}/res/components/data-lists/datagrid.css"/>

<#-- скрипт с константами -->
<@script type="text/javascript" src="${page.url.context}/res/scripts/lecm-delegation/delegation-const.js"/>
<#-- скрипты меню -->
<@script type="text/javascript" src="${page.url.context}/res/scripts/lecm-delegation/delegation-menu.js"/>
<#-- скрипты тулбара для страницы delegation-list -->
<@script type="text/javascript" src="${page.url.context}/res/scripts/lecm-delegation/list/delegation-list-toolbar.js"/>
<#-- Data Grid javascript-->
<@script type="text/javascript" src="${page.url.context}/res/scripts/lecm-base/components/lecm-datagrid.js"/>
<#-- Advanced search -->
<@script type="text/javascript" src="${page.url.context}/res/scripts/lecm-base/components/advsearch.js"/>
<#-- DelegationList Data Grid javascript -->
<@script type="text/javascript" src="${page.url.context}/res/scripts/lecm-delegation/list/delegation-list.js"/>

</@>

<#import "/ru/it/lecm/base/base-page.ftl" as bpage/>

<@bpage.basePage>
	<#if nativeObject.isEngineer || nativeObject.isBoss>
		<@region id="content" scope="template"/>
	<#else/>
		<@region id="forbidden" scope="template"/>
	</#if>
</@bpage.basePage>
