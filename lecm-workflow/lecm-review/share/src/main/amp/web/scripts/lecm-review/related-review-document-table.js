/* global YAHOO, Alfresco */

if (typeof LogicECM == 'undefined' || !LogicECM) {
    LogicECM = {};
}

LogicECM.module = LogicECM.module || {};
LogicECM.module.Review = LogicECM.module.Review || {};

(function () {
    var Bubbling = YAHOO.Bubbling;

    LogicECM.module.Review.RelatedDocumentTable = function (containerId, documentNodeRef) {
        LogicECM.module.Review.RelatedDocumentTable.superclass.constructor.call(this, containerId);
        this.name = 'LogicECM.module.Review.RelatedDocumentTable';
        this.documentNodeRef = documentNodeRef;
        return this;
    };

    YAHOO.lang.extend(LogicECM.module.Review.RelatedDocumentTable, LogicECM.module.DocumentTable, {
        printReportButton: null,
        rejectReviewButton: null,
        documentNodeRef: null,

        onReady: function () {
            this.createDataGrid();
        },

        actionCancelRelativeReviewEvaluator: function (rowData) {
            var state = rowData.itemData['prop_lecm-review-ts_review-state'],
                username = rowData.itemData['prop_lecm-review-ts_initiator-username'];

            return 'NOT_REVIEWED' === state.value && Alfresco.constants.USERNAME === username.value;
        },

        onActionCancelRelativeReview: function (rowData) {
            Alfresco.util.Ajax.jsonPost({
                url: Alfresco.constants.PROXY_URI + 'lecm/workflow/review/cancelRelativeReview',
                dataObj: {
                    nodeRef: rowData.nodeRef,
                    initiatingDocumentRef: this.documentNodeRef
                },
                successCallback: {
                    scope: this,
                    fn: function (response) {
                        if (response && response.json && response.json.success) {
                            Alfresco.util.PopupManager.displayMessage({
                                text: this.msg('message.details.success')
                            });

                            this.options.currentValue = this.options.currentValue.split(",").filter(function (item) {
                                return item != rowData.nodeRef;
                            }).join();
                            this.dataGrid.datagridMeta.searchConfig.filter = this.getSearchConfigFilter();

                            Bubbling.fire("dataItemsDeleted",
                                {
                                    items: [rowData],
                                    bubblingLabel: this.options.bubblingLabel
                                });
                        }
                    }
                },
                failureMessage: this.msg('message.failure')
            });
        },

        createDataGrid: function () {
            var actions = [];
            var actionType = "datagrid-action-link-" + this.options.bubblingLabel;
            if (!this.options.disabled && this.options.mode == "edit") {
                actions.push({
                    type: actionType,
                    id: 'onActionCancelReview',
                    permission: 'edit',
                    label: this.msg('title.reject'),
                    evaluator: this.actionCancelRelativeReviewEvaluator
                });
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
                    itemType: this.options.itemType,
                    datagridFormId: this.options.datagridFormId,
                    createFormId: "",
                    actionsConfig: {
                        fullDelete: true
                    },
                    sort: this.options.sort ? this.options.sort : "lecm-document:indexTableRow",
                    useChildQuery: false,
                    searchConfig: {
                        filter: this.getSearchConfigFilter()
                    }
                },
                bubblingLabel: this.options.bubblingLabel,
                showActionColumn: this.options.showActions,
                showOtherActionColumn: true,
                showCheckboxColumn: false,
                attributeForShow: this.options.attributeForShow,
                pageSize: 10,
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

            datagrid.deleteMessageFunction = this.options.deleteMessageFunction;
            datagrid.onActionCancelReview = this.onActionCancelRelativeReview.bind(this);
            datagrid.draw();
        },

        getSearchConfigFilter: function () {
            var result = "";
            if (this.options.currentValue && this.options.currentValue.length) {
                var items = this.options.currentValue.split(",");
                for (var item in items) {
                    if (items[item]) {
                        if (result.length) {
                            result += " OR ";
                        }
                        result += "ID:" + items[item].replace(":", "\\:");
                    }
                }
            }
            if (!result) {
                result += "ID:\"NOT_REF\"";
            }
            return result;
        }
    }, true);
})();
