<#assign id = args.htmlid,
selected = args.selected/>
<#import "/ru/it/lecm/base-share/components/base-components.ftl" as comp/>
<@comp.baseMenu>
	<@comp.baseMenuButton "object" msg('lecm.subscriptions.object.btn') selected/>
	<@comp.baseMenuButton "type" msg('lecm.subscriptions.type.btn') selected/>
	<#--<@comp.baseMenuButton "orgstructure" msg('lecm.orgstructure.orgstructure.btn') selected/>-->
	<#--<@comp.baseMenuButton "workGroup" msg('lecm.orgstructure.work-groups.btn') selected/>-->
	<#--<@comp.baseMenuButton "positions" msg('lecm.orgstructure.positions.btn') selected/>-->
	<#--<@comp.baseMenuButton "roles" msg('lecm.orgstructure.roles.btn') selected/>-->
	<#--<@comp.baseMenuButton "organization" msg('lecm.orgstructure.organization.btn') selected/>-->
	<#--<@comp.baseMenuButton "businessRoles" msg('lecm.orgstructure.businessRoles.btn') selected/>-->
</@comp.baseMenu>

<script type="text/javascript">//<![CDATA[

(function() {
    function init() {
        var menu = new LogicECM.module.Subscriptions.Menu("menu-buttons");
        menu.setMessages(${messages});
        menu.draw();
    }
    YAHOO.util.Event.onDOMReady(init);
})();
//]]></script>
