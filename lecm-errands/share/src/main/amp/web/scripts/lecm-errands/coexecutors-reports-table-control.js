/**
 * LogicECM root namespace.
 *
 * @namespace LogicECM
 */
// Ensure LogicECM root object exists
if (typeof LogicECM == "undefined" || !LogicECM) {
    LogicECM = {};
}

/**
 * LogicECM top-level module namespace.
 *
 * @namespace LogicECM
 * @class LogicECM.module
 */

LogicECM.errands = LogicECM.errands || {};

(function () {
    LogicECM.errands.CoexecutorsReportsTS = function (htmlId) {
        LogicECM.errands.CoexecutorsReportsTS.superclass.constructor.call(this, htmlId);
        return this;
    };

    YAHOO.extend(LogicECM.errands.CoexecutorsReportsTS, LogicECM.module.DocumentTable);

    YAHOO.lang.augmentObject(LogicECM.errands.CoexecutorsReportsTS.prototype, {
        createDataGrid: function () {
            var actions = [];
            var currentUser = {
                isExecutor: false,
                isCoexecutor: false,
                nodeRef: null
            };
            Alfresco.util.Ajax.jsonRequest(
                {
                    method: Alfresco.util.Ajax.GET,
                    url: Alfresco.constants.PROXY_URI + "lecm/errands/api/getCurrentEmployeeRoles?errandNodeRef=" + encodeURIComponent(this.options.documentNodeRef),
                    successCallback: {
                        fn: function (response) {
                            var roles = response.json;
                            if (roles) {
                                if (!this.options.disabled) {
                                    if (roles.isExecutor) {
                                        currentUser.isExecutor = true;
                                    }
                                    if (roles.isCoexecutor) {
                                        currentUser.isCoexecutor = true;
                                    }
                                }
                            }
                            Alfresco.util.Ajax.jsonGet({
                                url: Alfresco.constants.PROXY_URI + "lecm/orgstructure/api/getCurrentEmployee",
                                successCallback: {
                                    fn: function (response) {
                                        if (response && response.json.nodeRef) {
                                            currentUser.nodeRef = response.json.nodeRef;
                                            var currentDocumentStatus;
                                            Alfresco.util.Ajax.jsonPost({
                                                url: Alfresco.constants.PROXY_URI + "lecm/substitude/format/node",
                                                dataObj: {
                                                    nodeRef: this.options.documentNodeRef,
                                                    substituteString: "{lecm-statemachine:status}[SEPARATOR]{lecm-errands:executor-assoc-ref}[SEPARATOR]{lecm-errands:coexecutors-assoc-ref}"
                                                },
                                                successCallback: {
                                                    fn: function (response) {
                                                        if (response && response.json.formatString) {
                                                            var responseFields = response.json.formatString.split("[SEPARATOR]");
                                                            currentDocumentStatus = responseFields[0];
                                                            var executorAssoc = responseFields[1];
                                                            var coexecutorsAssoc = responseFields[2];
                                                            currentUser.isExecutor = currentUser.isExecutor && executorAssoc && executorAssoc.indexOf(currentUser.nodeRef) != -1;
                                                            currentUser.isCoexecutor = currentUser.isCoexecutor && coexecutorsAssoc && coexecutorsAssoc.indexOf(currentUser.nodeRef) != -1;
                                                            this.realCreateDatagrid(actions, currentUser, currentDocumentStatus);
                                                        }
                                                    },
                                                    scope: this
                                                },
                                                failureMessage: Alfresco.util.message("message.details.failure"),
                                                scope: this
                                            });
                                        }
                                    },
                                    scope: this
                                },
                                failureMessage: Alfresco.util.message("message.details.failure"),
                                scope: this
                            });

                        },
                        scope: this
                    },
                    failureMessage: Alfresco.util.message("message.details.failure"),
                    scope: this
                });

        },

        realCreateDatagrid: function (actions, currentUser, currentDocumentStatus) {
            if (this.tableData != null && this.tableData.rowType != null) {
                var defaultFilter, changedFilter;
                var filters = {
                    COMMON: '@lecm\\-errands\\-ts\\:coexecutor\\-report\\-status:"DECLINE" OR @lecm\\-errands\\-ts\\:coexecutor\\-report\\-status:"ACCEPT" OR (@lecm\\-errands\\-ts\\:coexecutor\\-report\\-status:"PROJECT" AND @lecm\\-errands\\-ts\\:coexecutor\\-assoc\\-ref:"' + currentUser.nodeRef + '")' +
                    ' OR @lecm\\-errands\\-ts\\:coexecutor\\-report\\-status:"ONCONTROL"',
                    DECLINED: '@lecm\\-errands\\-ts\\:coexecutor\\-report\\-status:"ACCEPT" OR (@lecm\\-errands\\-ts\\:coexecutor\\-report\\-status:"PROJECT" AND @lecm\\-errands\\-ts\\:coexecutor\\-assoc\\-ref:"' + currentUser.nodeRef + '")' +
                    ' OR @lecm\\-errands\\-ts\\:coexecutor\\-report\\-status:"ONCONTROL"',
                    OWN: '@lecm\\-errands\\-ts\\:coexecutor\\-assoc\\-ref:"' + currentUser.nodeRef + '"'
                };
                // Фильтры для текущего пользвателя
                if (currentUser.isCoexecutor && !currentUser.isExecutor) {
                    defaultFilter = filters.OWN;
                    changedFilter = filters.COMMON;
                    Dom.get(this.id + "-cntrl-change-filter-label").innerHTML = Alfresco.util.message("errands.label.showMy");
                    Dom.get(this.id + "-cntrl-change-filter").checked = true;
                } else {
                    defaultFilter = filters.DECLINED;
                    changedFilter = filters.COMMON;
                    Dom.get(this.id + "-cntrl-change-filter-label").innerHTML = Alfresco.util.message("errands.label.showDeclined");
                }

                var datagrid = new LogicECM.errands.CoexecutorsReportsDatagrid(this.options.containerId).setOptions({
                    usePagination: true,
                    showExtendSearchBlock: false,
                    formMode: this.options.mode,
                    actions: actions,
                    currentUser: currentUser,
                    currentDocumentStatus: currentDocumentStatus,
                    documentNodeRef: this.options.documentNodeRef,
                    datagridMeta: {
                        itemType: this.tableData.rowType,
                        datagridFormId: this.options.datagridFormId,
                        createFormId: "",
                        nodeRef: this.tableData.nodeRef,
                        parent: this.tableData.nodeRef,
                        actionsConfig: {
                            fullDelete: true
                        },
                        sort: "lecm-document:indexTableRow",
                        useChildQuery: false,
                        searchConfig: {
                            filter: defaultFilter
                        }
                    },
                    bubblingLabel: this.options.bubblingLabel,
                    showActionColumn: this.options.showActions,
                    showOtherActionColumn: false,
                    showCheckboxColumn: true,
                    attributeForShow: this.options.attributeForShow,
                    pageSize: this.tableData.pageSize != null && this.tableData.pageSize > 0 ? this.tableData.pageSize : 10,
                    useCookieForSort: false,
                    overrideSortingWith: this.options.isTableSortable,
                    refreshAfterCreate: this.options.refreshAfterCreate,
                    editFormTitleMsg: this.options.editFormTitleMsg,
                    createFormTitleMsg: this.options.createFormTitleMsg,
                    viewFormTitleMsg: this.options.viewFormTitleMsg,
                    expandable: this.options.expandable,
                    expandDataSource: this.options.expandDataSource
                }).setMessages(this.options.messages);
            }

            if (this.tableData != null) {
                datagrid.tableDataNodeRef = this.tableData.nodeRef;
            }
            datagrid.deleteMessageFunction = this.options.deleteMessageFunction;
            datagrid.draw();

            YAHOO.util.Event.on(this.id + "-cntrl-change-filter", "change", function () {
                var datagridMeta = datagrid.datagridMeta;
                if (datagridMeta.searchConfig.filter == defaultFilter) {
                    datagridMeta.searchConfig.filter = changedFilter;
                } else {
                    datagridMeta.searchConfig.filter = defaultFilter
                }
                datagrid.search.performSearch(datagridMeta);
            });
            YAHOO.Bubbling.on("onSearchSuccess", function (layer, args) {
                if (datagrid.options.bubblingLabel == args[1].bubblingLabel) {
                    YAHOO.Bubbling.fire("selectedItemsChanged", datagrid.options.bubblingLabel);
                }
            });
            //получаем кнопку переноса отчетов
            var transferSelectedReportsButton = Dom.get(this.id + "-cntrl-exec-report-transfer-coexecutors-reports");
            if (currentUser.isExecutor) {
                Dom.removeClass(transferSelectedReportsButton.parentElement, "hidden");
            }
            var buttonEl = YAHOO.util.Selector.query("span button", transferSelectedReportsButton, true);
            var isStatusOK = "На исполнении" == datagrid.options.currentDocumentStatus || "На доработке" == datagrid.options.currentDocumentStatus;

            YAHOO.Bubbling.on("selectedItemsChanged", function (layer, args) {
                if (datagrid.options.bubblingLabel == args[1]) {
                    var selectedRows = this.getSelectedItems();
                    var allItemsOk = false;
                    if (selectedRows && selectedRows.length) {
                        allItemsOk = selectedRows.every(function (row) {
                            return row.itemData["prop_lecm-errands-ts_coexecutor-report-status"].value == "ACCEPT";
                        });
                    }
                    if (allItemsOk && isStatusOK && datagrid.options.currentUser.isExecutor) {
                        YAHOO.util.Dom.removeClass(transferSelectedReportsButton, "disabled-button");
                        buttonEl.disabled = false;
                    } else {
                        YAHOO.util.Dom.addClass(transferSelectedReportsButton, "disabled-button");
                        buttonEl.disabled = true;
                    }
                }
            }, datagrid, true);

            YAHOO.util.Event.on(transferSelectedReportsButton, "click", function () {
                var selectedRows = this.getSelectedItems();
                if (selectedRows && selectedRows.length) {
                    var reportsRefs = selectedRows.map(function(row){
                        return row.nodeRef;
                    });
                    this.doReportsTransfer(reportsRefs);
                } else {
                    Alfresco.util.PopupManager.displayMessage({
                        text: Alfresco.util.message("lecm.errands.coexecutors.reports.msg.not.selected")
                    });
                }

            }, datagrid, true);

            this.datagrid = datagrid;
        }
    }, true);
})();

