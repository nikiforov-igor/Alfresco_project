<#import "/ru/it/lecm/base-share/components/lecm-datagrid.ftl" as grid/>

<#assign id = args.htmlid/>
<#assign showViewForm = false/>

<script type="text/javascript">//<![CDATA[
(function () {
	function init() {
		LogicECM.module.Base.Util.loadScripts([
			'/scripts/lecm-base/components/advsearch.js',
			'/scripts/lecm-base/components/lecm-datagrid.js',
			'/scripts/lecm-calendar/absence/absence-datagrid.js',
			'/scripts/lecm-calendar/absence/date-interval-validation.js'
		], createObject);
	}

	function createObject() {
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
					type:"datagrid-action-link-" + LogicECM.module.WCalendar.Absence.ABSENCE_PROFILE_LABEL,
					id:"onActionDelete",
					permission:"delete",
					label:"${msg("actions.delete-row")}"
				}
			],
            datagridMeta:{
                useFilterByOrg: false,
                itemType: LogicECM.module.WCalendar.Absence.ABSENCE_CONTAINER.itemType,
                nodeRef: LogicECM.module.WCalendar.Absence.ABSENCE_CONTAINER.nodeRef,
                datagridFormId: "absenceProfileDatagrid"
                //searchConfig: {
                //	filter: "ISNOTNULL:\"sys:node-uuid\" AND NOT (@lecm\\-d8n:delegation\\-opts\\-status:\"NOT_SET\")"
                //}
            }
		});
		datagrid.setMessages(${messages});
        datagrid.draw();
	}

	YAHOO.util.Event.onDOMReady(init);
})();
//]]></script>

<@grid.datagrid id showViewForm/>
