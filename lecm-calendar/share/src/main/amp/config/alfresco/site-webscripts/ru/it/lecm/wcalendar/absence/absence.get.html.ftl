<#import "/ru/it/lecm/base-share/components/lecm-datagrid.ftl" as grid/>

<@markup id="css">
	<@link rel="stylesheet" type="text/css" href="${url.context}/res/css/lecm-calendar/wcalendar-absence.css" />
</@>

<#assign id = args.htmlid/>
<#assign showViewForm = true/>

<script type="text/javascript">//<![CDATA[

var datagrid = new LogicECM.module.WCalendar.Absence.DataGrid("${id}");
datagrid.setOptions({
	usePagination:true,
    disableDynamicPagination:true,
	showExtendSearchBlock: false,
	showCheckboxColumn: false,
    bubblingLabel: LogicECM.module.WCalendar.Absence.ABSENCE_LABEL,
	dataSource: "/lecm/wcalendar/absence/get/list/admin",
	actions: [
       {
		type:"datagrid-action-link-${bubblingLabel!"absenceDatagrid"}",
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
			datagridFormId: "absenceAdminDatagrid",
			searchConfig: {
				filter: ""
			}
		},
		bubblingLabel: LogicECM.module.WCalendar.Absence.ABSENCE_LABEL
	});
});

function onShowOnlyActiveChanged() {
	var cbShowOnlyConfigured = YAHOO.util.Dom.get("cbShowOnlyActive");
	var obj = {
		datagridMeta: datagrid.datagridMeta
	};
	if (cbShowOnlyConfigured.checked) {
		obj.datagridMeta.searchConfig.filter = "@lecm\\-absence:activated:true";
	} else {
		obj.datagridMeta.searchConfig.filter = "";
	}
	YAHOO.Bubbling.fire ("activeGridChanged", obj);
};

//]]>
</script>

<div align="right" class="show-only-archive-container">
	<input type="checkbox" class="formsCheckBox" id="cbShowOnlyActive" onChange="onShowOnlyActiveChanged()">
	<label class="checkbox" for="cbShowOnlyActive">Отображать только активные</label>
</div>
<@grid.datagrid id showViewForm/>
