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
        var menu = new window.LogicECM.module.Subscriptions.Menu("menu-buttons");
        menu.setMessages(${messages});
        menu.draw();
    }
    YAHOO.util.Event.onDOMReady(init);
})();
//]]></script>
