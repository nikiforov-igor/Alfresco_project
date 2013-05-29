<#assign id = args.htmlid>

<#assign showCreateButton = false/>
<#if showCreateBtn??>
	<#assign showCreateButton = showCreateBtn/>
</#if>

<#assign showSearchControl = true/>
<#if showSearch??>
    <#assign showSearchControl = showSearch/>
</#if>

<#assign exSearch = false/>
<#if showExSearchBtn??>
    <#assign exSearch = showExSearchBtn/>
</#if>

<#import "/ru/it/lecm/base-share/components/base-components.ftl" as comp/>

<script type="text/javascript">//<![CDATA[
function init() {
    new LogicECM.module.Contracts.Toolbar("${id}").setMessages(${messages}).setOptions({
	    bubblingLabel: "${bubblingLabel!'documents'}",
        itemType: "lecm-contract:document",
        destination:LogicECM.module.Contracts.SETTINGS.nodeRef
    });
}
YAHOO.util.Event.onDOMReady(init);
//]]></script>
<@comp.baseToolbar id true showSearchControl exSearch>
<#if showCreateButton>
<div class="new-row">
        <span id="${id}-newContractButton" class="yui-button yui-push-button">
           <span class="first-child">
              <button type="button" title="${msg("button.new-row")}">${msg("button.new-row")}</button>
           </span>
        </span>
</div>
</#if>
</@comp.baseToolbar>