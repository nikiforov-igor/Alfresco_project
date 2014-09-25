<#import "/ru/it/lecm/base-share/components/base-components.ftl" as comp/>

<#assign toolbarId = args.htmlid/>

<#assign pageId = page.id/>

<script type="text/javascript"> //<![CDATA[
LogicECM.module = LogicECM.module || {};
LogicECM.module.WCalendar = LogicECM.module.WCalendar || {};
LogicECM.module.WCalendar.Schedule = LogicECM.module.WCalendar.Schedule || {};
(function () {

    function createToolbar() {
        var wcalendarToolbar = new LogicECM.module.WCalendar.Schedule.Toolbar("${toolbarId}");
        wcalendarToolbar.setMessages(${messages});
        wcalendarToolbar.setOptions ({
            pageId: "${pageId}",
            bubblingLabel: LogicECM.module.WCalendar.Schedule.SCHEDULE_LABEL
        });
    }

    function init() {
        LogicECM.module.Base.Util.loadResources([
            'jquery/jquery-1.6.2.js',
            'modules/simple-dialog.js',
            'scripts/lecm-base/components/advsearch.js',
            'scripts/lecm-base/components/lecm-datagrid.js',
            'scripts/lecm-calendar/schedule/schedule-datagrid.js',
            'scripts/lecm-base/components/lecm-toolbar.js',
            'scripts/lecm-calendar/schedule/schedule-toolbar.js',
            'scripts/lecm-calendar/schedule/schedule-limit-validation.js',
            'scripts/lecm-calendar/schedule/reiteration-rules-validation.js',
            'scripts/lecm-calendar/schedule/time-validation.js',
            'scripts/lecm-calendar/schedule/jquery-ui-1.10.3.custom.js',
            'scripts/lecm-calendar/schedule/jquery-ui-timepicker-addon.js',
            'scripts/lecm-calendar/schedule/jquery-ui-sliderAccess.js'
        ], [
            'components/data-lists/toolbar.css',
            'css/lecm-calendar/wcalendar-toolbar.css',
            'css/lecm-base/components/base-menu/base-menu.css',
            'css/lecm-calendar/reiteration-control.css',
            'css/lecm-calendar/jquery-ui-1.10.3.custom.css',
            'css/lecm-calendar/jquery-ui-timepicker-addon.css'
        ], createToolbar);
    }

    YAHOO.util.Event.onDOMReady(init);
})();
//]]>
</script>

<@comp.baseToolbar toolbarId true false false>
	<div id="${toolbarId}-btnCreateNewCommonSchedule"></div>
	<div id="${toolbarId}-btnCreateNewSpecialSchedule"></div>
	<#--<div id="${toolbarId}-btnCreateNewLink"></div>-->
</@comp.baseToolbar>

