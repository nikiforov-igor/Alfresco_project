<#import "/ru/it/lecm/base-share/components/base-components.ftl" as comp/>

<#assign toolbarId = args.htmlid>

<#--
TODO: сделать настройку того, какие кнопки создавать при создании тулбара
-->
<script type="text/javascript"> //<![CDATA[
	(function () {
		var delegationToolbar = new LogicECM.module.Delegation.Toolbar ("${toolbarId}");
		delegationToolbar.setMessages(${messages});
	})();
//]]>
</script>

<#--
TODO: сделать отображение кнопок по условию, в зависимости от страницы
-->
<@comp.baseToolbar toolbarId true true true>
	<div id="${toolbarId}-btnCreateProcuracy"></div> <!-- кнопка создания доверенности (старая страница) -->
	<div id="${toolbarId}-btnRefreshProcuracies"></div> <!-- кнопка обновления страницы (старая страница) -->
	<div id="${toolbarId}-btnShowOnlyConfigured"></div> <!-- checkbox "отображать только настроенные" -->
	<div id="${toolbarId}-btnCreateDelegationOpts"></div> <!-- кнопка создания параметров делегирования (временная) -->
</@comp.baseToolbar>
