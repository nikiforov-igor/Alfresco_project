<#assign id = args.htmlid>

<#assign searchBlock = true/>
<#assign exSearch = false/>
<#assign buttons = false/>

<#import "/ru/it/lecm/base-share/components/base-components.ftl" as comp/>

<script type="text/javascript">//<![CDATA[
function init() {
    new LogicECM.module.DocumentsJournal.Toolbar("${id}").setMessages(${messages}).setOptions({
	    bubblingLabel: "documents-journal"
    });
}
YAHOO.util.Event.onDOMReady(init);
//]]></script>
<@comp.baseToolbar id buttons searchBlock exSearch>
</@comp.baseToolbar>