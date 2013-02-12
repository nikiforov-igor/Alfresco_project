<#import "/ru/it/lecm/base-share/components/lecm-datagrid.ftl" as grid/>

<#assign id = args.htmlid>

<div class="yui-t1" id="orgstructure-employees-grid">
	<div id="yui-main-2">
		<div class="yui-b" id="alf-content" style="margin-left: 0;">
			<!-- include base datagrid markup-->
		<@grid.datagrid id=id showViewForm=true showArchiveCheckBox=true>
			<script type="text/javascript">//<![CDATA[
			function createDatagrid() {
				new LogicECM.module.Base.DataGrid('${id}').setOptions(
						{
							usePagination:true,
							useDynamicPagination:true,
							showExtendSearchBlock:true,
							actions: [
								{
									type:"action-link-${bubblingLabel!"employee"}",
									id:"onActionEdit",
									permission:"edit",
									label:"${msg("actions.edit")}"
								},
								{
									type:"action-link-${bubblingLabel!"employee"}",
									id:"onActionVersion",
									permission:"edit",
									label:"${msg("actions.version")}"
								},
								{
									type:"action-link-${bubblingLabel!"employee"}",
									id:"onActionDelete",
									permission:"delete",
									label:"${msg("actions.delete-row")}",
									evaluator: function (rowData) {
                                        var itemData = rowData.itemData;
                                        var isActive = itemData["prop_lecm-dic_active"] == undefined || itemData["prop_lecm-dic_active"].value == true;
                                        return isActive && (itemData["assoc_lecm-orgstr_employee-main-position"] == undefined ||
		                                        itemData["assoc_lecm-orgstr_employee-main-position"].value.length == 0);
                                    }
								}
							],
							bubblingLabel: "${bubblingLabel!"employee"}",
							showCheckboxColumn: false,
							attributeForShow:"lecm-orgstr:employee-last-name"
						}).setMessages(${messages});
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
