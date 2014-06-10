<#import "/ru/it/lecm/base-share/components/lecm-datagrid.ftl" as grid/>

<#assign id = args.htmlid/>

<#if delegator??>
<script type="text/javascript">//<![CDATA[
	(function(){
		"use strict";
		var delegationOpts = new LogicECM.module.Delegation.DelegationOpts('${id}');
		delegationOpts.setMessages (${messages});
		delegationOpts.setOptions ({
			delegator: "${delegator}",
			isActive: ${isActive?string}
		});
	})();
//]]>
</script>

<div id="${id}-content-part1" class="delegation-content"></div>
<#else>
<div class="not-fount-procuracy">
	<h1 class="theme-color-3">
		<span>Невозможно отобразить страницу с настройками делегирования. Связка пользователь-сотрудник не настроена.</span>
	</h1>
</div>
</#if>
