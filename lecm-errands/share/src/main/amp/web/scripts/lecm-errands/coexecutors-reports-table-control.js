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
                            var me = response.config.scope;
                            var roles = response.json;
                            if (roles) {
                                if (!me.options.disabled) {
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
                                        var me = response.config.scope;
                                        if (response && response.json.nodeRef) {
                                            currentUser.nodeRef = response.json.nodeRef;
                                            var currentDocumentStatus;
                                            Alfresco.util.Ajax.jsonPost({
                                                url: Alfresco.constants.PROXY_URI + "lecm/substitude/format/node",
                                                dataObj: {
                                                    nodeRef: me.options.documentNodeRef,
                                                    substituteString: "{lecm-statemachine:status}"
                                                },
                                                successCallback: {
                                                    fn: function (response) {
                                                        var me = response.config.scope;
                                                        if (response && response.json.formatString) {
                                                            currentDocumentStatus = response.json.formatString;
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
                    Dom.get(this.id + "-cntrl-change-filter-label").innerHTML = Alfresco.util.message("errands.label.showAll");
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
                var filter = defaultFilter;
                if (this.checked) {
                    filter = changedFilter;
                }
                var datagridMeta = datagrid.datagridMeta;
                datagridMeta.searchConfig.filter = filter;
                datagrid.search.performSearch(datagridMeta);
            });

            //получаем кнопку переноса отчетов
            var coexecutorsReportElementId = Dom.get(this.id).parentElement.parentElement.parentElement.id;
            var formTemplateString = coexecutorsReportElementId.substring(0, coexecutorsReportElementId.indexOf("-coexecutors-reports", 0));
            var transferSelectedReportsButton = Dom.get(formTemplateString + "-exec-report-transfer-coexecutors-reports");
            //скрываем кнопку переноса отчетов если поручение в неподходящих статусах.
            var isStatusOK = "На исполнении" == datagrid.options.currentDocumentStatus || "На доработке" == datagrid.options.currentDocumentStatus;
            if (!isStatusOK || !datagrid.options.currentUser.isExecutor) {
                YAHOO.util.Dom.setStyle(transferSelectedReportsButton, "display", "none");
            }

            YAHOO.util.Event.on(transferSelectedReportsButton, "click", function () {
                var selectedRows = this.getSelectedItems();
                if (selectedRows && selectedRows.length) {
                    //проверка на статусы выбранных отчетов
                    var allItemsOk = true;
                    var i, reportsRefs = [];
                    for (i = 0; i < selectedRows.length; i++) {
                        var reportStatus = selectedRows[i].itemData["prop_lecm-errands-ts_coexecutor-report-status"];
                        if (reportStatus.value != "ACCEPT") {
                            Alfresco.util.PopupManager.displayMessage({
                                text: Alfresco.util.message("lecm.errands.coexecutors.reports.msg.wrong.report")
                            });
                            allItemsOk = false;
                            break;
                        } else {
                            reportsRefs.push(selectedRows[i].nodeRef);
                        }
                    }
                    //если все очтеты в статусе Принят - переносим
                    if (allItemsOk) {
                        this.doReportsTransfer(reportsRefs);
                    }
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
        doReportsTransfer: function (reportsRefs) {
            Alfresco.util.Ajax.jsonRequest(
                {
                    method: Alfresco.util.Ajax.POST,
                    url: Alfresco.constants.PROXY_URI + "lecm/errands/coexecutorReport/transfer",
                    dataObj: reportsRefs,
                    successCallback: {
                        fn: function (response) {
                            var me = response.config.scope;
                            if (response.json.success) {
                                reportsRefs.forEach(function (nodeRef) {
                                    me._itemUpdate(nodeRef);
                                });
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
