<#assign id = args.htmlid,
selected = args.selected/>

<#import "/ru/it/lecm/base-share/components/base-components.ftl" as comp/>
<@comp.baseMenu>
    <@comp.baseMenuButton "list" msg('lecm.documents.list.btn') selected true true />
    <@comp.baseMenuButton "reports" msg('lecm.documents.reports.btn') selected true />
    <@comp.baseMenuButton "archive" msg('lecm.documents.archive.btn') selected true true />
</@comp.baseMenu>

<script type="text/javascript">//<![CDATA[

(function () {
    function init() {
        var menu = new LogicECM.module.Documents.Menu("menu-buttons");
        menu.setMessages(${messages});
        menu.setDocType("${args.itemType}");
        YAHOO.util.Dom.addClass("menu-buttons", "documents-menu-buttons");
    }

    YAHOO.util.Event.onDOMReady(init);
})();
//]]></script>

