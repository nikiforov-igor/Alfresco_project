<#assign id = args.htmlid,
    selected = args.selected/>
<#import "/ru/it/lecm/base-share/components/base-components.ftl" as comp/>

<@comp.baseMenu>
    <div id="contracts-menu"></div>
    <@comp.baseMenuButton "contracts" msg('lecm.documents.contracts.btn') selected/>
</@comp.baseMenu>

<script type="text/javascript">//<![CDATA[

(function() {
    function init() {
        var menu = new LogicECM.module.DocumentsJournal.Menu("menu-buttons");
        menu.setMessages(${messages});
    }
    YAHOO.util.Event.onDOMReady(init);
})();
//]]></script>
