<#import "/ru/it/lecm/base-share/components/base-components.ftl" as comp/>

<#assign pageId = page.id/>
<#assign selected = "delegationList"/>

<script type="text/javascript"> //<![CDATA[
	(function () {
		var delegationMenu = new LogicECM.module.Delegation.Menu ("menu-buttons");
		delegationMenu.setMessages(${messages});
		delegationMenu.setOptions ({
			pageId: "${pageId}"
		});
	})();
//]]>
</script>

<@comp.baseMenu>
	<#if "delegation-list" == pageId>
		<@comp.baseMenuButton "delegationList" "перечень делегирования"/>
	<#elseif "delegation-opts" == pageId>
		<#-- selected передается тогда и только тогда когда есть права -->
		<@comp.baseMenuButton "delegationList" "перечень делегирования" selected/>
	</#if>
</@comp.baseMenu>

