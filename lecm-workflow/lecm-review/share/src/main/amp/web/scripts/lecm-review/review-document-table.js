/* global YAHOO, Alfresco */

if (typeof LogicECM == 'undefined' || !LogicECM) {
	LogicECM = {};
}

LogicECM.module = LogicECM.module || {};
LogicECM.module.Review = LogicECM.module.Review || {};

(function () {
	var Bubbling = YAHOO.Bubbling;

	LogicECM.module.Review.DocumentTable = function (containerId, documentNodeRef) {
		this.documentNodeRef = documentNodeRef;

		this.printReportButton = YAHOO.util.Dom.get(containerId + '-printReviewReport');
		if (this.printReportButton) {
			YAHOO.util.Event.on(this.printReportButton, 'click', function () {
				LogicECM.module.Base.Util.printReport(this.documentNodeRef, this.options.reportId);
			}, this, true);
		}

		this.rejectReviewButton = YAHOO.util.Dom.get(containerId + '-rejectReview');
		if (this.rejectReviewButton) {
			YAHOO.util.Event.on(this.rejectReviewButton, 'click', this.rejectReview, this, true);
		}

		LogicECM.module.Review.DocumentTable.superclass.constructor.call(this, containerId);
		this.name = 'LogicECM.module.Review.DocumentTable';
		return this;
	};

	YAHOO.lang.extend(LogicECM.module.Review.DocumentTable, LogicECM.module.DocumentTable, {
		printReportButton: null,
		rejectReviewButton: null,
		documentNodeRef: null,

		actionCancelReviewEvaluator: function (rowData) {
			var state = rowData.itemData['prop_lecm-review-ts_review-state'],
				username = rowData.itemData['prop_lecm-review-ts_initiator-username'],
                initiatingDocuments = rowData.itemData['prop_lecm-review-ts_initiating-documents'];

			return 'NOT_REVIEWED' === state.value && Alfresco.constants.USERNAME === username.value && !initiatingDocuments.value.length;
		},

		rejectReview: function () {
			function onSuccess (successResponse) {
				var canceled = successResponse.json.canceled,
					msg = canceled ? 'Ознакомление успешно отозвано' : 'Не найдено ознакамливающихся для отзыва';
				Bubbling.fire('datagridRefresh', {
					bubblingLabel: this.options.bubblingLabel
				});
				Alfresco.util.PopupManager.displayMessage({
					text: msg
				});
			}

			Alfresco.util.Ajax.jsonPost({
				url: Alfresco.constants.PROXY_URI_RELATIVE + 'lecm/workflow/review/cancelReview/all',
				dataObj: {
					documentRef: this.documentNodeRef
				},
				successCallback: {
					scope: this,
					fn: onSuccess
				},
				failureMessage: this.msg('message.failure')
			});
		},

		onActionCancelReview: function (rowData, target, actionsConfig, confirmFunction) {

			function onSuccessItemUpdate (successResponse) {
				Alfresco.util.PopupManager.displayMessage({
					text: this.msg('message.details.success')
				});
				Bubbling.fire('dataItemUpdated', {
					item: successResponse.json.item,
					bubblingLabel: this.options.bubblingLabel
				});
			}

			function onSuccessCancelReview (successResponse) {
				var nodeRef =  new Alfresco.util.NodeRef(successResponse.json.persistedObject);
				Alfresco.util.Ajax.jsonPost({
					url: Alfresco.constants.PROXY_URI_RELATIVE + 'lecm/base/item/node/' + nodeRef.uri,
					dataObj: this._buildDataGridParams(),
					successCallback: {
						scope: this,
						fn: onSuccessItemUpdate
					},
					failureMessage: this.msg('message.failure')
				});
			}

			Alfresco.util.Ajax.jsonPost({
				url: Alfresco.constants.PROXY_URI + 'lecm/workflow/review/cancelReview',
				dataObj: {
					nodeRef: rowData.nodeRef,
					documentRef: Alfresco.util.getQueryStringParameter('nodeRef')
				},
				successCallback: {
					scope: this,
					fn: onSuccessCancelReview
				},
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
					excludeColumns: ['lecm-review-ts:initiator-username', 'lecm-review-ts:initiating-documents'],
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
				datagrid.onActionCancelReview = this.onActionCancelReview;
				datagrid.draw();
			}
		}
	}, true);
})();
