<#include "/org/alfresco/components/component.head.inc">
<@link rel="stylesheet" type="text/css" href="${url.context}/res/css/lecm-base/components/base-menu/base-menu.css" />
<@script type="text/javascript" src="${url.context}/res/scripts/lecm-arm/arm-menu.js"></@script>

<#assign id = args.htmlid>
<#assign onHomePage = true/>
<#if args.onHome??>
        <#assign onHomePage = (args.onHome == "true")/>
</#if>
<#import "/ru/it/lecm/base-share/components/base-components.ftl" as comp/>

<script type="text/javascript">//<![CDATA[

(function() {
        function init() {
		var menu = new LogicECM.module.ARM.Menu("menu-buttons").setMessages(${messages});
                menu.setOnHomePage(${args.onHome});
	}
	YAHOO.util.Event.onDOMReady(init);
})();
//]]></script>

<@comp.baseMenu>
    <@comp.baseMenuButton "home" msg('lecm.home.btn') args.selected/>
    <@comp.baseMenuButton "filtersDic" msg('lecm.filters.btn') args.selected/>
</@comp.baseMenu>