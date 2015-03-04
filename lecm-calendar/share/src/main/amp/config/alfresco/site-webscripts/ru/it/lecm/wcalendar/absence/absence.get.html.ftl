<#import "/ru/it/lecm/base-share/components/lecm-datagrid.ftl" as grid/>

<#assign id = args.htmlid/>
<#assign showViewForm = false/>

<script type="text/javascript">//<![CDATA[
    var datagrid = null;
    function createGrid() {
        datagrid = new LogicECM.module.WCalendar.Absence.DataGrid("${id}");
        datagrid.setOptions({
            usePagination:true,
            disableDynamicPagination:true,
            showExtendSearchBlock: false,
            showCheckboxColumn: false,
            bubblingLabel: LogicECM.module.WCalendar.Absence.ABSENCE_LABEL,
            dataSource: "/lecm/wcalendar/absence/get/list/admin",
            datagridMeta: {
                useFilterByOrg: false,
                itemType: LogicECM.module.WCalendar.Absence.ABSENCE_CONTAINER.itemType,
                nodeRef: LogicECM.module.WCalendar.Absence.ABSENCE_CONTAINER.nodeRef,
                datagridFormId: "absenceAdminDatagrid",
                searchConfig: {
                    filter: ""
                }
            },
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
        datagrid.draw();
    }

    function init() {
        LogicECM.module.Base.Util.loadResources([
            'modules/simple-dialog.js',
            'scripts/lecm-base/components/advsearch.js',
            'scripts/lecm-base/components/lecm-datagrid.js',
            'scripts/lecm-calendar/absence/absence-datagrid.js',
            'scripts/lecm-calendar/absence/absence-toolbar.js',
            'scripts/lecm-calendar/absence/date-interval-validation.js'
        ], [
            'css/lecm-base/components/base-menu/base-menu.css',
            'css/lecm-calendar/absence-summary-table.css',
            'css/lecm-calendar/wcalendar-absence.css'
        ], createGrid);
    }

    YAHOO.util.Event.onDOMReady(init);

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
	<label class="checkbox" for="cbShowOnlyActive">${msg("lecm.calendar.absence.show.active")}</label>
</div>
<@grid.datagrid id showViewForm/>
