<#import "/ru/it/lecm/base-share/components/base-components.ftl" as comp/>

<#assign toolbarId = args.htmlid/>

<#assign pageId = page.id/>

<script type="text/javascript">
    (function(){
        function createPage() {
            LogicECM.module.Routes.isEngineer = ${isEngineer?string};

            var routesToolbar = new LogicECM.module.Routes.Toolbar("${toolbarId}");
            routesToolbar.setMessages(${messages});
            routesToolbar.setOptions ({
                pageId: "${pageId}",
                inEngineer: LogicECM.module.Routes.isEngineer,
                datagridBubblingLabel: LogicECM.module.Routes.Const.ROUTES_DATAGRID_LABEL,
            });
        }

        function init() {
            LogicECM.module.Base.Util.loadResources([
                'scripts/lecm-workflow/routes/routes-toolbar.js',
                'components/form/form.js',
                'modules/simple-dialog.js'
            ], [
                'css/lecm-base/components/base-menu/base-menu.css',
                'css/lecm-workflow/routes.css'
            ], createPage);
        }

        YAHOO.util.Event.onDOMReady(init);
    })();

</script>

<@comp.baseToolbar toolbarId true false false>
	<#if isEngineer>
		<div id="${toolbarId}-btnCreateNewRoute"></div>
	</#if>
</@comp.baseToolbar>
