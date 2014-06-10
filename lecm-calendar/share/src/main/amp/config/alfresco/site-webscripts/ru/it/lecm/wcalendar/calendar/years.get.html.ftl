<#import "/ru/it/lecm/base-share/components/lecm-datagrid.ftl" as grid/>

<#assign id = args.htmlid/>

<script type="text/javascript">//<![CDATA[
(function() {
	var datagrid = new LogicECM.module.WCalendar.Calendar.Years.DataGrid("${id}");
	datagrid.setOptions({
		usePagination:true,
		showExtendSearchBlock:true,
		showCheckboxColumn: false,
	    bubblingLabel: LogicECM.module.WCalendar.Calendar.YEARS_LABEL,
		actions: [{
			type:"datagrid-action-link-${bubblingLabel!"wcalendarYears"}",
			id:"onActionEdit",
			permission:"edit",
			label:"${msg("actions.edit")}"
			} <#-- ,
	        {
			type:"datagrid-action-link-${bubblingLabel!"wcalendarYears"}",
			id:"onActionDelete",
			permission:"delete",
			label:"${msg("actions.delete-row")}"
	        } -->
		]
	});
	datagrid.setMessages (${messages});
	YAHOO.util.Event.onContentReady('${id}', function () {
		YAHOO.Bubbling.fire ("activeGridChanged", {
			datagridMeta:{
				itemType: LogicECM.module.WCalendar.Calendar.CALENDAR_CONTAINER.itemType,
				nodeRef: LogicECM.module.WCalendar.Calendar.CALENDAR_CONTAINER.nodeRef
	            //searchConfig: {
				//	filter: "ISNOTNULL:\"sys:node-uuid\" AND NOT (@lecm\\-d8n:delegation\\-opts\\-status:\"NOT_SET\")"
				//}
			},
	        bubblingLabel: LogicECM.module.WCalendar.Calendar.YEARS_LABEL
		});
	});
})();
//]]>
</script>

<@grid.datagrid id false>
</@grid.datagrid>
