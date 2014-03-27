<#import "/ru/it/lecm/base-share/components/lecm-datagrid.ftl" as grid/>

<#assign id = args.htmlid/>
<#assign showViewForm = true/>

<script type="text/javascript">//<![CDATA[

var datagrid = new LogicECM.module.WCalendar.Absence.DataGrid("${id}");
datagrid.setOptions({
	usePagination:true,
	disableDynamicPagination:true,
	showExtendSearchBlock: false,
	showCheckboxColumn: false,
    bubblingLabel: LogicECM.module.WCalendar.Absence.ABSENCE_PROFILE_LABEL,
	dataSource: "/lecm/wcalendar/absence/get/list/profile",
	actions: [
       {
		type:"datagrid-action-link-${bubblingLabel!"absenceProfileDatagrid"}",
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
			itemType: LogicECM.module.WCalendar.Absence.ABSENCE_CONTAINER.itemType,
			nodeRef: LogicECM.module.WCalendar.Absence.ABSENCE_CONTAINER.nodeRef,
			datagridFormId: "absenceProfileDatagrid"
			//searchConfig: {
			//	filter: "ISNOTNULL:\"sys:node-uuid\" AND NOT (@lecm\\-d8n:delegation\\-opts\\-status:\"NOT_SET\")"
			//}
		},
		bubblingLabel: LogicECM.module.WCalendar.Absence.ABSENCE_PROFILE_LABEL
	});
});
//]]>
</script>

<@grid.datagrid id showViewForm/>
