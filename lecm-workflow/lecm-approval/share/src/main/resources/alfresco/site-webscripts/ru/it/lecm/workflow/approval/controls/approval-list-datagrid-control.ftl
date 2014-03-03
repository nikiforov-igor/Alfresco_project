<#import "/ru/it/lecm/base-share/components/lecm-datagrid.ftl" as grid/>
<#import "/ru/it/lecm/base-share/components/base-components.ftl" as comp/>

<#assign params = field.control.params/>

<#assign itemId = args.itemId/>

<#assign controlId = fieldHtmlId + "-cntrl">

<script type="text/javascript">
(function() {
	LogicECM.CurrentModules = LogicECM.CurrentModules || {};

	function init() {
		var loader = new YAHOO.util.YUILoader({
			require: [
				"lecmDatagrid",
				"lecmApprovalListDataGridControl"
			],
			skin: {}
		});

		loader.addModule({
			name: 'lecmDatagrid',
			type: 'js',
			fullpath: Alfresco.constants.URL_RESCONTEXT + 'scripts/lecm-base/components/lecm-datagrid.js'
		});

		loader.addModule({
			name: 'lecmApprovalListDataGridControl',
			type: 'js',
			fullpath: Alfresco.constants.URL_RESCONTEXT + 'scripts/lecm-approval/approval-list-datagrid-control.js'
		});

		loader.onSuccess = createDatagrid;
		loader.insert();
	}

	function createDatagrid() {
		LogicECM.CurrentModules["approvalListDatagridControl"] = new LogicECM.module.Approval.ApprovalListDataGridControl("${controlId}", "${itemId}");
		LogicECM.CurrentModules["approvalListDatagridControl"].setMessages(${messages});
		LogicECM.CurrentModules["approvalListDatagridControl"].setOptions({
			usePagination: true,
			showExtendSearchBlock: false,
			showCheckboxColumn: false,
			bubblingLabel: "ApprovalListDataGridControl",
			expandable: true,
			showActionColumn: true,
			actions: [{
				type: "datagrid-action-link-ApprovalListDataGridControl",
				id: "onActionPrint",
				permission: "edit",
				label: "${msg('button.print')}"
			}]
		});
	}

	YAHOO.util.Event.onDOMReady(init);
})();
</script>


<div id="${controlId}">
	<@grid.datagrid controlId false />
</div>
