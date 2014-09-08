<#import "/ru/it/lecm/base-share/components/base-components.ftl" as comp/>

<#assign toolbarId = args.htmlid/>

<#assign pageId = page.id/>

<script type="text/javascript"> //<![CDATA[
	(function () {

        function createToolbar() {
            var wcalendarToolbar = new LogicECM.module.WCalendar.Absence.Toolbar("${toolbarId}");
            wcalendarToolbar.setMessages(${messages});
            wcalendarToolbar.setOptions ({
                pageId: "${pageId}"
            });
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
                'scripts/lecm-calendar/menu.js',
                'scripts/lecm-calendar/calendar/date-existence-validation.js'
            ], [
                'css/lecm-calendar/wcalendar-calendar.css',
                'css/lecm-base/components/base-menu/base-menu.css',
                'css/lecm-calendar/wcalendar-menu.css',
                'components/data-lists/toolbar.css',
                'css/lecm-calendar/wcalendar-toolbar.css'
            ], createToolbar);
        }

        YAHOO.util.Event.onDOMReady(init);
	})();
//]]>
</script>

<@comp.baseToolbar toolbarId true false false>
	<div id="${toolbarId}-btnCreateNewEmployeeAbsence"></div>
</@comp.baseToolbar>
