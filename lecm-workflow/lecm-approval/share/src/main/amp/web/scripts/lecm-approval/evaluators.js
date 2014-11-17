if (typeof LogicECM == "undefined" || !LogicECM) {
	LogicECM = {};
}

LogicECM.module = LogicECM.module || {};
LogicECM.module.Routes = LogicECM.module.Routes || {};
LogicECM.module.Routes.Evaluators = {
	stageItemEdit: function(rowData) {
		/*
		 * редактировать участника можно, только если стоит галка "Разрешить изменять параметры согласования"
		 * и участник еще не получил назначение на задачу
		 */
		var approvalListDatagrid = Alfresco.util.ComponentManager.find({name: 'LogicECM.module.Approval.ApprovalListDataGridControl'})[0];

		return approvalListDatagrid.approvalIsEditable && rowData.itemData.prop_lecmApproveAspects_approvalState.value === 'NEW';
	},
	stageItemDelete: function(rowData) {
		/*
		 * удалять участника можно, только если стоит галка "Разрешить изменять параметры согласования"
		 * и участник еще не получил назначение на задачу
		 */
		var approvalListDatagrid = Alfresco.util.ComponentManager.find({name: 'LogicECM.module.Approval.ApprovalListDataGridControl'})[0];

		return approvalListDatagrid.approvalIsEditable && rowData.itemData.prop_lecmApproveAspects_approvalState.value === 'NEW';
	},
	iterationAdd: function(rowData) {
		/*
		 * добавить участника можно, если этап новый или активный.
		 * в завершенный этап никого добавить нельзя
		 */

		// assuming this === 'LogicECM.module.Approval.ApprovalListDataGridControl'
		return this.approvalState === 'NEW' || this.approvalState === 'ACTIVE';
	},
	iterationEdit: function(rowData) {
		/*
		 * редактировать этап можно, только если стоит галка "Разрешить изменять параметры согласования".
		 * итерация должна быть или новая, или активная. сам этап при этом должен быть новым.
		 * в завершенных итерации/этапе ничего редактировать нельзя
		 */

		// assuming this === 'LogicECM.module.Approval.ApprovalListDataGridControl'
		return this.approvalIsEditable && (this.approvalState === 'NEW' ||
			(this.approvalState === 'ACTIVE' && rowData.itemData.prop_lecmApproveAspects_approvalState.value === 'NEW'));
	},
	iterationDelete: function(rowData) {
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
