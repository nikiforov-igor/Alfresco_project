<#include "/org/alfresco/include/alfresco-template.ftl"/>

<@templateHeader>
<#-- подключить все скрипты необходимые для диалоговых форм -->
<#include "/org/alfresco/components/form/form.dependencies.inc">

<#-- Далее перечисляются стандартные скрипты из Alfresco -->

<#-- Далее перечисляются самописные скрипты LogicECM  -->
<#-- Стили меню для страницы delegation-list -->
<@link rel="stylesheet" type="text/css" href="${url.context}/res/css/lecm-base/components/base-menu/base-menu.css"/>
<@link rel="stylesheet" type="text/css" href="${url.context}/res/css/lecm-delegation/delegation-menu.css"/>
<#-- Стили тулбара для страницы delegation-list -->
<@link rel="stylesheet" type="text/css" href="${url.context}/res/components/data-lists/toolbar.css"/>
<#-- Стили для DelegationList Data Grid -->
<@link rel="stylesheet" type="text/css" href="${url.context}/res/css/lecm-delegation/list/delegation-list.css"/>

<#-- скрипт с константами -->
<#-- <@script type="text/javascript" src="${url.context}/res/scripts/lecm-delegation/delegation-const.js"/> -->
<#-- скрипты меню -->
<@script type="text/javascript" src="${url.context}/res/scripts/lecm-delegation/delegation-menu.js"/>
<#-- скрипты тулбара для страницы delegation-list -->
<@script type="text/javascript" src="${url.context}/res/scripts/lecm-base/components/lecm-toolbar.js"></@script>
<@script type="text/javascript" src="${url.context}/res/scripts/lecm-delegation/list/delegation-list-toolbar.js"/>
<#-- DelegationList Data Grid javascript -->
<@script type="text/javascript" src="${url.context}/res/modules/simple-dialog.js"/>
<@script type="text/javascript" src="${url.context}/res/jquery/jquery-1.6.2.js"/>
<#--загрузка base-utils.js вынесена в base-share-config-custom.xml-->
<#--<@script type="text/javascript" src="${url.context}/res/scripts/lecm-base/components/base-utils.js"></@script>-->
<@script type="text/javascript" src="${url.context}/res/scripts/lecm-base/components/advsearch.js"></@script>
<@script type="text/javascript" src="${url.context}/res/scripts/lecm-base/components/lecm-datagrid.js"></@script>
<@script type="text/javascript" src="${url.context}/res/scripts/lecm-delegation/list/delegation-list.js"/>

<script type="text/javascript">//<![CDATA[
if (typeof LogicECM == "undefined" || !LogicECM) {
	var LogicECM = {};
}

LogicECM.module = LogicECM.module || {};
LogicECM.module.Delegation = LogicECM.module.Delegation || {};

LogicECM.module.Delegation.Const = LogicECM.module.Delegation.Const || {
		"nodeRef": "", //nodeRef папки в которой хранятся данные с перечнем делегирования
		"itemType": "", //тип данных который отображается в таблице с перечнем делегирования
		"isBoss": false, //является ли текущий пользователь руководителем в каком либо подразделении
		"isEngineer": false, //является ли текущий пользователь технологом
		"hasSubordinate": false, //есть ли в пользователя подчиненный (используется только на странице delegation-opts)
		"employee": null //nodeRef lecm-orgstr:employee ссылка на сотрудника для которого были открыты параметры делегирования
	};
(function () {
	"use strict";
	var response = ${response};

	LogicECM.module.Delegation.Const.nodeRef = response.nodeRef;
	LogicECM.module.Delegation.Const.itemType = response.itemType;
	LogicECM.module.Delegation.Const.isBoss = response.isBoss;
	LogicECM.module.Delegation.Const.isEngineer = response.isEngineer;
})();
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
