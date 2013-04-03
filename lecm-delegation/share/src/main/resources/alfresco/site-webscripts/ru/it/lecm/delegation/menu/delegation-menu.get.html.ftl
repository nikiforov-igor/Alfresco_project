<#import "/ru/it/lecm/base-share/components/base-components.ftl" as comp/>

<#assign pageId = page.id/>
<#assign selected = "delegationList"/>

<script type="text/javascript"> //<![CDATA[
	(function () {
		"use strict";
		var delegationMenu = new window.LogicECM.module.Delegation.Menu ("menu-buttons");
		delegationMenu.setMessages(${messages});
		delegationMenu.setOptions ({
			pageId: "${pageId}"
		});
	})();
//]]>
</script>

<@comp.baseMenu>
	<@comp.baseMenuButton "delegationList" "перечень делегирования"/>
</@comp.baseMenu>

