<#assign id = args.htmlid,
         selected = args.selected/>
<#import "/ru/it/lecm/base-share/components/base-components.ftl" as comp/>
<@comp.baseMenu>
    <@comp.baseMenuButton "employees" msg('lecm.orgstructure.employees.btn') selected/>
    <@comp.baseMenuButton "staff" msg('lecm.orgstructure.staff-list.btn') selected/>
    <@comp.baseMenuButton "orgstructure" msg('lecm.orgstructure.orgstructure.btn') selected/>
    <@comp.baseMenuButton "workGroup" msg('lecm.orgstructure.work-groups.btn') selected/>
    <@comp.baseMenuButton "positions" msg('lecm.orgstructure.positions.btn') selected/>
    <@comp.baseMenuButton "roles" msg('lecm.orgstructure.roles.btn') selected/>
    <@comp.baseMenuButton "organization" msg('lecm.organization.roles.btn') selected/>
</@comp.baseMenu>

<script type="text/javascript">//<![CDATA[

(function() {
	function init() {
		var menu = new LogicECM.module.OrgStructure.Menu("menu-buttons");
		menu.setMessages(${messages});
		menu.draw();
	}
	YAHOO.util.Event.onDOMReady(init);
})();
//]]></script>
