if (typeof LogicECM == "undefined" || !LogicECM) {
    LogicECM = {};
}

LogicECM.errands = LogicECM.errands || {};

(function () {
    LogicECM.errands.ExecutionReportsTS = function (htmlId) {
        LogicECM.errands.ExecutionReportsTS.superclass.constructor.call(this, htmlId);
        return this;
    };

    YAHOO.extend(LogicECM.errands.ExecutionReportsTS, LogicECM.module.DocumentTable);

    YAHOO.lang.augmentObject(LogicECM.errands.ExecutionReportsTS.prototype, {
        filter: "",
        onReady: function () {
            var gridBlock = Dom.get(this.options.containerId + "-grid-block");

            YAHOO.util.Event.on(this.id + "-cntrl-show-previous-reports", "change", function () {
                if (this.checked) {
                    Dom.removeClass(gridBlock, "hidden1");
                } else {
                    Dom.addClass(gridBlock, "hidden1");
                }
            });
            if (this.options.showPreviousReports) {
                Dom.get(this.id + "-cntrl-show-previous-reports").checked = true;
            }

            if (this.options.documentNodeRef && this.options.documentNodeRef.search("SpacesStore") != -1) {
                Alfresco.util.Ajax.jsonPost({
                    url: Alfresco.constants.PROXY_URI + "lecm/substitude/format/node",
                    dataObj: {
                        nodeRef: this.options.documentNodeRef,
                        substituteString: "{lecm-errands:execution-report-status},{lecm-errands:project-report-ref}"
                    },
                    successCallback: {
                        fn: function (response) {
                            if (response && response.json.formatString) {
                                var resp = response.json.formatString.split(",");
                                var currentReportStatus = resp[0];
                                var reportRef = resp[1];
                                if (currentReportStatus == "Отклонен" && reportRef) {
                                    this.filter = "@lecm\\-errands\\-ts:execution\\-report\\-status:\"DECLINE\" AND NOT ID:\"" + reportRef + "\"";
                                }
                            }
                        },
                        scope: this
                    },
                    failureMessage: Alfresco.util.message("message.details.failure"),
                    scope: this
                });
            }

            this.loadTableData();
        },

        createDataGrid: function () {
            if (this.tableData && this.tableData.rowType) {
                var actions = [];
                var searchFilter = this.filter;
                var datagrid = new LogicECM.module.DocumentTableDataGrid(this.options.containerId).setOptions({
                    usePagination: true,
                    showExtendSearchBlock: false,
                    formMode: this.options.mode,
                    actions: actions,
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
                        useChildQuery: false,
                        searchConfig: {
                            filter: searchFilter
                        }
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
            }
            if (this.tableData) {
                datagrid.tableDataNodeRef = this.tableData.nodeRef;
            }
            datagrid.deleteMessageFunction = this.options.deleteMessageFunction;
            datagrid.draw();
        }

    }, true);
})();