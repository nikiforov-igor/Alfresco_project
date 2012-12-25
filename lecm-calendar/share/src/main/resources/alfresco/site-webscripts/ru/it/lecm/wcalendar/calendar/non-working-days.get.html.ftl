<#import "/ru/it/lecm/base-share/components/lecm-datagrid.ftl" as grid/>

<#assign id = args.htmlid/>

<script type="text/javascript">//<![CDATA[

var nwDaysDatagrid = new LogicECM.module.WCalendar.Calendar.NonWorkingDays.DataGrid("${id}");
nwDaysDatagrid.setOptions({
	usePagination:true,
	showExtendSearchBlock:false,
	showCheckboxColumn: false,
    bubblingLabel: "wcalendarNonWorkingDays",
	actions: [{
		type:"action-link-${bubblingLabel!"wcalendarNonWorkingDays"}",
		id:"onActionEdit",
		permission:"edit",
		label:"${msg("actions.edit")}"
		},
        {
		type:"action-link-${bubblingLabel!"wcalendarNonWorkingDays"}",
		id:"onActionDelete",
		permission:"delete",
		label:"${msg("actions.delete-row")}"
        }
	]
});
nwDaysDatagrid.setMessages (${messages});

YAHOO.util.Event.onContentReady ('${id}', function () {
	YAHOO.Bubbling.fire ("activeGridChanged", {
        datagridMeta: {
            itemType: "lecm-cal:non-working-days",
            nodeRef: "NOT_LOAD",
            actionsConfig: {
                fullDelete:true,
                targetDelete:true
            }
        },
    bubblingLabel: "wcalendarNonWorkingDays"
    });
});
//]]>
</script>

<h2>${msg("label.non-working-days-section.title")}</h2>

<@grid.datagrid id false>
</@grid.datagrid>
