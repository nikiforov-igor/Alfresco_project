<#import "/ru/it/lecm/base-share/components/lecm-datagrid.ftl" as grid/>

<#assign id = args.htmlid/>

<script type="text/javascript">//<![CDATA[

var wDaysDatagrid = new LogicECM.module.WCalendar.Calendar.WorkingDays.DataGrid("${id}");
wDaysDatagrid.setOptions({
	usePagination:true,
	showExtendSearchBlock:false,
	showCheckboxColumn: false,
    bubblingLabel: "wcalendarWorkingDays",
	actions: [{
		type:"action-link-${bubblingLabel!"wcalendarWorkingDays"}",
		id:"onActionEdit",
		permission:"edit",
		label:"${msg("actions.edit")}"
		},
        {
		type:"action-link-${bubblingLabel!"wcalendarWorkingDays"}",
		id:"onActionDelete",
		permission:"delete",
		label:"${msg("actions.delete-row")}"
        }
	]
});
wDaysDatagrid.setMessages (${messages});

YAHOO.util.Event.onContentReady ('${id}', function () {
	YAHOO.Bubbling.fire ("activeGridChanged", {
        datagridMeta: {
            itemType: "lecm-cal:working-days",
            nodeRef: "NOT_LOAD",
            actionsConfig: {
                fullDelete:true,
                targetDelete:true
            }
        },
    bubblingLabel: "wcalendarWorkingDays"
    });
});
//]]>
</script>

<h2>${msg("label.working-days-section.title")}</h2>

<@grid.datagrid id false>
</@grid.datagrid>
