<#include "/org/alfresco/components/component.head.inc">
<@script type="text/javascript" src="${page.url.context}/scripts/lecm-arm/arm-documents-reports.js"></@script>

<#assign id = args.htmlid>

<script type="text/javascript">//<![CDATA[
(function(){
	function init() {
	    new LogicECM.module.ARM.DocumentsReports("${id}").setMessages(${messages});
	}
	YAHOO.util.Event.onContentReady("${id}", init);
})();
//]]></script>

<div id="${id}">
	Здесь будут отчёты
</div>
