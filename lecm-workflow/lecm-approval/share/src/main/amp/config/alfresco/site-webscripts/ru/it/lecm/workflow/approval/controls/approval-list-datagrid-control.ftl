<#import "/ru/it/lecm/base-share/components/lecm-datagrid.ftl" as grid/>

<#assign params = field.control.params/>
<#assign label = field.label?html/>
<#assign itemId = args.itemId/>
<#assign controlId = fieldHtmlId + "-cntrl">
<#assign editable = ((params.editable!"false") == "true") && !(field.disabled)>
<#assign reportId = "approval-list-main">

<div id='${controlId}'>

	<div class="printApprovalReportContainer">
		<a id="printApprovalReport" class="printApprovalReport" href="javascript:void(0);" title="Печать"></a>
	</div>
	<#if !editable>
	<div class="clear"></div>
	</#if>
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
			usePagination: false,
			showExtendSearchBlock: false,
			showCheckboxColumn: false,
			forceSubscribing: true,
			bubblingLabel: '${controlId}',
			expandable: true,
			expandDataSource: "ru/it/lecm/workflow/routes/stages/stageExpanded",
			useChildQuery: true,
			datagridMeta: {
				actionsConfig: {
					fullDelete: true,
					trash: false
				}
			},
			showActionColumn: true,
			actions: [{
				type:"datagrid-action-link-${controlId}",
				id:"onActionEdit",
				permission:"edit",
				label:"${msg('actions.edit')}"
			},{
				type:"datagrid-action-link-${controlId}",
				id:"onActionDelete",
				permission:"delete",
				label:"${msg('actions.delete-row')}"
			}]
		});
	}

	YAHOO.util.Event.onDOMReady(function() {

		YAHOO.util.Event.delegate('Share', 'click', function() {
			LogicECM.module.Base.Util.printReport('${itemId}', '${reportId}');
		}, '#printApprovalReport', this, true);

		var js = ['scripts/lecm-base/components/lecm-datagrid.js',
				  'scripts/lecm-approval/approval-list-datagrid-control.js'];
		var css = [];
		LogicECM.module.Base.Util.loadResources(js, css, createDatagrid);
	});
})();
</script>
