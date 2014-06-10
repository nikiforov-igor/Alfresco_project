<@link rel="stylesheet" type="text/css" href="${url.context}/res/css/lecm-base/components/base-menu/base-menu.css" />
<@script type="text/javascript" src="${url.context}/res/scripts/lecm-business-journal/business-journal-menu.js"></@script>

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
	<@comp.baseMenuButton "bj-archiver-settings" msg('lecm.business-journal.archive-settings.btn') args.selected/>
	<@comp.baseMenuButton "bj-logger-settings" msg('lecm.business-journal.logger-settings.btn') args.selected/>
</@comp.baseMenu>