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
            var actionType = "datagrid-action-link-" + this.options.bubblingLabel;
            Alfresco.util.Ajax.jsonRequest(
                {
                    method: Alfresco.util.Ajax.GET,
                    url: Alfresco.constants.PROXY_URI + "lecm/errands/api/getCurrentEmployeeRoles?errandNodeRef=" + encodeURIComponent(this.options.documentNodeRef),
                    successCallback: {
                        fn: function (response) {
                            var me = response.config.scope;
                            var roles = response.json;
                            if (roles) {
                                if (!me.options.disabled) {
                                    if (roles.isExecutor) {
                                        actions.push({
                                            type: actionType,
                                            id: "onActionAcceptCoexecutorReport",
                                            permission: "edit",
                                            label: me.msg("actions.coexecutor.report.accept"),
                                            evaluator: me.showActionsEvaluator
                                        });
                                        actions.push({
                                            type: actionType,
                                            id: "onActionDeclineCoexecutorReport",
                                            permission: "edit",
                                            label: me.msg("actions.coexecutor.report.decline"),
                                            evaluator: me.showActionsEvaluator
                                        });
                                        actions.push({
                                            type: actionType,
                                            id: "onActionTransferCoexecutorReport",
                                            permission: "edit",
                                            label: me.msg("actions.coexecutor.report.transfer"),
                                            evaluator: me.showTransferActionEvaluator
                                        });
                                    }
                                    if (roles.isCoexecutor) {
                                        actions.push({
                                            type: actionType,
                                            id: "onActionEdit",
                                            permission: "edit",
                                            label: me.msg("actions.edit"),
                                            evaluator: me.editActionEvaluator
                                        });
                                    }
                                }
                            }
                            var currentUser;
                            Alfresco.util.Ajax.jsonGet({
                                url: Alfresco.constants.PROXY_URI + "/lecm/orgstructure/api/getCurrentEmployee",
                                successCallback: {
                                    fn: function (response) {
                                        var me = response.config.scope;
                                        if (response && response.json.nodeRef) {
                                            currentUser = response.json.nodeRef;
                                            var currentDocumentStatus;
                                            Alfresco.util.Ajax.jsonGet({
                                                url: Alfresco.constants.PROXY_URI + "/lecm/errands/api/getDocumentCurrentStatus",
                                                dataObj: {
                                                    "nodeRef": me.options.documentNodeRef
                                                },
                                                successCallback: {
                                                    fn: function (response) {
                                                        var me = response.config.scope;
                                                        if (response && response.json.status) {
                                                            currentDocumentStatus = response.json.status;
                                                            me.realCreateDatagrid(actions, currentUser, currentDocumentStatus);
                                                        }
                                                    }
                                                },
                                                failureCallback: {
                                                    fn: function (response) {
                                                        var me = response.config.scope;
                                                        Alfresco.util.PopupManager.displayMessage(
                                                            {
                                                                text: me.msg("message.details.failure")
                                                            });
                                                    }
                                                },
                                                scope: me
                                            });
                                        }
                                    }
                                },
                                failureCallback: {
                                    fn: function (response) {
                                        var me = response.config.scope;
                                        Alfresco.util.PopupManager.displayMessage(
                                            {
                                                text: me.msg("message.details.failure")
                                            });
                                    }
                                },
                                scope: me
                            });

                        }
                    },
                    failureCallback: {
                        fn: function (response) {
                            var me = response.config.scope;
                            Alfresco.util.PopupManager.displayMessage(
                                {
                                    text: me.msg("message.details.failure")
                                });
                        }
                    },
                    scope: this
                });

        },

        realCreateDatagrid: function (actions, currentUser, currentDocumentStatus) {
            if (this.tableData != null && this.tableData.rowType != null) {

                var datagrid = new LogicECM.errands.CoexecutorsReportsDatagrid(this.options.containerId).setOptions({
                    usePagination: true,
                    showExtendSearchBlock: false,
                    formMode: this.options.mode,
                    actions: actions,
                    currentUser: currentUser,
                    currentDocumentStatus: currentDocumentStatus,
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
                            filter: 'NOT @lecm\\-errands\\-ts\\:coexecutor\\-report\\-status:"DECLINE" AND NOT (@lecm\\-errands\\-ts\\:coexecutor\\-report\\-status:"PROJECT" AND NOT @lecm\\-errands\\-ts\\:coexecutor\\-assoc\\-ref:"' + currentUser + '")'
                        }
                    },
                    bubblingLabel: this.options.bubblingLabel,
                    showActionColumn: this.options.showActions,
                    showOtherActionColumn: true,
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
                    expandDataSource: this.options.expandDataSource,
                    excludeColumns: ["lecm-errands-ts:coexecutor-report-is-transferred"]
                }).setMessages(this.options.messages);
            }

            if (this.tableData != null) {
                datagrid.tableDataNodeRef = this.tableData.nodeRef;
            }
            datagrid.deleteMessageFunction = this.options.deleteMessageFunction;
            datagrid.draw();

            YAHOO.util.Event.on(this.id + "-cntrl-show-declined", "change", function () {
                var filter = 'NOT @lecm\\-errands\\-ts\\:coexecutor\\-report\\-status:"DECLINE" AND NOT (@lecm\\-errands\\-ts\\:coexecutor\\-report\\-status:"PROJECT" AND NOT @lecm\\-errands\\-ts\\:coexecutor\\-assoc\\-ref:"' + currentUser + '")';
                if (this.checked) {
                    filter = '@lecm\\-errands\\-ts\\:coexecutor\\-report\\-status:"DECLINE" OR @lecm\\-errands\\-ts\\:coexecutor\\-report\\-status:"ACCEPT" OR (@lecm\\-errands\\-ts\\:coexecutor\\-report\\-status:"PROJECT" AND @lecm\\-errands\\-ts\\:coexecutor\\-assoc\\-ref:"' + currentUser + '")' +
                        'OR @lecm\\-errands\\-ts\\:coexecutor\\-report\\-status:"ONCONTROL"';

                }
                var datagridMeta = datagrid.datagridMeta;
                datagridMeta.searchConfig.filter = filter;
                datagrid.search.performSearch(datagridMeta);
            });

            this.datagrid = datagrid;
        },

        showActionsEvaluator: function (rowData) {
            var status = rowData.itemData["prop_lecm-errands-ts_coexecutor-report-status"];

            return status != null && status.value == "ONCONTROL";
        },
        showTransferActionEvaluator: function (rowData) {
            var status = rowData.itemData["prop_lecm-errands-ts_coexecutor-report-status"];
            var isTransferred = rowData.itemData["prop_lecm-errands-ts_coexecutor-report-is-transferred"];
            var isStatusOK = "На исполнении" == this.options.currentDocumentStatus || "На доработке" == this.options.currentDocumentStatus;
            return status != null && status.value == "ACCEPT" && !isTransferred.value && isStatusOK;
        },
        editActionEvaluator: function (rowData) {
            var status = rowData.itemData["prop_lecm-errands-ts_coexecutor-report-status"];
            var coexecutor = rowData.itemData["assoc_lecm-errands-ts_coexecutor-assoc"];
            return status != null && status.value == "PROJECT" && coexecutor.value == this.options.currentUser;
        }
    }, true);
})();

