<#import "/ru/it/lecm/base-share/components/lecm-datagrid.ftl" as grid/>

<#assign params = field.control.params/>

<#assign itemId = args.itemId/>

<#assign controlId = fieldHtmlId + "-cntrl">

<script type='text/javascript'>
(function () {
	LogicECM.CurrentModules = LogicECM.CurrentModules || {};

	function init() {
		var loader = new YAHOO.util.YUILoader({
			require: [
				'lecmDatagrid',
				'lecmApprovalListDataGridControl'
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

		loader.onSuccess = checkPermission;
		loader.insert();
	}

	function checkPermission() {
		var brPermission = '${params.permission!""}';
		var isBRole = (brPermission.indexOf('~') < 0);
		brPermission = brPermission.replace('!', '').replace('~', '');
		if (brPermission != '') {
			Alfresco.util.Ajax.request({
				url: Alfresco.constants.PROXY_URI + (isBRole ? 'lecm/orgstructure/isCurrentEmployeeHasBusinessRole' : 'lecm/security/api/getPermission'),
				dataObj: {
					nodeRef: '${form.arguments.itemId}',
					roleId: brPermission,
					permission: brPermission
				},
				successCallback: {
					scope: this,
					fn: function (response) {
						if (response.json == true) {
							createDatagrid();
						} else {
							Dom.setStyle('${controlId}', 'display', 'none');
						}
					}
				},
				failureCallback: {
					scope: this,
					fn: function (response) {
						Alfresco.util.PopupManager.displayMessage({
							text: response.responseText
						});
					}
				}
			});
		} else {
			createDatagrid();
		}
	}

	function createDatagrid() {
		var controlId = '${controlId}';
		LogicECM.CurrentModules[controlId] = new LogicECM.module.Approval.ApprovalListDataGridControl(controlId, '${itemId}');
		LogicECM.CurrentModules[controlId].setMessages(${messages});
		LogicECM.CurrentModules[controlId].setOptions({
			usePagination: false,
			showExtendSearchBlock: false,
			showCheckboxColumn: false,
			forceSubscribing: true,
			approvalListDatagridId: '${field.control.params.approvalListDatagridId!"approvalListDataGridControl"}',
			approvalItemsDatagridId: '${field.control.params.approvalItemsDatagridId!"approvalItemsDataGridControl"}',
			bubblingLabel: 'ApprovalListDataGridControl',
			expandable: true,
			showActionColumn: true,
			actions: [{
				type: 'datagrid-action-link-ApprovalListDataGridControl',
				id: 'onActionPrint',
				permission: 'edit',
				label: '${msg("button.print")}'
			}]
		});
	}

	YAHOO.util.Event.onDOMReady(init);
})();
</script>

<div id='${controlId}'>
	<div class='viewmode-label' style='padding-top: 20px'>
		<h3>${field.label?html}</h3>
	</div>
	<@grid.datagrid controlId false />
</div>
