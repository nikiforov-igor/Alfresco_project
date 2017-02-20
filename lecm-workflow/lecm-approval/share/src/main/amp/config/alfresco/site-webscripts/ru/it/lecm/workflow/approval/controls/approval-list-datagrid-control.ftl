<#import "/ru/it/lecm/base-share/components/lecm-datagrid.ftl" as grid/>

<#assign params = field.control.params/>
<#assign label = field.label?html/>
<#assign itemId = (args.itemId?? && args.itemId?contains("SpacesStore")) ? string(args.itemId, '')/>
<#if itemId == ''>
	<#assign itemId = (args.nodeRef?? && args.nodeRef?contains("SpacesStore")) ? string(args.nodeRef, '')/>
</#if>

<#assign controlId = fieldHtmlId + "-cntrl">
<#assign reportId = "lecm-approval-list">
<#assign hasStatemachine = (args.hasStatemachine!"false") == "true"/>
<#assign mayAdd = (args.mayAdd!"false") == "true"/>
<#assign mayView = (args.mayView!"false") == "true"/>

<#assign editable = (((params.editable!"true") != "false") && !(field.disabled) && hasStatemachine && mayAdd)
	|| (params.forceEditable?? && params.forceEditable == "true")>

<div id='${controlId}' class='hidden'>
    <input type="hidden" id="${fieldHtmlId}" name="${field.name}" value=""/><!--for validation-->
	<div class="approvalFinishedContainer">
		${msg("label.approvals.completed")}: <span id="${controlId}-approval-completed-count"></span>
		<a id="${controlId}-show-history-link" href="javascript:void(0)" class="hidden">${msg("label.view.history")}</a>
	</div>

	<#if editable>
	<span id="${controlId}-create-approval-list-button" class="yui-button yui-push-button hidden">
		<span class="first-child">
			<button type="button">${msg("label.button.new.approval")}</button>
		</span>
	</span>
	<span id="${controlId}-clear-button" class="yui-button yui-push-button hidden">
		<span class="first-child">
			<button type="button">${msg("label.button.clear.approval.data")}</button>
		</span>
	</span>
	<span id="${controlId}-expand-all-button" class="yui-button yui-push-button hidden">
		<span class="first-child">
			<button type="button">${msg("label.button.expand.all.stages")}</button>
		</span>
	</span>
	</#if>

	<div id="${controlId}-approval-container" class="approvalContainer hidden">
		<div class="approvalControlsContainer">
            <a id="${controlId}-expandAllStages" class="expandAllStages" href="javascript:void(0);" title="${msg('label.button.expand.all.stages')}"></a>
			<#if editable><a id="${controlId}-editIteration" class="editIteration" href="javascript:void(0);" title="${msg('title.edit')}"></a></#if>
			<a id="${controlId}-printApprovalReport" class="printApprovalReport" href="javascript:void(0);" title="${msg('title.print')}"></a>
		</div>

		<div class="approvalDescriptionContainer">
			<div class="approvalRouteDataContainer">
				<strong>${msg("label.route")}</strong>
				<span id="${controlId}-source-route-info"></span>
			</div>
			<div class="approvalStatusContainer">
				<strong>${msg("label.current.approval.status")}</strong>
				<span id="${controlId}-current-approval-info"></span>
			</div>
		</div>

		<#if editable>
		<span id="${controlId}-add-stage" class="yui-button yui-push-button hidden addStageButton">
			<span class="first-child">
				<button type="button">${msg("label.button.add.stage")}</button>
			</span>
		</span>
		</#if>
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
			formId: "${args.htmlid}",
			fieldId: "${field.configName}",
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
				editable: ${editable?string},
				isApproval: true
			},
			datagridMeta: {
				useFilterByOrg: false,
				actionsConfig: {
					fullDelete: true,
					trash: false
				}
			},
            excludeColumns: ["lecmApproveAspects:hasComment"],
			showActionColumn: ${editable?string},
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
		LogicECM.CurrentModules[controlId].renewDatagrid();
		LogicECM.module.Base.Util.createComponentReadyElementId("${controlId}", "${args.htmlid}", "${field.configName}");
	}

	YAHOO.util.Event.onDOMReady(function() {

		var js = ['scripts/lecm-base/components/lecm-datagrid.js',
				  'scripts/lecm-base/components/advsearch.js',
				  'modules/simple-dialog.js',
				  'scripts/lecm-approval/approval-list-datagrid-control.js',
				  'scripts/lecm-approval/evaluators.js',
				  'scripts/lecm-workflow/routes/stages-control.js'];
		var css = ['css/lecm-approval/approval-list-datagrid-control.css'];
		LogicECM.module.Base.Util.loadResources(js, css, createDatagrid);
	});
})();
</script>
