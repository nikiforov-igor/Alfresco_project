<@link rel="stylesheet" type="text/css" href="${url.context}/res/components/data-lists/toolbar.css" />
<@script type="text/javascript" src="${url.context}/res/scripts/lecm-base/components/lecm-toolbar.js"/>
<@script type="text/javascript" src="${url.context}/res/scripts/grids/documents-journal-toolbar.js"></@script>

<#assign id = args.htmlid>

<#assign searchBlock = true/>
<#assign exSearch = false/>
<#assign buttons = false/>

<#import "/ru/it/lecm/base-share/components/base-components.ftl" as comp/>

<script type="text/javascript">//<![CDATA[
(function(){
	function init() {
	    new LogicECM.module.DocumentsJournal.Toolbar("${id}").setMessages(${messages}).setOptions({
		    bubblingLabel: "documents-journal"
	    });
	}
	YAHOO.util.Event.onDOMReady(init);
})();
//]]></script>
<@comp.baseToolbar id buttons searchBlock exSearch>
</@comp.baseToolbar>