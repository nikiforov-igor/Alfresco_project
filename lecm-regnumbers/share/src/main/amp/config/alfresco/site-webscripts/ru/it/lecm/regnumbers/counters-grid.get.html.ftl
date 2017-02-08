<#import "/ru/it/lecm/base-share/components/lecm-datagrid.ftl" as grid/>

<#assign id = args.htmlid + "-counters-datagrid"/>
<#assign showViewForm = false/>

<div id="counters-reset-title" class="documents-counters-reset">
    <div class="yui-g">
        <div class="yui-u first">
            <div class="title">${msg("label.regnumbers.documents.counters.reset.title")}</div>
        </div>
    </div>
</div>
<div id="${id}">
    <@grid.datagrid id showViewForm/>
</div>
<div class="clear"></div>

<script type="text/javascript">
(function(){
    function createPage() {
        // Ищем оба типа счетчиков: сквозной и годовой.
        var itemTypes = LogicECM.module.Counters.Const.COUNTERS_CONTAINER.yearCounterType + "," + LogicECM.module.Counters.Const.COUNTERS_CONTAINER.plainCounterType;
        LogicECM.module.Counters.DataGrid.createDatagrid("${id}", {
            usePagination: false,
            disableDynamicPagination: true,
            showExtendSearchBlock: false,
            showCheckboxColumn: false,
            overrideSortingWith: false,
            bubblingLabel: LogicECM.module.Counters.Const.COUNTERS_DATAGRID_LABEL,
            actions: [{
                type:"datagrid-action-link-" + LogicECM.module.Counters.Const.COUNTERS_DATAGRID_LABEL,
                id:"onActionEdit",
                permission:"edit",
                label:"${msg('actions.counters.edit')}",
                evaluator: function() {
                    return LogicECM.module.Counters.isEngineer;
                }
            }],
            editFormTitleMsg: "${msg('actions.counters.edit')}"
        }, {
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
        }, ${messages});
    }

    LogicECM.module.Base.Util.loadResources([
        'scripts/lecm-base/components/advsearch.js',
        'scripts/lecm-base/components/lecm-datagrid.js',
        'scripts/lecm-regnumbers/counters-datagrid.js',
    ], ['css/lecm-regnumbers/documents-counters-reset.css'], createPage);
})();
</script>