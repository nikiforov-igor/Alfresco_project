<#assign id = args.htmlid,
selected = args.selected/>

<#import "/ru/it/lecm/base-share/components/base-components.ftl" as comp/>
<@comp.baseMenu>
    <@comp.baseMenuButton "list" msg('lecm.errands.list.btn') selected true />
    <@comp.baseMenuButton "reports" msg('lecm.errands.reports.btn') selected true true />
    <#if isBoss == "true">
        <@comp.baseMenuButton "tasks" msg('lecm.errands.tasks.btn') selected true true />
    </#if>
    <@comp.baseMenuButton "archive" msg('lecm.errands.archive.btn') selected true true />
</@comp.baseMenu>

<script type="text/javascript">//<![CDATA[

(function () {
    function init() {
        var menu = new LogicECM.module.Errands.Menu("menu-buttons");
        menu.setMessages(${messages});

        YAHOO.util.Dom.addClass("menu-buttons", "errands-menu-buttons");
    }

    YAHOO.util.Event.onDOMReady(init);
})();
//]]></script>

