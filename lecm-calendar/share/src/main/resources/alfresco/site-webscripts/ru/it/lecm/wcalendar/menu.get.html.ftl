<#import "/ru/it/lecm/base-share/components/base-components.ftl" as comp/>

<script type="text/javascript"> //<![CDATA[
	(function () {
		var calendarMenu = new LogicECM.module.WCalendar.Calendar.Menu("menu-buttons");
		calendarMenu.setMessages(${messages});
	})();
//]]>
</script>

<@comp.baseMenu>
    <#-- TODO: скрывать некоторые кнопки, если нет прав -->
	<@comp.baseMenuButton "wcalendarCalendar" "Производственный календарь" args.selected/>
	<@comp.baseMenuButton "wcalendarShedule" "График работы" args.selected/>
    <@comp.baseMenuButton "wcalendarAbsence" "Отсутствие" args.selected/>
</@comp.baseMenu>

