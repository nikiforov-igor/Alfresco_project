<#import "/ru/it/lecm/base-share/components/base-components.ftl" as comp/>

<#assign toolbarId = args.htmlid/>

<#assign pageId = page.id/>

<script type="text/javascript"> //<![CDATA[
	(function () {
		var wcalendarToolbar = new window.LogicECM.module.WCalendar.Schedule.Toolbar("${toolbarId}");
		wcalendarToolbar.setMessages(${messages});
		wcalendarToolbar.setOptions ({
			pageId: "${pageId}"
		});
	})();
//]]>
</script>

<@comp.baseToolbar toolbarId true false false>
	<div id="${toolbarId}-btnCreateNewCommonSchedule"></div>
	<div id="${toolbarId}-btnCreateNewSpecialSchedule"></div>
	<#--<div id="${toolbarId}-btnCreateNewLink"></div>-->
</@comp.baseToolbar>

