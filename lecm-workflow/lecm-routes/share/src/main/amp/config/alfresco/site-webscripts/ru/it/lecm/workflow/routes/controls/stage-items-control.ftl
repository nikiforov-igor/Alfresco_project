<#import "/ru/it/lecm/base-share/components/lecm-datagrid.ftl" as grid/>

<#assign params = field.control.params/>

<#assign itemId = args.itemId/>

<#assign controlId = fieldHtmlId + "-cntrl">

<script type='text/javascript'>
(function () {
	LogicECM.CurrentModules = LogicECM.CurrentModules || {};

	function init() {
		LogicECM.module.Base.Util.loadResources([
			'scripts/lecm-base/components/lecm-datagrid.js',
			'scripts/lecm-workflow/routes/stage-items-control.js'
		],
		[], createDatagrid);
	}

	function createDatagrid() {
		var controlId = '${controlId}';
		LogicECM.CurrentModules[controlId] = new LogicECM.module.Routes.StagesItemsControlDatagrid(controlId);
		LogicECM.CurrentModules[controlId].setMessages(${messages});
		LogicECM.CurrentModules[controlId].setOptions({
			usePagination: false,
			showExtendSearchBlock: false,
			showCheckboxColumn: false,
			bubblingLabel: controlId,
			expandable: false,
			showActionColumn: true,
			allowCreate: false,
			forceSubscribing: true,
			actions: [{
				type:"datagrid-action-link-" + controlId,
				id:"onActionEdit",
				permission:"edit",
				label:"${msg('actions.edit')}"
			}, {
				type:"datagrid-action-link-" + controlId,
				id:"onActionDelete",
				permission:"delete",
				label:"${msg('actions.delete-row')}"
			}]
		});

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
			bubblingLabel: controlId
		});
	}

	YAHOO.util.Event.onContentReady('${controlId}', init);
})();
</script>

<div class='form-field'>
	<div id='${controlId}'>
		<select id="${controlId}-add-item-dropdown">
			<option value="dafault">-- Добавить участников этапа</option>
			<option value="employee">Добавить сотрудника</option>
			<option value="macros">Добавить потенциального участника</option>
		</select>
		<@grid.datagrid controlId false />
	</div>
</div>
<div class="clear"></div>
