<#import "/ru/it/lecm/base-share/components/lecm-datagrid.ftl" as grid/>

<#assign itemId = args["itemId"]>
<#assign id = itemId?replace(":|/", "_", "r")>
<#assign datagridId = id + "-dtgrd">

<script>
(function(){
	LogicECM.CurrentModules = LogicECM.CurrentModules || {};
	LogicECM.CurrentModules["${id}"] = new LogicECM.module.Base.DataGrid("${datagridId}");
	LogicECM.CurrentModules["${id}"].setMessages(${messages});
	LogicECM.CurrentModules["${id}"].setOptions({
		usePagination: false,
		showExtendSearchBlock: false,
		showCheckboxColumn: false,
		bubblingLabel: "${datagridId}",
		expandable: false,
		showActionColumn: false
	});

	YAHOO.util.Event.onContentReady("${datagridId}", function () {
		YAHOO.Bubbling.fire("activeGridChanged", {
			datagridMeta:{
				itemType: LogicECM.module.Routes.Const.ROUTES_CONTAINER.stageItemType,
				nodeRef: '${itemId}',
				searchConfig: {
					filter: ""
				},
				actionsConfig: {
					fullDelete: true,
					trash: false
				}
			},
			bubblingLabel: "${datagridId}"
		});
	});

})();
</script>

<div id="${datagridId}" class="stagesDatagridExpanded">
	<@grid.datagrid datagridId false />
</div>
