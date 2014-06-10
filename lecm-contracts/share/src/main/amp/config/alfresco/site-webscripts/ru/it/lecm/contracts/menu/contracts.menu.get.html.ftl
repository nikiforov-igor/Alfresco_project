<@link rel="stylesheet" type="text/css" href="${url.context}/res/css/lecm-base/components/base-menu/base-menu.css" />
<@link rel="stylesheet" type="text/css" href="${url.context}/res/css/lecm-contracts/contracts-menu.css"/>
<@script type="text/javascript" src="${url.context}/res/scripts/lecm-contracts/contracts-menu.js"></@script>

    <#assign id = args.htmlid,
    selected = args.selected/>

    <#import "/ru/it/lecm/base-share/components/base-components.ftl" as comp/>
    <@comp.baseMenu>
        <@comp.baseMenuButton "main" msg('lecm.contracts.main.btn') selected true />
        <@comp.baseMenuButton "list" msg('lecm.contracts.list.btn') selected true true />
        <@comp.baseMenuButton "documents" msg('lecm.contracts.documents.btn') selected true true />
        <#if hasPermission>
            <@comp.baseMenuButton "reports" msg('lecm.contracts.reports.btn') selected true />
        </#if>
        <div id="contracts-archive-menu"></div>
        <@comp.baseMenuButton "archive" msg('lecm.contracts.archive.btn') selected true true />
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

