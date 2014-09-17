<#import "/ru/it/lecm/base-share/components/lecm-datagrid.ftl" as grid/>

<#assign id = args.htmlid/>


<@grid.datagrid id false>
<script type="text/javascript">//<![CDATA[
(function() {

    function createDatagrid() {
        var datagrid = new LogicECM.module.WCalendar.Calendar.Years.DataGrid("${id}");
        datagrid.setOptions({
            usePagination:true,
            showExtendSearchBlock:true,
            showCheckboxColumn: false,
            bubblingLabel: LogicECM.module.WCalendar.Calendar.YEARS_LABEL,
            datagridMeta:{
                itemType: LogicECM.module.WCalendar.Calendar.CALENDAR_CONTAINER.itemType,
                nodeRef: LogicECM.module.WCalendar.Calendar.CALENDAR_CONTAINER.nodeRef
                //searchConfig: {
                //	filter: "ISNOTNULL:\"sys:node-uuid\" AND NOT (@lecm\\-d8n:delegation\\-opts\\-status:\"NOT_SET\")"
                //}
            },
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
        datagrid.draw();
    }

    function init() {
        LogicECM.module.Base.Util.loadResources([
            'jquery/jquery-1.6.2.js',
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
