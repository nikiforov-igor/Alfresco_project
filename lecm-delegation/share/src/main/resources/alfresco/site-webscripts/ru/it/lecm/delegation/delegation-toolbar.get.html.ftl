<#import "/ru/it/lecm/base-share/components/base-components.ftl" as comp/>

<#assign toolbarId = args.htmlid/>

<script type="text/javascript"> //<![CDATA[
	(function () {
		var delegationToolbar = new LogicECM.module.Delegation.Toolbar ("${toolbarId}");
		delegationToolbar.setMessages(${messages});
		delegationToolbar.setOptions ({
			pageId: "${page}"
		});
	})();
//]]>
</script>

<@comp.baseToolbar toolbarId true true true>
	<#if "delegation" == page>
		<div id="${toolbarId}-btnCreateProcuracy"></div> <!-- кнопка создания доверенности (старая страница) -->
		<div id="${toolbarId}-btnRefreshProcuracies"></div> <!-- кнопка обновления страницы (старая страница) -->
	<#elseif "delegation-opts" == page>
		<div id="${toolbarId}-btnCreateDelegationOpts"></div> <!-- кнопка создания параметров делегирования (временная) -->
	</#if>
</@comp.baseToolbar>
