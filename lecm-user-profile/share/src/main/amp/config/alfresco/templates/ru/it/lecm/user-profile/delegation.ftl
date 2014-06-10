<#include "/org/alfresco/include/alfresco-template.ftl"/>

<@templateHeader>
<#-- подключить все скрипты необходимые для диалоговых форм -->
<#include "/org/alfresco/components/form/form.dependencies.inc">

<#-- Далее перечисляются самописные скрипты LogicECM  -->
<#-- Стили меню для страницы delegation-opts -->
<@link rel="stylesheet" type="text/css" href="${url.context}/res/css/lecm-base/components/base-menu/base-menu.css"/>
<@link rel="stylesheet" type="text/css" href="${url.context}/res/css/lecm-user-profile/user-profile-menu.css"/>
<@link rel="stylesheet" type="text/css" href="${url.context}/res/css/lecm-delegation/opts/procuracy-grid.css"/>

<#-- скрипт с константами -->
<#--<@script type="text/javascript" src="${url.context}/res/scripts/lecm-delegation/delegation-const.js"/>-->
<#-- валидаторы -->
<@script type="text/javascript" src="${url.context}/res/scripts/lecm-delegation/delegation-validator.js"/>
<#-- скрипты меню -->
<#-- <@script type="text/javascript" src="${url.context}/res/scripts/lecm-calendar/absence/absence-instant.js"/> -->
<@script type="text/javascript" src="${url.context}/res/modules/simple-dialog.js"></@script>
<#-- нужен в base-utils -->
<@script type="text/javascript" src="${url.context}/res/jquery/jquery-1.6.2.js"></@script>
<#--загрузка base-utils.js вынесена в base-share-config-custom.xml-->
<#--<@script type="text/javascript" src="${url.context}/res/scripts/lecm-base/components/base-utils.js"></@script>-->
<@script type="text/javascript" src="${url.context}/res/scripts/lecm-user-profile/menu.js"/>
<@script type="text/javascript" src="${url.context}/res/scripts/lecm-base/components/advsearch.js"></@script>
<@script type="text/javascript" src="${url.context}/res/scripts/lecm-base/components/lecm-datagrid.js"></@script>
<@script type="text/javascript" src="${url.context}/res/scripts/lecm-delegation/opts/procuracy-grid.js"/>
<@script type="text/javascript" src="${url.context}/res/scripts/lecm-delegation/opts/delegation-opts.js"/>

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

(function (){
	"use strict";
	var response = ${response};

	LogicECM.module.Delegation.Const.isBoss = response.isBoss;
	LogicECM.module.Delegation.Const.isEngineer = response.isEngineer;
	LogicECM.module.Delegation.Const.hasSubordinate = response.hasSubordinate;
	LogicECM.module.Delegation.Const.employee = response.employee;
})();
//]]>
</script>
</@>

<#import "/ru/it/lecm/base/base-page.ftl" as bpage/>
<@bpage.basePage>
	<@region id="content" scope="template"/>
</@bpage.basePage>
