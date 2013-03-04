<#import "/ru/it/lecm/base-share/components/lecm-datagrid.ftl" as grid/>

<#assign id = args.htmlid/>

<script type="text/javascript">//<![CDATA[

var datagrid = new LogicECM.module.WCalendar.Shedule.DataGrid("${id}");
datagrid.setOptions({
	usePagination:true,
	showExtendSearchBlock: false,
	showCheckboxColumn: false,
    bubblingLabel: LogicECM.module.WCalendar.Shedule.SHEDULE_LABEL,
	dataSource: "/lecm/wcalendar/shedule/get/list",
	actions: [
       {
		type:"datagrid-action-link-${bubblingLabel!"sheduleDatagrid"}",
		id:"onActionDelete",
		permission:"delete",
		label:"${msg("actions.delete-row")}"
        }
	]
});
datagrid.setMessages(${messages});
YAHOO.util.Event.onContentReady('${id}', function () {
	YAHOO.Bubbling.fire ("activeGridChanged", {
		datagridMeta:{
			itemType: LogicECM.module.WCalendar.Shedule.SHEDULE_CONTAINER.itemType,
			nodeRef: LogicECM.module.WCalendar.Shedule.SHEDULE_CONTAINER.nodeRef
			//searchConfig: {
			//	filter: "ISNOTNULL:\"sys:node-uuid\" AND NOT (@lecm\\-d8n:delegation\\-opts\\-status:\"NOT_SET\")"
			//}
		},
		bubblingLabel: LogicECM.module.WCalendar.Shedule.SHEDULE_LABEL
	});
});
//]]>
</script>

<#-- Скрипт для всплывающего по клику окна -->
<@grid.viewForm/>
	<@grid.datagrid id false>
</@grid.datagrid>
