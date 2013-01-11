<#import "/ru/it/lecm/base-share/components/base-components.ftl" as comp/>

<#assign toolbarId = args.htmlid/>

<#assign pageId = page.id/>

<script type="text/javascript"> //<![CDATA[
	(function () {
		var wcalendarToolbar = new LogicECM.module.WCalendar.Shedule.Toolbar("${toolbarId}");
		wcalendarToolbar.setMessages(${messages});
		wcalendarToolbar.setOptions ({
			pageId: "${pageId}"
		});
	})();
//]]>
</script>

<@comp.baseToolbar toolbarId true false false>
	<div id="${toolbarId}-btnCreateNewCommonShedule"></div>
	<div id="${toolbarId}-btnCreateNewSpecialShedule"></div>
	<#--<div id="${toolbarId}-btnCreateNewLink"></div>-->
</@comp.baseToolbar>

