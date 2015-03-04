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
		overrideSortingWith: false,
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
			'lecmApproveAspects:hasComment',
			'lecmWorkflowRoutes:stageItemEmployeeAssoc',
			'lecmWorkflowRoutes:stageItemMacrosAssoc'
		]
	});

	<#if isApproval>
	LogicECM.CurrentModules["${id}"].getCustomCellFormatter = LogicECM.module.Approval.StageExpanded.getCustomCellFormatter;
	</#if>
	<#if isApproval && editable>
	LogicECM.CurrentModules["${id}"].onActionDelete = function (p_items, owner, actionsConfig, fnDeleteComplete) {
		this.onDelete(p_items, owner, actionsConfig, function() {
			YAHOO.Bubbling.fire('stageItemDeleted');
		}, null);
	};
	</#if>

	YAHOO.util.Event.onContentReady("${datagridId}", function () {
		YAHOO.Bubbling.fire("activeGridChanged", {
			datagridMeta:{
				useFilterByOrg: false,
				itemType: LogicECM.module.Routes.Const.ROUTES_CONTAINER.stageItemType,
				nodeRef: '${itemId}',
				useChildQuery: true,
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
