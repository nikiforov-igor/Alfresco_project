<@link rel="stylesheet" type="text/css" href="${url.context}/res/css/lecm-base/components/base-menu/base-menu.css" />
<@link rel="stylesheet" type="text/css" href="${url.context}/res/css/lecm-subscriptions/subscription-menu.css"/>
<@script type="text/javascript" src="${url.context}/res/scripts/lecm-subscriptions/subscriptions-menu.js"></@script>

<#assign id = args.htmlid,
selected = args.selected/>
<#import "/ru/it/lecm/base-share/components/base-components.ftl" as comp/>
<@comp.baseMenu>
	<@comp.baseMenuButton "type" msg('lecm.subscriptions.type.btn') selected/>
	<@comp.baseMenuButton "object" msg('lecm.subscriptions.object.btn') selected/>
</@comp.baseMenu>

<script type="text/javascript">//<![CDATA[

(function() {
    function init() {
        var menu = new LogicECM.module.Subscriptions.Menu("menu-buttons");
        menu.setMessages(${messages});
        menu.draw();
    }
    YAHOO.util.Event.onDOMReady(init);
})();
//]]></script>
