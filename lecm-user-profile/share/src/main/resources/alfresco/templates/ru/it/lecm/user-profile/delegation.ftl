<#include "/org/alfresco/include/alfresco-template.ftl"/>

<@templateHeader>
<#-- подключить все скрипты необходимые для диалоговых форм -->
<#include "/org/alfresco/components/form/form.get.head.ftl">

<#-- Далее перечисляются стандартные скрипты из Alfresco -->
<@script type="text/javascript" src="${page.url.context}/res/modules/simple-dialog.js"/>

<#-- Далее перечисляются самописные скрипты LogicECM  -->
<#-- Стили меню для страницы delegation-opts -->
<@link rel="stylesheet" type="text/css" href="${page.url.context}/css/lecm-base/components/base-menu/base-menu.css"/>
<@link rel="stylesheet" type="text/css" href="${page.url.context}/res/css/lecm-user-profile/user-profile-menu.css"/>
<#-- Data Grid stylesheet -->
<@link rel="stylesheet" type="text/css" href="${page.url.context}/res/components/data-lists/datagrid.css"/>

<#-- скрипт с константами -->
<@script type="text/javascript" src="${page.url.context}/res/scripts/lecm-delegation/delegation-const.js"/>
<#-- скрипты меню -->
<@script type="text/javascript" src="${page.url.context}/res/scripts/lecm-user-profile/menu.js"/>
<#-- Data Grid javascript-->
<@script type="text/javascript" src="${page.url.context}/res/scripts/lecm-base/components/lecm-datagrid.js"/>
<#-- Advanced search -->
<@script type="text/javascript" src="${page.url.context}/res/scripts/lecm-base/components/advsearch.js"/>
<@script type="text/javascript" src="${page.url.context}/res/scripts/lecm-delegation/opts/procuracy-grid.js"/>
<@script type="text/javascript" src="${page.url.context}/res/scripts/lecm-delegation/opts/delegation-opts.js"/>

<script type="text/javascript">//<![CDATA[

var response = ${response};

initDelegationConst ();

LogicECM.module.Delegation.Const.isBoss = response.isBoss;
LogicECM.module.Delegation.Const.isEngineer = response.isEngineer;
LogicECM.module.Delegation.Const.hasSubordinate = response.hasSubordinate;

//]]>
</script>
</@>

<#import "/ru/it/lecm/base/base-page.ftl" as bpage/>
<@bpage.basePage>
	<#if "current" == delegator>
		<@region id="content" scope="template"/>
	<#elseif nativeObject.isEngineer || (nativeObject.isBoss && nativeObject.hasSubordinate)>
		<@region id="content" scope="template"/>
	<#else/>
		<@region id="forbidden" scope="template"/>
	</#if>
</@bpage.basePage>
