<#assign id = args.htmlid>
<#import "/ru/it/lecm/base-share/components/base-components.ftl" as comp/>

<script type="text/javascript">//<![CDATA[
(function() {
    function init() {
        var menu = new LogicECM.module.BusinessJournal.Menu("menu-buttons").setMessages(${messages});
    }
    YAHOO.util.Event.onDOMReady(init);
})();
//]]></script>

<@comp.baseMenu>
    <!-- добавить кнопки сюда-->
</@comp.baseMenu>