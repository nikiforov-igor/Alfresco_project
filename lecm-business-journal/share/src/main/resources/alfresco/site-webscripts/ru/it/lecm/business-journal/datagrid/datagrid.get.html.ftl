<#import "/ru/it/lecm/base-share/components/lecm-datagrid.ftl" as grid/>

<#assign id = args.htmlid>

<div class="yui-t1" id="bj-dictionary-grid">
	<div id="yui-main-2">
		<div class="yui-b" id="alf-content" style="margin-left: 0;">
			<!-- include base datagrid markup-->
		<@grid.datagrid id=id showViewForm=true showArchiveCheckBox=true>
			<script type="text/javascript">//<![CDATA[
			function createDatagrid() {
				var datagrid = new LogicECM.module.Base.DataGrid('${id}').setOptions(
						{
							usePagination:true,
							showExtendSearchBlock:true,
							actions: [
								{
									type:"action-link-${bubblingLabel!"bj-records"}",
									id:"onActionDelete",
									permission:"delete",
									label:"${msg("actions.delete-row")}"
								}
							],
							bubblingLabel: "${bubblingLabel!"bj-records"}",
							showCheckboxColumn: true,
							attributeForShow:"lecm-busjournal:bjRecord-description"
						}).setMessages(${messages});

                YAHOO.util.Event.onContentReady ('${id}', function () {
                    YAHOO.Bubbling.fire ("activeGridChanged", {
                        datagridMeta: {
                            itemType: "lecm-busjournal:bjRecord",
	                        nodeRef: LogicECM.module.BusinessJournal.CONTAINER.nodeRef,
                            searchConfig: {
                                filter: '+PATH:"/app:company_home/lecm-busjournal:businessJournal//*"'
                            }
                        },
                        bubblingLabel: "bj-records"
                    });
                });
			}

			function init() {
				createDatagrid();
			}

			YAHOO.util.Event.onDOMReady(init);
			//]]></script>
		</@grid.datagrid>
		</div>
	</div>
</div>
