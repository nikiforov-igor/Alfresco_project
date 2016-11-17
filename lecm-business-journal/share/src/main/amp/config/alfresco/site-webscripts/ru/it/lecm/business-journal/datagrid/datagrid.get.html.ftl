<#import "/ru/it/lecm/base-share/components/lecm-datagrid.ftl" as grid/>

<#assign id = args.htmlid>

<div class="yui-t1" id="bj-dictionary-grid">
	<div id="yui-main-2">
		<div class="yui-b datagrid-content" id="alf-content">
			<!-- include base datagrid markup-->
		<@grid.datagrid id=id showViewForm=true viewFormId="bj-view-node-form">
			<script type="text/javascript">//<![CDATA[
			(function() {
				function createDatagrid() {
					var datagrid = new LogicECM.module.BusinessJournal.DataGrid('${id}').setOptions(
							{
								usePagination:true,
								showExtendSearchBlock:true,
	                            dataSource: "/lecm/business-journal/ds/main",
	                            bubblingLabel: "${bubblingLabel!"bj-records"}",
								showCheckboxColumn: true,
								attributeForShow:"lecm-busjournal:bjRecord-date",
                                datagridMeta: {
                                    useFilterByOrg: false,
                                    itemType: "lecm-busjournal:bjRecord",
                                    sort:"lecm-busjournal:bjRecord-date|false"

                                },
								showActionColumn: false
							}).setMessages(${messages});
                            datagrid.draw();
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
