<#import "/ru/it/lecm/base-share/components/base-components.ftl" as comp/>

<script type="text/javascript"> //<![CDATA[
	(function () {
		var delegationMenu = new LogicECM.module.Delegation.Menu ("menu-buttons");
		delegationMenu.setMessages(${messages});
	})();
//]]>
</script>

<@comp.baseMenu>
	<@comp.baseMenuButton "delegationOpts" "настройка параметров делегирования" args.selected/>
	<#-- TODO: перечень делегирования показывается только тогда, когда есть права (технолог или начальник) -->
	<@comp.baseMenuButton "delegationList" "перечень делегирования" args.selected/>
</@comp.baseMenu>

