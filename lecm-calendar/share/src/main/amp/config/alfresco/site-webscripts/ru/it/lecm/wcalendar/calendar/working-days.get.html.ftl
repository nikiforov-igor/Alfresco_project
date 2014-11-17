<#import "/ru/it/lecm/base-share/components/lecm-datagrid.ftl" as grid/>

<#assign id = args.htmlid/>

<h2>${msg("label.working-days-section.title")}</h2>

<@grid.datagrid id false>
<script type="text/javascript">//<![CDATA[
(function() {
    function createDatagrid() {
        var wDaysDatagrid = new LogicECM.module.WCalendar.Calendar.WorkingDays.DataGrid("${id}");
        wDaysDatagrid.setOptions({
            usePagination:true,
            showExtendSearchBlock:false,
            showCheckboxColumn: false,
            bubblingLabel: LogicECM.module.WCalendar.Calendar.WORKING_DAYS_LABEL,
            datagridMeta: {
                itemType: "lecm-cal:working-days",
                nodeRef: "NOT_LOAD",
                actionsConfig: {
                    fullDelete:true,
                    trash:false
                }
            },
            actions: [{
                type:"datagrid-action-link-${bubblingLabel!"wcalendarWorkingDays"}",
                id:"onActionEdit",
                permission:"edit",
                label:"${msg("actions.edit")}"
            },
                {
                    type:"datagrid-action-link-${bubblingLabel!"wcalendarWorkingDays"}",
                    id:"onActionDelete",
                    permission:"delete",
                    label:"${msg("actions.delete-row")}"
                }
            ]
        });
        wDaysDatagrid.setMessages (${messages});
        wDaysDatagrid.draw();

    }

    function init() {
        LogicECM.module.Base.Util.loadResources([
            'modules/simple-dialog.js',
            'scripts/lecm-base/components/advsearch.js',
            'scripts/lecm-base/components/lecm-datagrid.js',
            'scripts/lecm-calendar/calendar/non-working-days-datagrid.js',
            'scripts/lecm-calendar/calendar/working-days-datagrid.js',
            'scripts/lecm-calendar/calendar/years-datagrid.js',
            'scripts/lecm-calendar/calendar/special-days-toolbar.js',
            'scripts/lecm-calendar/calendar/years-toolbar.js',
            'scripts/lecm-calendar/calendar/date-existence-validation.js'
        ], [
            'css/lecm-calendar/wcalendar-calendar.css',
            'css/lecm-base/components/base-menu/base-menu.css',
            'components/data-lists/toolbar.css',
            'css/lecm-calendar/wcalendar-toolbar.css'
        ], createDatagrid);
    }

    YAHOO.util.Event.onDOMReady(init);
})();
//]]>
</script>
</@grid.datagrid>
