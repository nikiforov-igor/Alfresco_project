<#import "/ru/it/lecm/base-share/components/lecm-datagrid.ftl" as grid/>

<#assign id = args.htmlid/>
<#assign controlId = id + "-cntrl"/>
<#assign containerId = id + "-container"/>

<#assign FILTER = filter!"NOT_REF"/>

<div id="contracts-grid-${controlId}">
<@grid.datagrid containerId false id+"form" false>
    <script type="text/javascript">//<![CDATA[
    (function () {
        function init () {
            LogicECM.module.Base.Util.loadScripts([
                'scripts/lecm-base/components/advsearch.js',
                'scripts/lecm-base/components/lecm-datagrid.js',
               'scripts/grids/documents-journal-grid.js'
            ], function (){
                var dGrid = new LogicECM.module.DocumentsJournal.DataGrid('${containerId}').setOptions({
                    usePagination: false,
                    pageSize: 5,
                    showExtendSearchBlock: false,
                    actions: [],
                    allowCreate: false,
                    showActionColumn: false,
                    showCheckboxColumn: false,
                    bubblingLabel: "contracts-list",
                    attributeForShow: "lecm-document:present-string",
                    datagridMeta: {
                        itemType: "lecm-document:base",
                        nodeRef: null,
                        sort: "cm:modified|false",
                        searchConfig: {
                            filter: ("${FILTER}".length > 0 ? "${FILTER}" : "")
                        }
                    },
                    contractsWithMyActiveTasks: [
		            <#list contractsWithMyActiveTasks as contractNodeRef>
                        "${contractNodeRef}"<#if contractNodeRef_has_next>,</#if>
                    </#list>
                    ]
                }).setMessages(${messages});
                dGrid.draw();
            });
        }
        YAHOO.util.Event.onDOMReady(init);
    })();
    //]]></script>

</@grid.datagrid>
</div>
