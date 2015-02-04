<#import '/ru/it/lecm/base-share/components/lecm-datagrid.ftl' as grid/>


<#assign itemId = args.itemId/>
<#assign documentNodeRef = args.documentNodeRef/>

<#assign controlId = fieldHtmlId + '-cntrl'>
<#assign reportId = 'lecm-approval-history'>


<div id='${controlId}'>
	<span><a href='javascript:void(0)' id='printApprovalHistoryReport'>${msg("title.print.approval.history.report")}</a></span>
	<@grid.datagrid controlId false />
</div>

<script type='text/javascript'>
(function () {
	LogicECM.CurrentModules = LogicECM.CurrentModules || {};

	function createDatagrid() {
		var controlId = '${controlId}';
		LogicECM.CurrentModules[controlId] = new LogicECM.module.Approval.ApprovalHistoryDataGridControl(controlId);
		LogicECM.CurrentModules[controlId].setMessages(${messages});
		LogicECM.CurrentModules[controlId].setOptions({
			documentNodeRef: '${documentNodeRef}',
			reportId: '${reportId}',
			usePagination: false,
			showExtendSearchBlock: false,
			overrideSortingWith: false,
			showCheckboxColumn: false,
			forceSubscribing: true,
			bubblingLabel: controlId,
			expandable: true,
			expandDataSource: 'ru/it/lecm/workflow/approval/approvalHistoryExpanded',
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
			showActionColumn: false
		});

		YAHOO.Bubbling.fire('activeGridChanged', {
			datagridMeta:{
				itemType: LogicECM.module.Routes.Const.ROUTES_CONTAINER.routeType,
				nodeRef: '${itemId}',
				useChildQuery: true,
				sort: 'cm:created|true',
				searchConfig: {
					filter: ''
				},
				actionsConfig: {
					fullDelete: true,
					trash: false
				}
			},
			bubblingLabel: controlId
		});
	}

	YAHOO.util.Event.onContentReady('${controlId}', function () {
		var js = ['scripts/lecm-base/components/lecm-datagrid.js',
				  'scripts/lecm-approval/approval-history-datagrid-control.js'];
		var css = ['css/lecm-approval/approval-history-datagrid-control.css'];
		LogicECM.module.Base.Util.loadResources(js, css, createDatagrid);
	});
})();
</script>
