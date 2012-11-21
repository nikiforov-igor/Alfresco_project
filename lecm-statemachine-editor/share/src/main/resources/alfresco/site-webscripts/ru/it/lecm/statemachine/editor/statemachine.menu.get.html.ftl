<#assign id = args.htmlid>
<#import "/ru/it/lecm/base-share/components/base-components.ftl" as comp/>
<@comp.baseMenu>
	<div class="organization-menu">
		<span id="menu-buttons-employeesBtn" class="yui-button yui-push-button">
	        <span class="first-child">
	            <button type="button" title="${msg('lecm.orgstructure.employees.btn')}">&nbsp;</button>
	        </span>
	    </span>
	</div>
	<div class="organization-menu">
		<span id="menu-buttons-staffBtn" class="yui-button yui-push-button">
            <span class="first-child">
                <button type="button" title="${msg('lecm.orgstructure.staff-list.btn')}">&nbsp;</button>
            </span>
	    </span>
	</div>
	<div class="organization-menu">
		<span id="menu-buttons-orgstructureBtn" class="yui-button yui-push-button">
            <span class="first-child">
                <button type="button" title="${msg('lecm.orgstructure.orgstructure.btn')}">&nbsp;</button>
            </span>
	    </span>
	</div>
	<div class="organization-menu">
		<span id="menu-buttons-workGroupBtn" class="yui-button yui-push-button">
            <span class="first-child">
                <button type="button" title="${msg('lecm.orgstructure.work-groups.btn')}">&nbsp;</button>
            </span>
	    </span>
	</div>
	<div class="organization-menu">
		<span id="menu-buttons-positionsBtn" class="yui-button yui-push-button">
            <span class="first-child">
                <button type="button" title="${msg('lecm.orgstructure.positions.btn')}">&nbsp;</button>
            </span>
	    </span>
	</div>
	<div class="organization-menu">
		<span id="menu-buttons-rolesBtn" class="yui-button yui-push-button">
            <span class="first-child">
                <button type="button" title="${msg('lecm.orgstructure.roles.btn')}">&nbsp;</button>
            </span>
	    </span>
	</div>
	<div class="organization-menu">
		<span id="menu-buttons-organizationBtn" class="yui-button yui-push-button">
            <span class="first-child">
                <button type="button" title="${msg('lecm.orgstructure.organization.btn')}">&nbsp;</button>
            </span>
	    </span>
	</div>
</@comp.baseMenu>
