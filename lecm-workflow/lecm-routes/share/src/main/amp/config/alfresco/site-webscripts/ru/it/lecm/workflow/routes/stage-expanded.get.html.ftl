<#import "/ru/it/lecm/base-share/components/lecm-datagrid.ftl" as grid/>

<#assign itemId = args["itemId"]>
<#assign id = itemId?replace(":|/", "_", "r")>
<#assign datagridId = id + "-dtgrd">
<#assign editable = args["editable"] == "true"/>
<#assign isApproval = args["isApproval"] == "true"/>

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
		showActionColumn: ${editable?string},
		<#if editable>
		actions: [{
			type:"datagrid-action-link-${datagridId}",
			id:"onActionDelete",
			permission:"delete",
			label:"${msg('actions.delete-row')}",
			evaluator: LogicECM.module.Routes.Evaluators.stageItemEdit
		}, {
			type:"datagrid-action-link-${datagridId}",
			id:"onActionEdit",
			permission:"edit",
			label:"${msg('actions.edit')}",
			evaluator: LogicECM.module.Routes.Evaluators.stageItemDelete
		}],
		</#if>
		excludeColumns: [
			<#if !isApproval>
			'lecmApproveAspects:approvalState',
			</#if>
			'lecmApproveAspects:approvalDecision',
			'lecmWorkflowRoutes:stageItemEmployeeAssoc',
			'lecmWorkflowRoutes:stageItemMacrosAssoc'
		]
	});

	YAHOO.util.Event.onContentReady("${datagridId}", function () {
		YAHOO.Bubbling.fire("activeGridChanged", {
			datagridMeta:{
				itemType: LogicECM.module.Routes.Const.ROUTES_CONTAINER.stageItemType,
				nodeRef: '${itemId}',
				useChildQuery: true,
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
