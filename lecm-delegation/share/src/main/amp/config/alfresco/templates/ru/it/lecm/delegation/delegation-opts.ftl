<#import "/ru/it/lecm/base/base-page.ftl" as bpage/>
<#include "/org/alfresco/components/form/form.dependencies.inc">


<script type="text/javascript">//<![CDATA[
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

(function() {
	"use strict";
	var response = ${response};

	LogicECM.module.Delegation.Const.isBoss = response.isBoss;
	LogicECM.module.Delegation.Const.isEngineer = response.isEngineer;
	LogicECM.module.Delegation.Const.hasSubordinate = response.hasSubordinate;
	LogicECM.module.Delegation.Const.employee = response.employee;
})();
//]]>
</script>


<@bpage.basePageSimple>
	<#if "me" == delegator>
		<@region id="content" scope="template"/>
	<#elseif nativeObject.isEngineer || (nativeObject.isBoss && nativeObject.hasSubordinate)>
		<@region id="content" scope="template"/>
	<#else/>
		<@region id="forbidden" scope="template"/>
	</#if>
</@bpage.basePageSimple>
