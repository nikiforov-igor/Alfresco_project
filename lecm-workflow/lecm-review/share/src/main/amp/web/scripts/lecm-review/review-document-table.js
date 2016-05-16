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

		actionCancelReviewEvaluator: function (rowData) {
			var state = rowData.itemData['prop_lecm-review-info_review-state'],
				username = rowData.itemData['prop_lecm-review-info_initiator-username'];

			return 'NOT_REVIEWED' === state.value && Alfresco.constants.USERNAME === username.value;
		},

		onActionPrintReview: function (rowData, target, actionsConfig, confirmFunction) {
			// если отчет будет строиться по какому-то одному ознакомлению
			// LogicECM.module.Base.Util.printReport(rowData.nodeRef, 'ИД отчета');
			// если отчет будет строиться по всем ознакомлениям сразу
			// LogicECM.module.Base.Util.printReport(this.datagridMeta.nodeRef, 'ИД отчета');
		},

		onActionCancelReview: function (rowData, target, actionsConfig, confirmFunction) {

			Alfresco.util.Ajax.jsonPost({
				url: Alfresco.constants.PROXY_URI + '',
				dataObj: {
					nodeRef: rowData.nodeRef
				},
				successMessage: this.msg('message.save.success'),
				failureMessage: this.msg('message.failure')
			});
		},

		createDataGrid: function() {
			if (this.tableData && this.tableData.rowType) {
				var actions = [];
				var actionType = "datagrid-action-link-" + this.options.bubblingLabel;
				if (!this.options.disabled && this.options.mode == "edit") {
					actions.push({
						type: actionType,
						id: 'onActionPrintReview',
						permission: 'edit',
						label: this.msg('Печать листа ознакомления')
					});
					actions.push({
						type: actionType,
						id: 'onActionCancelReview',
						permission: 'edit',
						label: this.msg('Отозвать с ознакомления'),
						evaluator: this.actionCancelReviewEvaluator
					});
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

				var datagrid = new LogicECM.module.DocumentTableDataGrid(this.options.containerId).setOptions({
					excludeColumns: ['lecm-review-info:initiator-username'],
					usePagination: true,
					showExtendSearchBlock: false,
					formMode: this.options.mode,
					actions: actions,
					splitActionsAt: actions.length,
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
					pageSize: this.tableData.pageSize ? this.tableData.pageSize : 10,
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

				datagrid.tableDataNodeRef = this.tableData.nodeRef;
				datagrid.deleteMessageFunction = this.options.deleteMessageFunction;
				datagrid.onActionPrintReview = this.onActionPrintReview;
				datagrid.onActionCancelReview = this.onActionCancelReview;
				datagrid.draw();
			}
		}
	}, true);
})();
