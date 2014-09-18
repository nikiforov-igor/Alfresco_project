<#import "/ru/it/lecm/base-share/components/lecm-datagrid.ftl" as grid/>

<#assign params = field.control.params/>
<#assign label = field.label?html/>
<#assign itemId = args.itemId/>
<#assign controlId = fieldHtmlId + "-cntrl">
<#assign reportId = "approval-list-main">

<div id='${controlId}'>

	<div class="approvalFinishedContainer">
		Завершено согласований: <span id="${controlId}-approval-completed-count"></span>
		<a id="${controlId}-show-history-link" href="javascript:void(0)" class="hidden">Смотреть историю</a>
	</div>

	<span id="${controlId}-create-approval-list-button" class="yui-button yui-push-button hidden">
		<span class="first-child">
			<button type="button">Создать новое согласование</button>
		</span>
	</span>
	<span id="${controlId}-clear-button" class="yui-button yui-push-button hidden">
		<span class="first-child">
			<button type="button">Очистить данные согласования</button>
		</span>
	</span>

	<div id="${controlId}-approval-container" class="approvalContainer hidden">
		<div class="approvalControlsContainer">
			<a id="editIteration" class="editIteration" href="javascript:void(0);" title="Редактировать"></a>
			<a id="printApprovalReport" class="printApprovalReport" href="javascript:void(0);" title="Печать"></a>
		</div>

		<div class="approvalDescriptionContainer">
			<div class="approvalRouteDataContainer">
				<strong>Маршрут</strong>
				<span id="${controlId}-source-route-info"></span>
			</div>
			<div class="approvalStatusContainer">
				<strong>Статус текущего согласования</strong>
				<span id="${controlId}-current-approval-info"></span>
			</div>
		</div>

		<span id="${controlId}-add-stage" class="yui-button yui-push-button hidden">
			<span class="first-child">
				<button type="button">Добавить этап</button>
			</span>
		</span>
		<div></div>
		<@grid.datagrid controlId false />
	</div>
</div>

<script type='text/javascript'>
(function () {
	LogicECM.CurrentModules = LogicECM.CurrentModules || {};

	function createDatagrid() {
		var controlId = '${controlId}';
		LogicECM.CurrentModules[controlId] = new LogicECM.module.Approval.ApprovalListDataGridControl(controlId, '${itemId}');
		LogicECM.CurrentModules[controlId].setMessages(${messages});
		LogicECM.CurrentModules[controlId].setOptions({
			reportId: '${reportId}',
			usePagination: false,
			showExtendSearchBlock: false,
			showCheckboxColumn: false,
			overrideSortingWith: false,
			forceSubscribing: true,
			isApprovalListContext: true,
			bubblingLabel: '${controlId}',
			expandable: true,
			expandDataSource: 'ru/it/lecm/workflow/routes/stages/stageExpanded',
			expandDataObj: {
				editable: true,
				isApproval: true
			},
			datagridMeta: {
				actionsConfig: {
					fullDelete: true,
					trash: false
				}
			},
			showActionColumn: true,
			actions: [{
				type:"datagrid-action-link-" + controlId,
				id:"onActionAddEmployee",
				permission:"edit",
				label:"${msg('actions.add.employee')}",
				evaluator: LogicECM.module.Routes.Evaluators.iterationAdd
			}, {
				type:"datagrid-action-link-" + controlId,
				id:"onActionAddMacros",
				permission:"edit",
				label:"${msg('actions.add.macros')}",
				evaluator: LogicECM.module.Routes.Evaluators.iterationAdd
			}, {
				type:"datagrid-action-link-" + controlId,
				id:"onActionEdit",
				permission:"edit",
				label:"${msg('actions.edit')}",
				evaluator: LogicECM.module.Routes.Evaluators.iterationEdit
			}, {
				type:"datagrid-action-link-" + controlId,
				id:"onActionDelete",
				permission:"delete",
				label:"${msg('actions.delete-row')}",
				evaluator: LogicECM.module.Routes.Evaluators.iterationDelete
			}]
		});
	}

	YAHOO.util.Event.onDOMReady(function() {

		var js = ['scripts/lecm-base/components/lecm-datagrid.js',
				  'scripts/lecm-approval/approval-list-datagrid-control.js',
				  'scripts/lecm-approval/evaluators.js',
				  'scripts/lecm-workflow/routes/stages-control.js'];
		var css = ['css/lecm-approval/approval-list-datagrid-control.css'];
		LogicECM.module.Base.Util.loadResources(js, css, createDatagrid);
	});
})();
</script>
