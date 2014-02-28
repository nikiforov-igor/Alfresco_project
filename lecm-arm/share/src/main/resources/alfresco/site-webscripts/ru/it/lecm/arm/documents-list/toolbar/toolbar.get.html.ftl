<#include "/org/alfresco/components/component.head.inc">
<!-- Data List Toolbar -->
<@script type="text/javascript" src="${page.url.context}/scripts/lecm-arm/arm-documents-toolbar.js"></@script>

<#assign id = args.htmlid>
<script type="text/javascript">
(function(){
    function init() {
        new LogicECM.module.ARM.DocumentsToolbar("${id}").setMessages(${messages});
    }

    YAHOO.util.Event.onDOMReady(init);
})();
//<![CDATA[
//]]></script>

<#import "/ru/it/lecm/base-share/components/base-components.ftl" as comp/>
<@comp.baseToolbar id true true true>
<div class="filters-block">
	<div class="filters">
	    <span id="${id}-filtersButton" class="yui-button yui-push-button yui-menu-button">
	       <span class="first-child">
	          <button type="button" title="${msg("btn.filters")}">${msg("btn.filters")}</button>
	       </span>
	    </span>
	</div>
	<div id="${id}-filters-dialog" class="filters-dialog">
		<div id="${id}-filters-dialog-content" class="filters-dialog-content"></div>
		<div class="filters-dialog-buttons">
            <span id="${id}-filters-apply-button" class="yui-button yui-push-button filters-icon">
                <span class="first-child">
                    <button type="button">${msg('filter.button.ok')}</button>
                </span>
            </span>
		</div>
	</div>
</div>
<div class="group-actions">
    <span id="${id}-groupActionsButton" class="yui-button yui-push-button">
       <span class="first-child">
          <button type="button">${msg("button.group-actions")}</button>
       </span>
    </span>
</div>

</@comp.baseToolbar>
