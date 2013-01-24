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
	<@comp.baseMenuButton "bj-summary" msg('lecm.business-journal.records.btn') args.selected/>
	<@comp.baseMenuButton "bj-settings" msg('lecm.business-journal.archive-settings.btn') args.selected/>
</@comp.baseMenu>