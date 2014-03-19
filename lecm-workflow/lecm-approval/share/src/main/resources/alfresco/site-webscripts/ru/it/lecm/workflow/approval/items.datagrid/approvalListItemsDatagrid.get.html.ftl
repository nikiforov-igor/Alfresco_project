<#import "/ru/it/lecm/base-share/components/lecm-datagrid.ftl" as grid/>
<#import "/ru/it/lecm/base-share/components/base-components.ftl" as comp/>

<#assign datagridId = id + "-dtgrd">

<script>
(function(){
	LogicECM.CurrentModules = LogicECM.CurrentModules || {};
	LogicECM.CurrentModules["${id}"] = new LogicECM.module.Approval.ApprovalItemsDataGridControl("${datagridId}", "${approvalListNodeRef}", "${approvalItemType}");
	LogicECM.CurrentModules["${id}"].setMessages(${messages});
	LogicECM.CurrentModules["${id}"].setOptions({
		usePagination: false,
		showExtendSearchBlock: false,
        datagridFormId: "${formId!"approvalItemsDataGridControl"}",
		showCheckboxColumn: false,
		bubblingLabel: "${datagridId}",
		expandable: false,
		showActionColumn: false,
		attributeForShow: "lecmApprovalResult:approvalResultItemDecision"
	});
})();
</script>

<div id="${datagridId}" style="padding-left: 30px;">
	<@grid.datagrid datagridId false />
</div>
