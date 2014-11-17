<script type="text/javascript">//<![CDATA[

if (typeof LogicECM == "undefined" || !LogicECM) {
	LogicECM = {};
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

<#import "/ru/it/lecm/base/base-page.ftl" as bpage/>
<@bpage.basePageSimple>
	<@region id="content" scope="template"/>
</@bpage.basePageSimple>
