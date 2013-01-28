<#import "/ru/it/lecm/base-share/components/base-components.ftl" as comp/>

<#assign pageId = page.id/>

<script type="text/javascript"> //<![CDATA[
	(function () {
		var userProfileMenu = new LogicECM.module.UserProfile.Menu("menu-buttons");
		userProfileMenu.setMessages(${messages});
		userProfileMenu.setOptions ({
			pageId: "${pageId}"
		});
	})();
//]]>
</script>

<@comp.baseMenu>
    <@comp.baseMenuButton "userProfileAbsence" msg('label.user-profile.menu.absence.btn') selected/>
	<@comp.baseMenuButton "userProfileDelegation" msg('label.user-profile.menu.delegation.btn')/>
</@comp.baseMenu>

