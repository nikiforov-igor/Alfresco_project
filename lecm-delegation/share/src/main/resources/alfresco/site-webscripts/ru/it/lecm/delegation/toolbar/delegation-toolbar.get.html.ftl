<#import "/ru/it/lecm/base-share/components/base-components.ftl" as comp/>

<#assign toolbarId = args.htmlid/>

<#assign pageId = page.id/>

<script type="text/javascript"> //<![CDATA[
	(function () {
		var delegationToolbar = new LogicECM.module.Delegation.Toolbar ("${toolbarId}");
		delegationToolbar.setMessages(${messages});
		delegationToolbar.setOptions ({
			pageId: "${pageId}"
		});
	})();
//]]>
</script>

<@comp.baseToolbar toolbarId true true true>
	<#if "delegation-list" == pageId>
		<div id="${toolbarId}-btnCreateDelegationList"></div> <!-- кнопка создания параметров делегирования (временная) -->
	<#elseif "delegation-opts" == pageId>
	<#elseif "delegation" == pageId>
		<div id="${toolbarId}-btnCreateProcuracy"></div> <!-- кнопка создания доверенности (старая страница) -->
		<div id="${toolbarId}-btnRefreshProcuracies"></div> <!-- кнопка обновления страницы (старая страница) -->
	</#if>
</@comp.baseToolbar>
