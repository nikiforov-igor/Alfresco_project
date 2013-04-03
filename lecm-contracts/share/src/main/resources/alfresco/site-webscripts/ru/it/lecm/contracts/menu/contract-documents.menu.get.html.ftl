<#assign id = args.htmlid,
selected = args.selected/>
<#import "/ru/it/lecm/base-share/components/base-components.ftl" as comp/>
<@comp.baseMenu>
	<@comp.baseMenuButton "contracts" msg('lecm.contracts.contracts.btn') selected/>
</@comp.baseMenu>

<script type="text/javascript">//<![CDATA[

(function() {
    function init() {
        var menu = new window.LogicECM.module.Contracts.Menu("menu-buttons");
        menu.setMessages(${messages});
        menu.draw();
    }
    YAHOO.util.Event.onDOMReady(init);
})();
//]]></script>
