<#import '/ru/it/lecm/base-share/components/lecm-datagrid.ftl' as grid/>

<#assign bubblingLabel = 'review-list'/>
<#assign datagridId = args.htmlid + '-' + bubblingLabel/>

<div id='${datagridId}'>
<@grid.datagrid id=datagridId showViewForm=false showArchiveCheckBox=false>
    <script type='text/javascript'>//<![CDATA[
    (function () {
        function initReviewListDatagrid() {
            new LogicECM.module.Review.ReviewList('${datagridId}', {
                usePagination: true,
                disableDynamicPagination: false,
                showExtendSearchBlock: true,
                overrideSortingWith: false,
                showCheckboxColumn: false,
                showActionColumn: true,
                expandable: false,
                bubblingLabel: '${bubblingLabel}',
                excludeColumns:['deletable'],
                actions: [{
                    type:'datagrid-action-link-' + '${bubblingLabel}',
                    id:'onActionEdit',
                    permission:'edit',
                    label: '${msg("actions.edit")}'
                }, {
                    type:'datagrid-action-link-' + '${bubblingLabel}',
                    id:'onActionDelete',
                    permission:'delete',
                    label: '${msg("actions.delete-row")}'
                }]
            }, {
                datagridFormId: 'reviewListDatagrid',
                createFormId: 'contractorOrg',
                itemType: '${itemType}',
                nodeRef: '${nodeRef}',
                useChildQuery: false,
                useFilterByOrg: false,
                searchConfig: {
                    filter: '@cm\\:creator:"' + Alfresco.constants.USERNAME + '"'
                }
            }, ${messages});
        }

        LogicECM.module.Base.Util.loadResources([
            'scripts/lecm-base/components/advsearch.js',
            'scripts/lecm-base/components/lecm-datagrid.js',
            'scripts/lecm-review/review-list.js'
        ], [
        ], initReviewListDatagrid);
    })();
    //]]></script>
</@>
</div>
