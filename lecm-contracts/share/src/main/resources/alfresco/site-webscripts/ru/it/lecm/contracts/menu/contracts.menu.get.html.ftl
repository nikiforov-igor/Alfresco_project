<#if hasPermission>
    <#assign id = args.htmlid,
    selected = args.selected/>

    <#import "/ru/it/lecm/base-share/components/base-components.ftl" as comp/>
    <@comp.baseMenu>
        <@comp.baseMenuButton "main" msg('lecm.contracts.main.btn') selected true />
        <@comp.baseMenuButton "list" msg('lecm.contracts.list.btn') selected true />
        <@comp.baseMenuButton "documents" msg('lecm.contracts.documents.btn') selected true true />
        <@comp.baseMenuButton "reports" msg('lecm.contracts.reports.btn') selected true />
    </@comp.baseMenu>

<script type="text/javascript">//<![CDATA[

(function () {
    function init() {
        var menu = new LogicECM.module.Contracts.Menu("menu-buttons");
        menu.setMessages(${messages});

        YAHOO.util.Dom.addClass("menu-buttons", "contracts-menu-buttons");
    }
    YAHOO.util.Event.onDOMReady(init);
})();
//]]></script>

</#if>