(function () {
    var Dom = YAHOO.util.Dom,
        Event = YAHOO.util.Event,
        Selector = YAHOO.util.Selector,
        Bubbling = YAHOO.Bubbling;

    LogicECM.errands.CoexecutorsReportsDatagrid = function (htmlId) {
        return LogicECM.errands.CoexecutorsReportsDatagrid.superclass.constructor.call(this, htmlId);
    };

    YAHOO.lang.extend(LogicECM.errands.CoexecutorsReportsDatagrid, LogicECM.module.DocumentTableDataGrid);

    YAHOO.lang.augmentObject(LogicECM.errands.CoexecutorsReportsDatagrid.prototype, {

        onActionAcceptCoexecutorReport: function (me, asset, owner, actionsConfig, confirmFunction) {
            var nodeRef = arguments[0].nodeRef;
            if (nodeRef != null) {
                Alfresco.util.Ajax.jsonRequest(
                    {
                        method: Alfresco.util.Ajax.GET,
                        url: Alfresco.constants.PROXY_URI + "lecm/errands/coexecutorReport/accept?nodeRef=" + nodeRef,
                        successCallback: {
                            fn: function (response) {
                                var me = response.config.scope;
                                if (response.json.success) {
                                    me._itemUpdate(nodeRef);
                                } else {
                                    Alfresco.util.PopupManager.displayMessage(
                                        {
                                            text: me.msg("message.details.failure")
                                        });
                                }
                            }
                        },
                        failureCallback: {
                            fn: function (response) {
                                var me = response.config.scope;
                                Alfresco.util.PopupManager.displayMessage(
                                    {
                                        text: me.msg("message.details.failure")
                                    });
                            }
                        },
                        scope: this
                    });
            }
        },

        onActionDeclineCoexecutorReport: function (me, asset, owner, actionsConfig, confirmFunction) {
            var nodeRef = arguments[0].nodeRef;
            if (nodeRef != null) {
                Alfresco.util.Ajax.jsonRequest(
                    {
                        method: Alfresco.util.Ajax.GET,
                        url: Alfresco.constants.PROXY_URI + "lecm/errands/coexecutorReport/decline?nodeRef=" + nodeRef,
                        successCallback: {
                            fn: function (response) {
                                var me = response.config.scope;
                                if (response.json.success) {
                                    me._itemUpdate(nodeRef);
                                } else {
                                    Alfresco.util.PopupManager.displayMessage(
                                        {
                                            text: me.msg("message.details.failure")
                                        });
                                }
                            }
                        },
                        failureCallback: {
                            fn: function (response) {
                                var me = response.config.scope;
                                Alfresco.util.PopupManager.displayMessage(
                                    {
                                        text: me.msg("message.details.failure")
                                    });
                            }
                        },
                        scope: this
                    });
            }
        },
        onActionTransferCoexecutorReport: function (me, asset, owner, actionsConfig, confirmFunction) {
            var nodeRef = arguments[0].nodeRef;
            if (nodeRef != null) {
                Alfresco.util.Ajax.jsonRequest(
                    {
                        method: Alfresco.util.Ajax.POST,
                        url: Alfresco.constants.PROXY_URI + "lecm/errands/coexecutorReport/transfer",
                        dataObj: [nodeRef],
                        successCallback: {
                            fn: function (response) {
                                var me = response.config.scope;
                                if (response.json.success) {
                                    me._itemUpdate(nodeRef);
                                    me.updateExecutorReport(response.json.data);
                                } else {
                                    Alfresco.util.PopupManager.displayMessage(
                                        {
                                            text: me.msg("message.details.failure")
                                        });
                                }
                            }
                        },
                        failureCallback: {
                            fn: function (response) {
                                var me = response.config.scope;
                                Alfresco.util.PopupManager.displayMessage(
                                    {
                                        text: me.msg("message.details.failure")
                                    });
                            }
                        },
                        scope: this
                    });
            }
        },
        //обновление форм отчета исполнителя
        updateExecutorReport: function (data) {
            var transferedReportsData = data.items;
            //получаем формы работы над поручением исполнителя через форму отчетов соисполнителей
            var coexecutorsReportElementId = Dom.get(this.id + "-body").parentElement.parentElement.id;
            var formTemplateString = coexecutorsReportElementId.substring(0, coexecutorsReportElementId.indexOf("-coexecutors-reports", 0));

            var executorReportDomElement = Dom.get(formTemplateString + "-exec");
            //Почему-то текст отчета не в блоке  ..-exec-report, а рядом с ним
            var executorReportRichTextFrame = Selector.query(".control.richtext.editmode .value-div iframe ", executorReportDomElement, true);
            if (executorReportRichTextFrame) {
                var executorReportTextElement = Selector.query("body", executorReportRichTextFrame.contentDocument, true);
            }
            var executorReportAttachmentsDomElement = Dom.get(formTemplateString + "-exec-attachments");
            var executorReportConnectionsDomElement = Dom.get(formTemplateString + "-exec-connections");
            var executorReportAttachmentsULElement = Selector.query("ul.data-list", executorReportAttachmentsDomElement, true);
            var executorReportConnectionsULElement = Selector.query("ul.data-list", executorReportConnectionsDomElement, true);
            var executorReportAttachmentsCountDomElement = Selector.query(".count", executorReportAttachmentsDomElement, true);
            var executorReportConnectionsCountDomElement = Selector.query(".count", executorReportConnectionsDomElement, true);


            //добавляем перенесенные значения
            for (var i = 0; i < transferedReportsData.length; i++) {
                //заполняем текст отчета
                if (executorReportTextElement) {
                    executorReportTextElement.innerHTML += transferedReportsData[i].reportText;
                }
                var liElementTemplate = "<li title={name}>" +
                    "<img src='/share/res/components/images/filetypes/{fileIcon}' class='file-icon'/> " +
                    "<a href={link}>{name}</a></li>";
                //добавляем вложения
                if (executorReportAttachmentsULElement) {
                    var attachments = transferedReportsData[i].attachments;
                    attachments.forEach(function (a) {
                        var fileIcon = Alfresco.util.getFileIcon(a.name, "cm:content", 16);
                        var attachmentEl = YAHOO.lang.substitute(liElementTemplate, {
                            fileIcon: fileIcon,
                            name: a.name,
                            link: a.link
                        });
                        executorReportAttachmentsULElement.innerHTML += attachmentEl;
                    });
                }
                //добавляем связанные документы
                if (executorReportConnectionsULElement) {
                    var connections = transferedReportsData[i].connections;
                    var fileIcon = "generic-file-16.png";
                    connections.forEach(function (c) {
                        var connectionEl = YAHOO.lang.substitute(liElementTemplate, {
                            fileIcon: fileIcon,
                            name: c.name,
                            link: c.link
                        });
                        executorReportConnectionsULElement.innerHTML += connectionEl;
                    });
                }
            }
            //обновляем каунтеры
            if (executorReportAttachmentsCountDomElement) {
                executorReportAttachmentsCountDomElement.innerHTML = " (" + executorReportAttachmentsULElement.children.length + ")";
            }
            if (executorReportConnectionsCountDomElement) {
                executorReportConnectionsCountDomElement.innerHTML = " (" + executorReportConnectionsULElement.children.length + ")";
            }
        }
    }, true)

})();
