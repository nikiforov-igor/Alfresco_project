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
		overrideSortingWith: false,
		showCheckboxColumn: false,
		bubblingLabel: "${datagridId}",
		showActionColumn: false,
		expandable: true,
		expandDataSource: 'ru/it/lecm/workflow/routes/stages/stageExpanded',
		expandDataObj: {
			editable: false,
			isApproval: true
		}
	});


	YAHOO.util.Event.onContentReady("${datagridId}", function () {
		YAHOO.Bubbling.fire("activeGridChanged", {
			datagridMeta:{
				itemType: LogicECM.module.Routes.Const.ROUTES_CONTAINER.stageType,
				nodeRef: '${itemId}',
				useChildQuery: true,
				useFilterByOrg: false,
				sort: 'cm:created|true',
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
<div class="clear"></div>
