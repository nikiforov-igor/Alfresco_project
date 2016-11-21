<#import "/ru/it/lecm/base-share/components/lecm-datagrid.ftl" as grid/>

<#assign id = args.htmlid + "-route-datagrid"/>
<#assign showViewForm = false/>

<script type="text/javascript">
(function() {

    (function(){
        function createPage() {
            var datagrid = new LogicECM.module.Routes.DataGrid("${id}");
            datagrid.setOptions({
                usePagination: true,
                disableDynamicPagination: true,
                showExtendSearchBlock: true,
                showCheckboxColumn: false,
                overrideSortingWith: false,
                bubblingLabel: LogicECM.module.Routes.Const.ROUTES_DATAGRID_LABEL,
                excludeColumns: ['lecmApproveAspects:approvalState'],
                datagridMeta:{
                    useFilterByOrg: false,
                    itemType: LogicECM.module.Routes.Const.ROUTES_CONTAINER.routeType,
                    nodeRef: LogicECM.module.Routes.Const.ROUTES_CONTAINER.nodeRef,
                    searchConfig: {
                        filter: '-ASPECT:"sys:temporary" AND -ASPECT:"lecm-workflow:temp"'
                    },
                    actionsConfig: {
                        fullDelete: true,
                        trash: false
                    }
                },
                actions: [{
                    type:"datagrid-action-link-" + LogicECM.module.Routes.Const.ROUTES_DATAGRID_LABEL,
                    id:"onActionEdit",
                    permission:"edit",
                    label:"${msg('actions.edit')}",
                    evaluator: function() {
                        return LogicECM.module.Routes.isEngineer;
                    }
                }, {
                    type:"datagrid-action-link-" + LogicECM.module.Routes.Const.ROUTES_DATAGRID_LABEL,
                    id:"onActionDelete",
                    permission:"delete",
                    label:"${msg('actions.delete-row')}",
                    evaluator: function() {
                        return LogicECM.module.Routes.isEngineer;
                    }
                }]
            });

            datagrid.setMessages(${messages});
            datagrid.draw();

        }

        function init() {
            LogicECM.module.Base.Util.loadResources([
                'scripts/lecm-base/components/advsearch.js',
                'scripts/lecm-base/components/lecm-datagrid.js',
                'scripts/lecm-workflow/routes/routes-datagrid.js',
                'scripts/lecm-workflow/routes/evaluators.js'
            ], [], createPage);
        }

        YAHOO.util.Event.onDOMReady(init);
    })();

})();
</script>

<@grid.datagrid id showViewForm/>
<div class="clear"></div>
