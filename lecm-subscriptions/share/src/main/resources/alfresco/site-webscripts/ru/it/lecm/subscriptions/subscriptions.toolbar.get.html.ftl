<#assign id = args.htmlid>

<#assign searchBlock = true/>
<#if showSearchBlock??>
	<#assign searchBlock = showSearchBlock/>
</#if>
<#assign exSearch = false/>
<#if showExSearchBtn??>
	<#assign exSearch = showExSearchBtn/>
</#if>
<#assign buttons = true/>
<#if showButtons??>
	<#assign buttons = showButtons/>
</#if>

<#import "/ru/it/lecm/base-share/components/base-components.ftl" as comp/>

<script type="text/javascript">//<![CDATA[
function init() {
    new LogicECM.module.Subscriptions.Toolbar("${id}").setMessages(${messages}).setOptions({});
}
YAHOO.util.Event.onDOMReady(init);
//]]></script>
<@comp.baseToolbar id buttons searchBlock exSearch>
<div class="new-row">
        <span id="${id}-newRowButton" class="yui-button yui-push-button">
           <span class="first-child">
              <button type="button" title="${msg("button.new-row")}">${msg("button.new-row")}</button>
           </span>
        </span>
</div>
<div class="delete-row">
        <span id="${id}-deleteButton" class="yui-button yui-push-button">
           <span class="first-child">
              <button type="button" title="${msg("button.delete")}">&nbsp;</button>
           </span>
        </span>
</div>
</@comp.baseToolbar>