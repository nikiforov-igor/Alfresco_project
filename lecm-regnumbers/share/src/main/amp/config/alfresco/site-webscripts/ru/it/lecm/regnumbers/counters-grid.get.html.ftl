<#import "/ru/it/lecm/base-share/components/lecm-datagrid.ftl" as grid/>

<#assign id = args.htmlid + "-counters-datagrid"/>
<#assign showViewForm = false/>

<script type="text/javascript">
(function() {

    (function(){
        function createPage() {
            // Ищем оба типа счетчиков: сквозной и годовой.
            var itemTypes = LogicECM.module.Counters.Const.COUNTERS_CONTAINER.yearCounterType + "," + LogicECM.module.Counters.Const.COUNTERS_CONTAINER.plainCounterType;
            var datagrid = new LogicECM.module.Counters.DataGrid("${id}");
            datagrid.setOptions({
                usePagination: false,
                disableDynamicPagination: true,
                showExtendSearchBlock: false,
                showCheckboxColumn: false,
                overrideSortingWith: false,
                bubblingLabel: LogicECM.module.Counters.Const.COUNTERS_DATAGRID_LABEL,
                datagridMeta:{
                    useFilterByOrg: false,
                    itemType: itemTypes,
                    datagridFormId: 'year-counter-datagrid',
                    nodeRef: LogicECM.module.Counters.Const.COUNTERS_CONTAINER.nodeRef,
                    searchConfig: {
                        filter: '-ASPECT:"sys:temporary" AND -ASPECT:"lecm-workflow:temp"'
                    },
                    actionsConfig: {
                        fullDelete: true,
                        trash: false
                    }
                },
                actions: [{
                    type:"datagrid-action-link-" + LogicECM.module.Counters.Const.COUNTERS_DATAGRID_LABEL,
                    id:"onActionEdit",
                    permission:"edit",
                    label:"${msg('actions.edit')}",
                    evaluator: function() {
                        return LogicECM.module.Counters.isEngineer;
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
                'scripts/lecm-regnumbers/counters-datagrid.js',
            ], ['css/lecm-regnumbers/documents-counters-reset.css'], createPage);
        }

        YAHOO.util.Event.onDOMReady(init);
    })();

})();
</script>

<div id="counters-reset-title" class="documents-counters-reset">
    <div class="yui-g">
        <div class="yui-u first">
            <div class="title">${msg("label.regnumbers.documents.counters.reset.title")}</div>
        </div>
    </div>
</div>

<@grid.datagrid id showViewForm/>
<div class="clear"></div>
