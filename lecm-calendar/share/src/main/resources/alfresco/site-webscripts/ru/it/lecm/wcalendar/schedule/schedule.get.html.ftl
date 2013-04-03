<#import "/ru/it/lecm/base-share/components/lecm-datagrid.ftl" as grid/>

<#assign id = args.htmlid/>
<#assign showViewForm = true/>

<script type="text/javascript">//<![CDATA[

var datagrid = new window.LogicECM.module.WCalendar.Schedule.DataGrid("${id}");
datagrid.setOptions({
	usePagination:true,
	showExtendSearchBlock: false,
	showCheckboxColumn: false,
    bubblingLabel: LogicECM.module.WCalendar.Schedule.SCHEDULE_LABEL,
	dataSource: "/lecm/wcalendar/schedule/get/list",
	actions: [
       {
		type:"datagrid-action-link-${bubblingLabel!"scheduleDatagrid"}",
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
			itemType: LogicECM.module.WCalendar.Schedule.SCHEDULE_CONTAINER.itemType,
			nodeRef: LogicECM.module.WCalendar.Schedule.SCHEDULE_CONTAINER.nodeRef
			//searchConfig: {
			//	filter: "ISNOTNULL:\"sys:node-uuid\" AND NOT (@lecm\\-d8n:delegation\\-opts\\-status:\"NOT_SET\")"
			//}
		},
		bubblingLabel: LogicECM.module.WCalendar.Schedule.SCHEDULE_LABEL
	});
});
//]]>
</script>

<@grid.datagrid id showViewForm/>
