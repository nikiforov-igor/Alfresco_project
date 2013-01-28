<#import "/ru/it/lecm/base-share/components/base-components.ftl" as comp/>

<#assign pageId = page.id/>

<script type="text/javascript"> //<![CDATA[
	(function () {
		var wCalendarMenu = new LogicECM.module.WCalendar.Menu("menu-buttons");
		wCalendarMenu.setMessages(${messages});
		wCalendarMenu.setOptions ({
			pageId: "${pageId}"
		});
	})();
//]]>
</script>

<@comp.baseMenu>
	<@comp.baseMenuButton "wcalendarCalendar"  msg('label.wcalendar.menu.calendar.btn') />
	<@comp.baseMenuButton "wcalendarShedule" msg('label.wcalendar.menu.shedule.btn') />
    <@comp.baseMenuButton "wcalendarAbsence" msg('label.wcalendar.menu.absence.btn') />
</@comp.baseMenu>

