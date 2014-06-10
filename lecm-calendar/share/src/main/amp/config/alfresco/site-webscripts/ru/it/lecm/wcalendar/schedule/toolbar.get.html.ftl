<#import "/ru/it/lecm/base-share/components/base-components.ftl" as comp/>

<#assign toolbarId = args.htmlid/>

<#assign pageId = page.id/>

<script type="text/javascript"> //<![CDATA[
if (typeof LogicECM == "undefined" || !LogicECM) {
	var LogicECM = {};
}
LogicECM.module = LogicECM.module || {};
LogicECM.module.WCalendar = LogicECM.module.WCalendar || {};
LogicECM.module.WCalendar.Schedule = LogicECM.module.WCalendar.Schedule || {};
(function () {
	var wcalendarToolbar = new LogicECM.module.WCalendar.Schedule.Toolbar("${toolbarId}");
	wcalendarToolbar.setMessages(${messages});
	wcalendarToolbar.setOptions ({
		pageId: "${pageId}",
		bubblingLabel: LogicECM.module.WCalendar.Schedule.SCHEDULE_LABEL
	});
})();
//]]>
</script>

<@comp.baseToolbar toolbarId true false false>
	<div id="${toolbarId}-btnCreateNewCommonSchedule"></div>
	<div id="${toolbarId}-btnCreateNewSpecialSchedule"></div>
	<#--<div id="${toolbarId}-btnCreateNewLink"></div>-->
</@comp.baseToolbar>

