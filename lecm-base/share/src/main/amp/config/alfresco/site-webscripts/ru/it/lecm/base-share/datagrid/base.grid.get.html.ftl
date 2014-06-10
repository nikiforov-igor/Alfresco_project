<#import "/ru/it/lecm/base-share/components/lecm-datagrid.ftl" as grid/>

<#assign id = args.htmlid>

<#assign showView = (args.showViewForm?? && args.showViewForm == "true")>
<#assign showArchive = (args.showArchiveCheckBox?? && args.showArchiveCheckBox == "true")>
<#assign dynamicPagination = (args.dynamicPagination?? && args.dynamicPagination == "true")>
<#assign showActions = (args.showActions?? && args.showActions == "true")>
<#assign bubblingLabel = args.bubblingLabel!"">
<#assign showCheckBox = (args.showCheckBox?? && args.showCheckBox == "true")>
<#assign attributeForShow = args.attributeForShow!"cm:name">
<#assign fullDelete = (args.fullDelete?? && args.fullDelete == "true")>
<#assign itemType = args.itemType>

<div class="yui-t1" id="${id}-datagrid-content">
	<div id="yui-main-2">
		<div class="yui-b datagrid-content" id="alf-content">
		<@grid.datagrid id=id showViewForm=showView showArchiveCheckBox=showArchive>
			<script type="text/javascript">//<![CDATA[
			function createDatagrid() {
				var datagrid = new LogicECM.module.Base.DataGrid('${id}').setOptions(
						{
							usePagination:true,
							showExtendSearchBlock:true,
                            showActionColumn: ${showActions?string},
							actions: [
								{
									type:"datagrid-action-link-${bubblingLabel}",
									id:"onActionEdit",
									permission:"edit",
									label:"${msg("actions.edit")}"
								},
								{
									type:"datagrid-action-link-${bubblingLabel}",
									id:"onActionDelete",
									permission:"delete",
									label:"${msg("actions.delete-row")}",
									evaluator: function (rowData) {
                                        var itemData = rowData.itemData;
                                        return this.isActiveItem(itemData);
                                    }
								},
								{
									type:"datagrid-action-link-${bubblingLabel}",
									id:"onActionRestore",
									permission:"delete",
									label:"${msg("actions.restore-row")}",
									evaluator: function (rowData) {
										return !this.isActiveItem(rowData.itemData);
									}
								}
							],
							bubblingLabel: "${bubblingLabel}",
							showCheckboxColumn: ${showCheckBox?string},
							attributeForShow:"${attributeForShow}"
						}).setMessages(${messages});

                YAHOO.util.Event.onContentReady ('${id}', function () {
                    YAHOO.Bubbling.fire ("activeGridChanged", {
                        datagridMeta: {
                            itemType: "${itemType}",
                            nodeRef: "${nodeRef}",
                            actionsConfig:{
                                fullDelete:${fullDelete?string}
                            }
                        },
                        bubblingLabel: "${bubblingLabel}"
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
