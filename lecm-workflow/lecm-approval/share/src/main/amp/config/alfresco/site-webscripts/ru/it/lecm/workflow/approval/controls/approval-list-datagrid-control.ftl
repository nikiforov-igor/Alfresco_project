<#import "/ru/it/lecm/base-share/components/lecm-datagrid.ftl" as grid/>

<#assign params = field.control.params/>
<#assign label = field.label?html/>
<#assign itemId = args.itemId/>
<#assign controlId = fieldHtmlId + "-cntrl">
<#assign reportId = "approval-list-main">


<div id='${controlId}'>

	<div class="approvalControlsContainer">
		<a id="editIteration" class="editIteration" href="javascript:void(0);" title="Редактировать"></a>
		<a id="printApprovalReport" class="printApprovalReport" href="javascript:void(0);" title="Печать"></a>
	</div>

	<select id="${controlId}-create-approval-list">
		<option value="dуfault">-- Создать лист согласования</option>
		<option value="route">Из маршрута</option>
		<option value="empty">Пустой</option>
	</select>
	<div class="yui-button yui-push-button controlButtonContainer">
		<span class="first-child">
			<button type="button" id="${controlId}-add-stage">Добавить этап</button>
		</span>
	</div>
	<@grid.datagrid controlId false />
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
			forceSubscribing: true,
			bubblingLabel: '${controlId}',
			expandable: true,
			expandDataSource: "ru/it/lecm/workflow/routes/stages/stageExpanded",
			useChildQuery: true,
			excludeColumns: ['lecmApproveAspects:approvalState'],
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
				label:"${msg('actions.add.employee')}"
			}, {
				type:"datagrid-action-link-" + controlId,
				id:"onActionAddMacros",
				permission:"edit",
				label:"${msg('actions.add.macros')}"
			}, {
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
	}

	YAHOO.util.Event.onDOMReady(function() {

		var js = ['scripts/lecm-base/components/lecm-datagrid.js',
				  'scripts/lecm-approval/approval-list-datagrid-control.js',
				  'scripts/lecm-workflow/routes/stages-control.js'];
		var css = ['css/lecm-approval/approval-list-datagrid-control.css'];
		LogicECM.module.Base.Util.loadResources(js, css, createDatagrid);
	});
})();
</script>
