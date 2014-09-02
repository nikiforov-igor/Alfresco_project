<#import "/ru/it/lecm/base-share/components/base-components.ftl" as comp/>

<#assign toolbarId = args.htmlid/>

<#assign pageId = page.id/>

<script type="text/javascript">//<![CDATA[
(function () {
	function init() {
		LogicECM.module.Base.Util.loadScripts([
					'/scripts/lecm-calendar/absence/absence-profile-toolbar.js'
				], createObject);
	}

	function createObject() {
		var wcalendarToolbar = new LogicECM.module.WCalendar.Absence.ToolbarProfile("${toolbarId}");
		wcalendarToolbar.setMessages(${messages});
		wcalendarToolbar.setOptions ({
			pageId: "${pageId}"
		});
	}

	YAHOO.util.Event.onDOMReady(init);
})();
//]]></script>

<@comp.baseToolbar toolbarId true false false>
	<div id="${toolbarId}-btnCreateNewMyAbsence"></div>
</@comp.baseToolbar>
