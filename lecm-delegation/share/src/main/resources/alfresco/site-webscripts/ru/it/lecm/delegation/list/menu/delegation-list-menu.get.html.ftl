<#import "/ru/it/lecm/base-share/components/base-components.ftl" as comp/>

<script type="text/javascript"> //<![CDATA[
	(function () {
		var delegationMenu = new LogicECM.module.Delegation.List.Menu ("menu-buttons");
		delegationMenu.setMessages(${messages});
	})();
//]]>
</script>

<@comp.baseMenu>
	<@comp.baseMenuButton "delegationList" "перечень делегирования" args.selected/>
</@comp.baseMenu>

