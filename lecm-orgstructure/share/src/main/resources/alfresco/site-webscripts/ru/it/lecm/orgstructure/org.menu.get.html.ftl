<#assign id = args.htmlid>
<#import "/ru/it/lecm/base-share/components/base-components.ftl" as comp/>
<@comp.baseMenu>
	<br/>
	<div class="organization-menu">
		<span id="menu-buttons-employeesBtn" class="yui-button yui-push-button">
	        <span class="first-child">
	            <button type="button" title="${msg('lecm.orgstructure.employees.btn')}">${msg('lecm.orgstructure.employees.btn')}</button>
	        </span>
	    </span>
	</div><br/>
	<div class="organization-menu">
		<span id="menu-buttons-staffBtn" class="yui-button yui-push-button">
            <span class="first-child">
                <button type="button" title="${msg('lecm.orgstructure.staff-list.btn')}">${msg('lecm.orgstructure.staff-list.btn')}</button>
            </span>
	    </span>
	</div><br/>
	<div class="organization-menu">
		<span id="menu-buttons-orgstructureBtn" class="yui-button yui-push-button">
            <span class="first-child">
                <button type="button" title="${msg('lecm.orgstructure.orgstructure.btn')}">${msg('lecm.orgstructure.orgstructure.btn')}</button>
            </span>
	    </span>
	</div><br/>
	<div class="organization-menu">
		<span id="menu-buttons-workGroupBtn" class="yui-button yui-push-button">
            <span class="first-child">
                <button type="button" title="${msg('lecm.orgstructure.work-groups.btn')}">${msg('lecm.orgstructure.work-groups.btn')}</button>
            </span>
	    </span>
	</div><br/>
	<div class="organization-menu">
		<span id="menu-buttons-positionsBtn" class="yui-button yui-push-button">
            <span class="first-child">
                <button type="button" title="${msg('lecm.orgstructure.positions.btn')}">${msg('lecm.orgstructure.positions.btn')}</button>
            </span>
	    </span>
	</div><br/>
	<div class="organization-menu">
		<span id="menu-buttons-rolesBtn" class="yui-button yui-push-button">
            <span class="first-child">
                <button type="button" title="${msg('lecm.orgstructure.roles.btn')}">${msg('lecm.orgstructure.roles.btn')}</button>
            </span>
	    </span>
	</div><br/>
	<div class="organization-menu">
		<span id="menu-buttons-organizationBtn" class="yui-button yui-push-button">
            <span class="first-child">
                <button type="button" title="${msg('lecm.orgstructure.organization.btn')}">${msg('lecm.orgstructure.organization.btn')}</button>
            </span>
	    </span>
	</div>
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
