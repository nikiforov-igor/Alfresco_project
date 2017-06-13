<#import "/ru/it/lecm/base-share/components/lecm-datagrid.ftl" as grid/>

<#assign id = args.htmlid/>
<#assign showViewForm = false/>

<script type="text/javascript">//<![CDATA[
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

    function createDatagrid() {
        var datagrid = new LogicECM.module.WCalendar.Schedule.DataGrid("${id}");
        datagrid.setOptions({
            usePagination:true,
        disableDynamicPagination: true,
            showExtendSearchBlock: false,
            showCheckboxColumn: false,
            bubblingLabel: LogicECM.module.WCalendar.Schedule.SCHEDULE_LABEL,
            dataSource: "/lecm/wcalendar/schedule/get/list",
            datagridMeta: {
                useFilterByOrg: false,
                itemType: LogicECM.module.WCalendar.Schedule.SCHEDULE_CONTAINER.itemType,
                nodeRef: LogicECM.module.WCalendar.Schedule.SCHEDULE_CONTAINER.nodeRef
                //searchConfig: {
                //	filter: "ISNOTNULL:\"sys:node-uuid\" AND NOT (@lecm\\-d8n:delegation\\-opts\\-status:\"NOT_SET\")"
                //}
            },
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
        datagrid.draw()
    }

    function init() {
        LogicECM.module.Base.Util.loadResources([
            'modules/simple-dialog.js',
            'scripts/lecm-base/components/advsearch.js',
            'scripts/lecm-base/components/lecm-datagrid.js',
            'scripts/lecm-calendar/schedule/schedule-datagrid.js',
            'scripts/lecm-base/components/lecm-toolbar.js',
            'scripts/lecm-calendar/schedule/schedule-toolbar.js',
            'scripts/lecm-calendar/schedule/schedule-limit-validation.js',
            'scripts/lecm-calendar/schedule/time-validation.js'
        ], [
            'components/data-lists/toolbar.css',
            'css/lecm-calendar/wcalendar-toolbar.css',
            'css/lecm-base/components/base-menu/base-menu.css',
            'css/lecm-calendar/reiteration-control.css'
        ], createDatagrid);
    }

    YAHOO.util.Event.onDOMReady(init);
})();
//]]></script>

<@grid.datagrid id showViewForm/>
