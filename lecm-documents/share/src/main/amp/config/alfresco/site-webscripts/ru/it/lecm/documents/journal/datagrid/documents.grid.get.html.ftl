<@script type="text/javascript" src="${url.context}/res/components/form/date-range.js"></@script>
<@script type="text/javascript" src="${url.context}/res/components/form/number-range.js"></@script>
<@link rel="stylesheet" type="text/css" href="${url.context}/res/components/search/search.css" />

<!-- Historic Properties Viewer -->
<@script type="text/javascript" src="${url.context}/res/scripts/lecm-base/components/versions.js"></@script>
<@link rel="stylesheet" type="text/css" href="${url.context}/res/modules/document-details/historic-properties-viewer.css" />

<@script type="text/javascript" src="${url.context}/res/modules/simple-dialog.js"/>
<@script type="text/javascript" src="${url.context}/res/scripts/lecm-base/components/advsearch.js"></@script>
<@script type="text/javascript" src="${url.context}/res/scripts/lecm-base/components/lecm-datagrid.js"></@script>
<@script type="text/javascript" src="${url.context}/res/scripts/grids/documents-journal-grid.js"></@script>


<#import "/ru/it/lecm/base-share/components/lecm-datagrid.ftl" as grid/>

<#assign id = args.htmlid>

<div class="yui-t1" id="documents-grid">
    <div id="yui-main-2">
        <div class="yui-b datagrid-content" id="alf-content">
        <@grid.datagrid id=id showViewForm=true>
            <script type="text/javascript">//<![CDATA[
			(function(){
	            function createDatagrid() {
	                new LogicECM.module.DocumentsJournal.DataGrid('${id}').setOptions({
	                    usePagination: true,
	                    pageSize: 10,
	                    showExtendSearchBlock: false,
	                    actions: [],
	                    allowCreate: false,
	                    showActionColumn: false,
	                    showCheckboxColumn: false,
	                    bubblingLabel: "documents-journal",
	                    attributeForShow: "lecm-document:present-string"
	                }).setMessages(${messages});

	                YAHOO.Bubbling.fire("activeGridChanged",
	                        {
	                            datagridMeta: {
	                                itemType: "lecm-document:base",
	                                nodeRef: LogicECM.module.DocumentsJournal.SETTINGS.nodeRef,
	                                sort: "cm:modified|false",
	                                searchConfig: {
	                                    filter: '+PATH:"' + LogicECM.module.DocumentsJournal.SETTINGS.draftsPath + '//*"'
	                                            + ' OR +PATH:"' + LogicECM.module.DocumentsJournal.SETTINGS.documentsPath + '//*"'
	                                }

	                            },
	                            bubblingLabel: "documents-journal"
	                        });
	            }

	            function init() {
	                createDatagrid();
	            }

	            YAHOO.util.Event.onDOMReady(init);
			})();
            //]]></script>
        </@grid.datagrid>
        </div>
    </div>
</div>
