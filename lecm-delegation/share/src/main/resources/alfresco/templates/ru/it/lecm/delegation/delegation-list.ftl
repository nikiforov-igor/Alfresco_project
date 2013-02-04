<#include "/org/alfresco/include/alfresco-template.ftl"/>

<@templateHeader>
<#-- подключить все скрипты необходимые для диалоговых форм -->
<#include "/org/alfresco/components/form/form.get.head.ftl">

<#-- Далее перечисляются стандартные скрипты из Alfresco -->

<#-- Далее перечисляются самописные скрипты LogicECM  -->
<#-- Стили меню для страницы delegation-list -->
<@link rel="stylesheet" type="text/css" href="${page.url.context}/css/lecm-base/components/base-menu/base-menu.css"/>
<@link rel="stylesheet" type="text/css" href="${page.url.context}/res/css/lecm-delegation/delegation-menu.css"/>
<#-- Стили тулбара для страницы delegation-list -->
<@link rel="stylesheet" type="text/css" href="${page.url.context}/res/components/data-lists/toolbar.css"/>

<#-- скрипт с константами -->
<@script type="text/javascript" src="${page.url.context}/res/scripts/lecm-delegation/delegation-const.js"/>
<#-- скрипты меню -->
<@script type="text/javascript" src="${page.url.context}/res/scripts/lecm-delegation/delegation-menu.js"/>
<#-- скрипты тулбара для страницы delegation-list -->
<@script type="text/javascript" src="${page.url.context}/res/scripts/lecm-delegation/list/delegation-list-toolbar.js"/>
<#-- DelegationList Data Grid javascript -->
<@script type="text/javascript" src="${page.url.context}/res/scripts/lecm-delegation/list/delegation-list.js"/>

<script type="text/javascript">//<![CDATA[

var response = ${response};

LogicECM.module.Delegation.Const.nodeRef = response.nodeRef;
LogicECM.module.Delegation.Const.itemType = response.itemType;
LogicECM.module.Delegation.Const.isBoss = response.isBoss;
LogicECM.module.Delegation.Const.isEngineer = response.isEngineer;
//]]>
</script>
</@>

<#import "/ru/it/lecm/base/base-page.ftl" as bpage/>

<#assign showContent = nativeObject.isEngineer || nativeObject.isBoss/>

<@bpage.basePage showToolbar=showContent>
	<#if showContent>
		<@region id="content" scope="template"/>
	<#else/>
		<@region id="forbidden" scope="template"/>
	</#if>
</@bpage.basePage>
