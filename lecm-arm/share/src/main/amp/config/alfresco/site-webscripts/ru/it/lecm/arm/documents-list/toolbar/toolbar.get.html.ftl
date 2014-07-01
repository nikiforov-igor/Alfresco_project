<#include "/org/alfresco/components/component.head.inc">
<!-- Data List Toolbar -->
<@markup id="js">
	<@script type="text/javascript" src="${url.context}/res/scripts/lecm-base/components/lecm-toolbar.js"></@script>
	<@script type="text/javascript" src="${url.context}/res/scripts/lecm-arm/arm-documents-toolbar.js"></@script>
</@>

<#assign id = args.htmlid>
<script type="text/javascript">
(function(){
    function init() {
        new LogicECM.module.ARM.DocumentsToolbar("${id}").setMessages(${messages}).setOptions({
            bubblingLabel: "documents-arm"
        });
    }

    YAHOO.util.Event.onDOMReady(init);
})();
//<![CDATA[
//]]></script>

<#import "/ru/it/lecm/base-share/components/base-components.ftl" as comp/>
<@comp.baseToolbar id true true true>
<div class="filters-block">
	<div id="${id}-filters-button-container" class="filters">
	    <span id="${id}-filtersButton" class="yui-button yui-push-button yui-menu-button">
	       <span class="first-child">
	          <button type="button" title="${msg("btn.filters")}">${msg("btn.filters")}</button>
	       </span>
	    </span>
	</div>
	<div id="${id}-filters-dialog" class="yui-panel filters-dialog">
		<div id="${id}-filters-dialog" class="bd">
			<div id="${id}-filters-dialog-content" class="filters-dialog-content"></div>
			<div class="bdft">
	            <span id="${id}-filters-apply-button" class="yui-button yui-push-button filters-icon">
	                <span class="first-child">
	                    <button type="button" tabindex="0">${msg("filter.button.ok")}</button>
	                </span>
	            </span>
				<span id="${id}-filters-cancel-button" class="yui-button yui-push-button filters-icon">
	                <span class="first-child">
	                    <button type="button" tabindex="0">${msg("filter.button.cancel")}</button>
	                </span>
	            </span>
			</div>
		</div>
	</div>
</div>
<div class="group-actions">
    <div class="actions">
        <span id="${id}-groupActionsButton" class="yui-button yui-push-button">
           <span class="first-child">
              <button type="button">${msg("button.group-actions")}</button>
           </span>
        </span>
    </div>
</div>
<div class="filters-block">
    <div id="${id}-columns-button-container" class="columns">
	    <span id="${id}-columnsButton" class="yui-button yui-push-button yui-menu-button">
	       <span class="first-child">
	          <button type="button" title="${msg("btn.columns")}">${msg("btn.columns")}</button>
	       </span>
	    </span>
    </div>
    <div id="${id}-columns-dialog" class="yui-panel filters-dialog">
        <div id="${id}-columns-dialog" class="bd">
            <div id="${id}-columns-dialog-content" class="filters-dialog-content"></div>
            <div class="bdft">
	            <span id="${id}-columns-apply-button" class="yui-button yui-push-button filters-icon">
	                <span class="first-child">
	                    <button type="button" tabindex="0">${msg("columns.button.ok")}</button>
	                </span>
	            </span>
				<span id="${id}-columns-cancel-button" class="yui-button yui-push-button filters-icon">
	                <span class="first-child">
	                    <button type="button" tabindex="0">${msg("columns.button.cancel")}</button>
	                </span>
	            </span>
            </div>
        </div>
    </div>
</div>
<div class="group-actions">
    <div class="actions">
        <span id="${id}-exportButton" class="yui-button yui-push-button">
           <span class="first-child">
              <button type="button">${msg("columns.button.export")}</button>
           </span>
        </span>
    </div>
</div>

</@comp.baseToolbar>
