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
<#assign buttonsCreate = true/>
<#if showButtonsCreate??>
	<#if showButtonsCreate == false >
		<#assign buttonsCreate = false/>
	</#if>
</#if>

<#import "/ru/it/lecm/base-share/components/base-components.ftl" as comp/>

<script type="text/javascript">//<![CDATA[
function init() {
    new LogicECM.module.DocumentsJournal.Toolbar("${id}").setMessages(${messages}).setOptions({
	    bubblingLabel: "${bubblingLabel!''}",
        searchActive: true
    });
}
YAHOO.util.Event.onDOMReady(init);
//]]></script>
<@comp.baseToolbar id true true true>
</@comp.baseToolbar>