(function () {
    var Dom = YAHOO.util.Dom,
        Event = YAHOO.util.Event,
        Selector = YAHOO.util.Selector,
        Bubbling = YAHOO.Bubbling;

    LogicECM.errands.CoexecutorsReportsDatagrid = function (htmlId) {

        Bubbling.on("onActionTransferCoexecutorReport", function (layer, args) {
            this.onActionTransferCoexecutorReport({
                nodeRef: args[1].nodeRef
            });
        }, this, true);
        Bubbling.on("onCoexecutorReportUpdated", function (layer, args) {
            this._itemUpdate(args[1].nodeRef);
        }, this, true);
        Bubbling.on("onActionEditCoexecutorReport", function (layer, args) {
            this.onActionEdit(args[1].report);
        }, this, true);

        return LogicECM.errands.CoexecutorsReportsDatagrid.superclass.constructor.call(this, htmlId);
    };

    YAHOO.lang.extend(LogicECM.errands.CoexecutorsReportsDatagrid, LogicECM.module.DocumentTableDataGrid);

    YAHOO.lang.augmentObject(LogicECM.errands.CoexecutorsReportsDatagrid.prototype, {

        getExpandedFormId: function(record) {
            return this.id + encodeURIComponent(record.getData("nodeRef"));
        },

        getRowFormater: function () {
            var scope = this;

            return function (elTr, oRecord) {
                var status = oRecord.getData().itemData["prop_lecm-errands-ts_coexecutor-report-status"];
                if (status.value == "DECLINE") {
                    elTr.classList.add("declined");
                }
                return true;
            }
        },

        onActionTransferCoexecutorReport: function (me, asset, owner, actionsConfig, confirmFunction) {
            var nodeRef = arguments[0].nodeRef;
            if (nodeRef) {
                this.doReportsTransfer([nodeRef]);
            }
        },
        //подготовка данных для формы
        doReportsTransfer: function (reportsRefs) {
            Alfresco.util.Ajax.jsonRequest(
                {
                    method: Alfresco.util.Ajax.POST,
                    url: Alfresco.constants.PROXY_URI + "lecm/errands/coexecutorReport/transfer",
                    dataObj: reportsRefs,
                    successCallback: {
                        fn: function (response) {
                            var me = response.config.scope;
                            if (response.json.formData) {
                                me.processExecutionReport(response.json.formData);
                            } else {
                                Alfresco.util.PopupManager.displayMessage(
                                    {
                                        text: me.msg("message.details.failure")
                                    });
                            }
                        }
                    },
                    failureMessage: Alfresco.util.message("message.details.failure"),
                    scope: this
                });
        },
        //создание/редактирование отчета исполнителя
        processExecutionReport: function(formData, reportsRefs) {
            var formConnections = formData.formConnections;
            var formAttachments = formData.formAttachments;
            var formText = formData.formText;
            var formArgs = {
                'prop_lecm-errands-ts_execution-report-text': formText,
                'assoc_lecm-errands-ts_execution-report-attachment-assoc': formAttachments,
                'executionConnectedDocs': formConnections
            };
            var nodeRef = this.options.documentNodeRef;
            if (nodeRef) {
                var formId = "edit-execution-report";
                var executionReportDialog = new Alfresco.module.SimpleDialog(this.id + '-' + formId);
                executionReportDialog.setOptions({
                    nodeRef: nodeRef,
                    templateUrl: Alfresco.constants.URL_SERVICECONTEXT + 'lecm/components/form',
                    actionUrl: Alfresco.constants.PROXY_URI_RELATIVE + 'lecm/errands/executionReport/process?nodeRef=' + nodeRef,
                    templateRequestParams: {
                        formId: "executionReportForm",
                        itemKind: "type",
                        itemId: "lecm-errands-ts:execution-report",
                        mode: "create",
                        showCancelButton: true,
                        showCaption: false,
                        submitType: 'json',
                        args: JSON.stringify(formArgs)
                    },
                    width: '60em',
                    destroyOnHide: true,
                    doBeforeDialogShow: {
                        fn: function (form, simpleDialog) {
                            simpleDialog.dialog.setHeader(this.msg("label.execution.report.form.title"));
                            simpleDialog.dialog.subscribe('destroy', function (event, args, params) {
                                LogicECM.module.Base.Util.destroyForm(simpleDialog.id);
                                LogicECM.module.Base.Util.formDestructor(event, args, params);
                            }, {moduleId: simpleDialog.id}, this);
                        },
                        scope: this
                    },
                    onSuccess: {
                        fn: function (response) {
                            window.location.reload();
                        }
                    },
                    onFailure: {
                        fn: function (response) {
                            Alfresco.util.PopupManager.displayMessage(
                                {
                                    text: Alfresco.util.message("message.details.failure")
                                });
                            executionReportDialog.hide();
                        }
                    }
                });
                executionReportDialog.show();
            }
        }
    }, true)

})();
