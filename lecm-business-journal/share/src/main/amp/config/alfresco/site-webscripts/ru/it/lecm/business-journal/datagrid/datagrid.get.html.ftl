<@script type="text/javascript" src="${url.context}/res/components/form/date-range.js"></@script>
<@script type="text/javascript" src="${url.context}/res/components/form/number-range.js"></@script>
<@link rel="stylesheet" type="text/css" href="${url.context}/res/components/search/search.css" />

<!-- Historic Properties Viewer -->
<@script type="text/javascript" src="${url.context}/res/scripts/lecm-base/components/versions.js"></@script>
<@link rel="stylesheet" type="text/css" href="${url.context}/res/modules/document-details/historic-properties-viewer.css" />


<#import "/ru/it/lecm/base-share/components/lecm-datagrid.ftl" as grid/>

<#assign id = args.htmlid>

<div class="yui-t1" id="bj-dictionary-grid">
	<div id="yui-main-2">
		<div class="yui-b datagrid-content" id="alf-content">
			<!-- include base datagrid markup-->
		<@grid.datagrid id=id showViewForm=true showArchiveCheckBox=true>
			<script type="text/javascript">//<![CDATA[
			(function() {
				function createDatagrid() {
					var datagrid = new LogicECM.module.BusinessJournal.DataGrid('${id}').setOptions(
							{
								usePagination:true,
								showExtendSearchBlock:true,
								actions: [
									{
										type:"datagrid-action-link-${bubblingLabel!"bj-records"}",
										id:"onActionDelete",
										permission:"delete",
										label:"${msg("actions.delete-row")}",
	                                    evaluator: function (rowData) {
		                                    if (rowData) {
	                                            var itemData = rowData.itemData;
	                                            return itemData["prop_lecm-dic_active"] == undefined ||
	                                                    itemData["prop_lecm-dic_active"].value == true;
		                                    }
	                                        return false;
	                                    }
									}
								],
	                            dataSource: "/lecm/business-journal/ds/main",
	                            bubblingLabel: "${bubblingLabel!"bj-records"}",
								showCheckboxColumn: true,
								attributeForShow:"lecm-busjournal:bjRecord-date"
							}).setMessages(${messages});
	
	                YAHOO.util.Event.onContentReady ('${id}', function () {
	                    YAHOO.Bubbling.fire ("activeGridChanged", {
	                        datagridMeta: {
	                            itemType: "lecm-busjournal:bjRecord",
		                        nodeRef: LogicECM.module.BusinessJournal.CONTAINER,
	                            sort:"lecm-busjournal:bjRecord-date|false"
	
	                        },
	                        bubblingLabel: "bj-records"
	                    });
	                });
				}
	
				function init() {
                    LogicECM.module.Base.Util.loadScripts([
                        'scripts/lecm-base/components/advsearch.js',
                        '/scripts/lecm-base/components/lecm-datagrid.js',
                        '/scripts/lecm-business-journal/business-journal-datagrid.js'
					], createDatagrid);
				}
	
				YAHOO.util.Event.onDOMReady(init);
			})();
			//]]></script>
		</@grid.datagrid>
		</div>
	</div>
</div>
