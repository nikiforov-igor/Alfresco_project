<#import "/ru/it/lecm/base-share/components/lecm-datagrid.ftl" as grid/>

<#assign id = args.htmlid>

<@grid.datagrid id=id showViewForm=false showArchiveCheckBox=false>
<script type="text/javascript">//<![CDATA[
(function(){
    function createDatagrid(config) {
        var datagrid = new LogicECM.module.SearchQueries.DataGrid('${id}').setOptions(
                {
                    bubblingLabel: "query-view-in-arm",
                    usePagination: true,
                    showExtendSearchBlock: false,
                    showActionColumn:false,
                    expandable: false,
                    showCheckboxColumn:true,
                    datagridFormId: "searchQueryGrid"
                }).setMessages(${messages});

		datagrid.setQueryConfig(config);

        YAHOO.Bubbling.fire("activeGridChanged",
                {
                    datagridMeta: {
                        itemType: config.docType,
                        useFilterByOrg: true,
                        searchConfig: {
                            filter: config.query
                        }
                    },
                    bubblingLabel: "query-view-in-arm"
                });
    }

	function loadConfig() {
        Alfresco.util.Ajax.jsonRequest({
            method: 'GET',
            url: Alfresco.constants.PROXY_URI + 'api/metadata?nodeRef=' + "${queryNodeRef}" + "&shortQNames",
            successCallback: {
                scope: this,
                fn: function(response) {
                    var props = response.json.properties;
                    var configObj = YAHOO.lang.JSON.parse(props["lecm-search-queries:query-setting"]);
                    configObj.query = props["lecm-search-queries:query"];
                    configObj.queryNodeRef = "${queryNodeRef}";
                    createDatagrid(configObj);
                }
            },
            failureMessage: "${msg('msg.failure')}",
            scope: this,
            execScripts: true
        });
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
        ], [], loadConfig);
    }

    YAHOO.util.Event.onDOMReady(init);
})();
//]]></script>
</@grid.datagrid>
