<#import "/ru/it/lecm/base-share/components/lecm-datagrid.ftl" as grid/>

<#assign id = args.htmlid>
<div class="grid-vis-hdr">
<@grid.datagrid id=id showViewForm=false showArchiveCheckBox=false>
    <script type="text/javascript">//<![CDATA[
    (function(){
        function createDatagrid() {
            new LogicECM.module.SearchQueries.DataGrid('${id}').setOptions(
                    {
                        bubblingLabel: "query-search-results",
                        usePagination: true,
                        showExtendSearchBlock: false,
                        showActionColumn: false,
                        expandable: false,
                        showCheckboxColumn: false,
                        datagridFormId: "searchQueryGrid"
                    }).setMessages(${messages});
        }

        function init() {
            LogicECM.module.Base.Util.loadResources([
                'modules/simple-dialog.js',
                'scripts/lecm-base/components/advsearch.js',
                'scripts/lecm-base/components/lecm-datagrid.js',
                'scripts/lecm-dictionary/dictionary-datagrid.js',
                'components/form/date-range.js',
                'components/form/number-range.js',
                'scripts/lecm-base/components/versions.js',
                'scripts/lecm-arm/arm-documents-datagrid.js',
                'scripts/lecm-search-editor/search-query-datagrid.js'
            ], [], createDatagrid);
        }

        YAHOO.util.Event.onDOMReady(init);
    })();
    //]]></script>
</@grid.datagrid>
</div>
