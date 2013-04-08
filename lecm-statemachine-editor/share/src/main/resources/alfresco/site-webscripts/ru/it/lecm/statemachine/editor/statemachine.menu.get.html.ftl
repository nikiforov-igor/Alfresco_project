<#if hasRole>

<#if page.url.args.statemachineId??>
<#assign id = args.htmlid>
<#assign menuId = "menu-buttons">
<#import "/ru/it/lecm/base-share/components/base-components.ftl" as comp/>
<@comp.baseMenu>
<div id="statemachine-status-menu"></div>
<div id="statemachine-properties-menu"></div>

<div class="statemachine-menu">
    <span id="menu-buttons-status-menu-button" class="yui-button yui-push-button">
        <span class="first-child">
            <input type="button" title="Добавить статус">
        </span>
    </span>
</div>

<div class="statemachine-menu">
		<span id="menu-buttons-properties-menu-button" class="yui-button yui-push-button">
	        <span class="first-child">
	            <button type="button" title="Свойства">&nbsp;</button>
	        </span>
	    </span>
</div>
<div class="statemachine-menu">
		<span id="menu-buttons-machine-deploy" class="yui-button yui-push-button">
	        <span class="first-child">
	            <button type="button" title="Развернуть">&nbsp;</button>
	        </span>
	    </span>
</div>
<div class="statemachine-menu">
		<span id="menu-buttons-machine-export" class="yui-button yui-push-button">
	        <span class="first-child">
	            <button type="button" title="${msg('button.export-xml')}">&nbsp;</button>
	        </span>
	    </span>
</div>

<div>
    <form method="post" id="${menuId}-import-xml-form" enctype="multipart/form-data"
          action="${url.context}/proxy/alfresco/lecm/statemachine/editor/import">
        <div id="${menuId}-import-xml" class="import-xml" title="${msg('button.import-xml')}">
                        <span id="${menuId}-machine-import" class="yui-button yui-push-button">
                            <span class="first-child">
                                <button type="button" title="${msg('button.import-xml')}">&nbsp;</button>
                            </span>
                        </span>
            <input type="file" id="${menuId}-import-xml-input" name="f" accept=".xml,application/xml,text/xml">
            <input type="hidden" id="${menuId}-stateMachineId-input" name="stateMachineId" value="${page.url.args.statemachineId}">
        </div>
    </form>
</div>

</@comp.baseMenu>
</#if>
</#if>