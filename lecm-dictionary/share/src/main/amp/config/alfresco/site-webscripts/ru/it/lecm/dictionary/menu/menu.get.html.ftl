<#include "/org/alfresco/components/form/form.dependencies.inc">
<@link rel="stylesheet" type="text/css" href="${url.context}/res/css/lecm-base/components/base-menu/base-menu.css" />
<@script type="text/javascript" src="${url.context}/res/scripts/lecm-dictionary/dictionary-menu.js"></@script>

<#assign id = args.htmlid>
<#import "/ru/it/lecm/base-share/components/base-components.ftl" as comp/>

<script type="text/javascript">//<![CDATA[

(function() {
	function init() {
		var menu = new LogicECM.module.Dictionary.Menu("menu-buttons").setMessages(${messages});
	}
	YAHOO.util.Event.onDOMReady(init);
})();
//]]></script>

<@comp.baseMenu>
    <@comp.baseMenuButton "dictionaries" msg('lecm.dictionaries.btn') args.selected/>
</@comp.baseMenu>