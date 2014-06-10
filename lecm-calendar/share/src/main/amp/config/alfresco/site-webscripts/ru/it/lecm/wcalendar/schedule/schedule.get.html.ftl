<#import "/ru/it/lecm/base-share/components/lecm-datagrid.ftl" as grid/>

<#assign id = args.htmlid/>
<#assign showViewForm = true/>

<script type="text/javascript">//<![CDATA[
if (typeof LogicECM == "undefined" || !LogicECM) {
	var LogicECM = {};
}
LogicECM.module = LogicECM.module || {};
LogicECM.module.WCalendar = LogicECM.module.WCalendar || {};
LogicECM.module.WCalendar.Schedule = LogicECM.module.WCalendar.Schedule || {};

(function() {
	function notDefaultSchedule(oData) {
		var rowNodeRef,
			orgNodeRef;
	
		orgNodeRef = LogicECM.module.WCalendar.Schedule.ORGANIZATION_NODE_REF;
		rowNodeRef = oData.itemData["assoc_lecm-sched_sched-employee-link-assoc"].value;
	    if(rowNodeRef == orgNodeRef) {
	    	return false;
	    }
	    return true;
	};
	
	var datagrid = new LogicECM.module.WCalendar.Schedule.DataGrid("${id}");
	datagrid.setOptions({
		usePagination:true,
    disableDynamicPagination: true,
		showExtendSearchBlock: false,
		showCheckboxColumn: false,
	    bubblingLabel: LogicECM.module.WCalendar.Schedule.SCHEDULE_LABEL,
		dataSource: "/lecm/wcalendar/schedule/get/list",
		actions: [
	       {
			type:"datagrid-action-link-${bubblingLabel!"scheduleDatagrid"}",
			id:"onActionDelete",
			permission:"delete",
			label:"${msg("actions.delete-row")}",
			evaluator: notDefaultSchedule
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
})();
//]]></script>

<@grid.datagrid id showViewForm/>
