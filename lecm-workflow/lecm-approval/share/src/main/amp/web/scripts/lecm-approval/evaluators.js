if (typeof LogicECM == "undefined" || !LogicECM) {
	LogicECM = {};
}

LogicECM.module = LogicECM.module || {};
LogicECM.module.Routes = LogicECM.module.Routes || {};

(function () {

	function hasNodeRef(element) {
		return element.getData().nodeRef == this.nodeRef;
	}

	LogicECM.module.Routes.Evaluators = {
		stageItemUp: function (rowData) {
			var recordSet = this.widgets.dataTable.getRecordSet();
			var index = recordSet.getRecords().findIndex(hasNodeRef, rowData);
			var approvalListDatagrid = Alfresco.util.ComponentManager.find({name: 'LogicECM.module.Approval.ApprovalListDataGridControl'})[0];
			var prevRowData = recordSet.getRecord(index - 1);
			//var currentStage = this.datagridMeta.nodeRef;
			//var records = approvalListDatagrid.widgets.dataTable.getRecordSet().getRecords();
			//var currentStageType, currentStageState;
            //
			//if (records) {
			//	for (var i = 0; i < records.length; i++) {
			//		if (currentStage == records[i].getData('nodeRef')) {
			//			currentStageState = records[i].getData('itemData').prop_prop_lecmApproveAspects_approvalState.value;
			//			currentStageType = records[i].getData('itemData').prop_lecmWorkflowRoutes_stageWorkflowType.value;
			//			break;
			//		}
			//	}
			//}
			/*
			 * передвигать вверх можно если стоит галка "Разрешить изменять параметры согласования"
			 * элементу есть куда двигаться вверх
			 * этот элемент не активный
			 * верхний элемент тоже не активный
			 */
			return approvalListDatagrid.approvalIsEditable
				&& index > 0
				&& rowData.itemData.prop_lecmApproveAspects_approvalState.value === 'NEW'
				&& prevRowData && prevRowData.getData().itemData.prop_lecmApproveAspects_approvalState.value === 'NEW';
		},
		stageItemDown: function (rowData) {
			var recordSet = this.widgets.dataTable.getRecordSet();
			var index = recordSet.getRecords().findIndex(hasNodeRef, rowData);
			var approvalListDatagrid = Alfresco.util.ComponentManager.find({name: 'LogicECM.module.Approval.ApprovalListDataGridControl'})[0];
			var nextRowData = recordSet.getRecord(index + 1);
			/*
			 * передвигать вверх можно если стоит галка "Разрешить изменять параметры согласования"
			 * элементу есть куда двигаться вниз
			 * этот элемент не активный
			 * нижний элемент тоже не активный
			 */
			return approvalListDatagrid.approvalIsEditable
				&& index < recordSet.getLength() - 1
				&& rowData.itemData.prop_lecmApproveAspects_approvalState.value === 'NEW'
				&& nextRowData && nextRowData.getData().itemData.prop_lecmApproveAspects_approvalState.value === 'NEW';
		},
		stageItemEdit: function (rowData) {
			/*
			 * редактировать участника можно, только если стоит галка "Разрешить изменять параметры согласования"
			 * и участник еще не получил назначение на задачу
			 */
			var approvalListDatagrid = Alfresco.util.ComponentManager.find({name: 'LogicECM.module.Approval.ApprovalListDataGridControl'})[0];

			return approvalListDatagrid.approvalIsEditable && rowData.itemData.prop_lecmApproveAspects_approvalState.value === 'NEW';
		},
		stageItemDelete: function (rowData) {
			/*
			 * удалять участника можно, только если стоит галка "Разрешить изменять параметры согласования"
			 * и участник еще не получил назначение на задачу
			 */
			var approvalListDatagrid = Alfresco.util.ComponentManager.find({name: 'LogicECM.module.Approval.ApprovalListDataGridControl'})[0];

			return approvalListDatagrid.approvalIsEditable && rowData.itemData.prop_lecmApproveAspects_approvalState.value === 'NEW';
		},
		iterationAdd: function (rowData) {
			/*
			 * добавить участника можно, если этап новый или активный.
			 * в завершенный этап никого добавить нельзя
			 */

			// assuming this === 'LogicECM.module.Approval.ApprovalListDataGridControl'
			return this.approvalState === 'NEW' || this.approvalState === 'ACTIVE';
		},
		iterationEdit: function (rowData) {
			/*
			 * редактировать этап можно, только если стоит галка "Разрешить изменять параметры согласования".
			 * итерация должна быть или новая, или активная. сам этап при этом должен быть новым.
			 * в завершенных итерации/этапе ничего редактировать нельзя
			 */

			// assuming this === 'LogicECM.module.Approval.ApprovalListDataGridControl'
			return this.approvalIsEditable && (this.approvalState === 'NEW' ||
				(this.approvalState === 'ACTIVE' && rowData.itemData.prop_lecmApproveAspects_approvalState.value === 'NEW'));
		},
		iterationDelete: function (rowData) {
			/*
			 * удалить этап можно, только если стоит галка "Разрешить изменять параметры согласования".
			 * итерация должна быть или новая, или активная. сам этап при этом должен быть новым.
			 * в завершенных итерации/этапе ничего удалять нельзя
			 */

			// assuming this === 'LogicECM.module.Approval.ApprovalListDataGridControl'
			return this.approvalIsEditable && (this.approvalState === 'NEW' ||
				(this.approvalState === 'ACTIVE' && rowData.itemData.prop_lecmApproveAspects_approvalState.value === 'NEW'));
		}
	};
})();
