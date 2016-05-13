/* global YAHOO, Alfresco */

if (typeof LogicECM == 'undefined' || !LogicECM) {
	LogicECM = {};
}

LogicECM.module = LogicECM.module || {};
LogicECM.module.Review = LogicECM.module.Review || {};

(function () {

	LogicECM.module.Review.DocumentTable = function (containerId) {
		LogicECM.module.Review.DocumentTable.superclass.constructor.call(this, containerId);
		this.name = 'LogicECM.module.Review.DocumentTable';
		return this;
	};

	YAHOO.lang.extend(LogicECM.module.Review.DocumentTable, LogicECM.module.DocumentTable, {

		createDataGrid: function() {
			if (this.tableData != null && this.tableData.rowType != null) {
				var actions = [];
				var actionType = "datagrid-action-link-" + this.options.bubblingLabel;
				if (!this.options.disabled && this.options.mode == "edit") {
					if (this.options.allowEdit === true) {
						actions.push({
							type: actionType,
							id: "onActionEdit",
							permission: "edit",
							label: this.msg("actions.edit")
						});
					}
					if (this.options.allowDelete === true) {
						actions.push({
							type: actionType,
							id: "onActionDelete",
							permission: "delete",
							label: this.msg("actions.delete-row")
						});
					}
				}
				var splitActionAt = actions.length;

				if (!this.options.isTableSortable && this.options.showActions && this.options.mode == "edit" && !this.options.disabled) {
					var otherActions = [];
					if (this.options.allowEdit === true) {
						otherActions.push({
							type: actionType,
							id: "onMoveTableRowUp",
							permission: "edit",
							label: this.msg("actions.tableRowUp")
						});
						otherActions.push({
							type: actionType,
							id: "onMoveTableRowDown",
							permission: "edit",
							label: this.msg("action.tableRowDown")
						});
					}
					if (this.options.allowCreate === true) {
						otherActions.push({
							type: actionType,
							id: "onAddRow",
							permission: "edit",
							label: this.msg("action.addRow")
						});
					}
					actions = actions.concat(otherActions);
					splitActionAt = actions.length;
				}

				var datagrid = new LogicECM.module.DocumentTableDataGrid(this.options.containerId).setOptions({
					usePagination: true,
					showExtendSearchBlock: false,
					formMode: this.options.mode,
					actions: actions,
					splitActionsAt: splitActionAt,
					datagridMeta: {
						useFilterByOrg: false,
						itemType: this.tableData.rowType,
						datagridFormId: this.options.datagridFormId,
						createFormId: "",
						nodeRef: this.tableData.nodeRef,
						actionsConfig: {
							fullDelete: true
						},
						sort: this.options.sort ? this.options.sort : "lecm-document:indexTableRow",
						useChildQuery: true
					},
					bubblingLabel: this.options.bubblingLabel,
					showActionColumn: this.options.showActions,
					showOtherActionColumn: true,
					showCheckboxColumn: false,
					attributeForShow: this.options.attributeForShow,
					pageSize: this.tableData.pageSize != null && this.tableData.pageSize > 0 ? this.tableData.pageSize : 10,
					useCookieForSort: false,
					overrideSortingWith: this.options.isTableSortable,
					refreshAfterCreate: this.options.refreshAfterCreate,
					editFormTitleMsg: this.options.editFormTitleMsg,
					createFormTitleMsg: this.options.createFormTitleMsg,
					viewFormTitleMsg: this.options.viewFormTitleMsg,
					dataSource: this.options.dataSource,
					expandable: this.options.expandable,
					expandDataSource: this.options.expandDataSource,
					createItemBtnMsg: this.options.createItemBtnMsg
				}).setMessages(this.options.messages);
			}

			if (this.tableData != null) {
				datagrid.tableDataNodeRef = this.tableData.nodeRef;
			}
			datagrid.deleteMessageFunction = this.options.deleteMessageFunction;
			datagrid.draw();
		}
	}, true);
})();
