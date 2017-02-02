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
                                        currentUser.isExecutor = true;
                                    }
                                    if (roles.isCoexecutor) {
                                        actions.push({
                                            type: actionType,
                                            id: "onActionEdit",
                                            permission: "edit",
                                            label: me.msg("actions.edit"),
                                            evaluator: me.editActionEvaluator
                                        });
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
                    showExpandActionsColumn: this.options.showExpandActions,
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
        },

        showActionsEvaluator: function (rowData) {
            var status = rowData.itemData["prop_lecm-errands-ts_coexecutor-report-status"];

            return status != null && status.value == "ONCONTROL";
        },
        showTransferActionEvaluator: function (rowData) {
            var reportStatus = rowData.itemData["prop_lecm-errands-ts_coexecutor-report-status"];
            var isDocumentStatusOK = "На исполнении" == this.options.currentDocumentStatus || "На доработке" == this.options.currentDocumentStatus;
            return reportStatus && reportStatus.value == "ACCEPT" && isDocumentStatusOK;
        },
        editActionEvaluator: function (rowData) {
            var status = rowData.itemData["prop_lecm-errands-ts_coexecutor-report-status"];
            var coexecutor = rowData.itemData["assoc_lecm-errands-ts_coexecutor-assoc"];
            return status != null && status.value == "PROJECT" && coexecutor.value == this.options.currentUser.nodeRef;
        }
    }, true);
})();

(function () {
    var Dom = YAHOO.util.Dom,
        Event = YAHOO.util.Event,
        Selector = YAHOO.util.Selector,
        Bubbling = YAHOO.Bubbling,
        Substitute = YAHOO.lang.substitute;

    LogicECM.errands.CoexecutorsReportsDatagrid = function (htmlId) {
        return LogicECM.errands.CoexecutorsReportsDatagrid.superclass.constructor.call(this, htmlId);
    };

    YAHOO.lang.extend(LogicECM.errands.CoexecutorsReportsDatagrid, LogicECM.module.DocumentTableDataGrid);

    YAHOO.lang.augmentObject(LogicECM.errands.CoexecutorsReportsDatagrid.prototype, {
        expandActionTypePrefix: "datagrid-expand-action-link",
        getExpandedFormId: function(record) {
            return this.id + encodeURIComponent(record.getData("nodeRef"));
        },

       /* addExpandedRow: function (record, text) {
            var me = this;
            var colSpan = this.datagridColumns.length;
            if (this.options.showCheckboxColumn) {
                colSpan++;
            }
            if (this.options.expandable) {
                colSpan++;
            }
            if (this.options.showActionColumn || this.options.showExpandActionsColumn) {
                colSpan++;
            }
            var newRow = Dom.get(this.getExpandedRecordId(record));
            var newColumn = document.createElement('td');
            newColumn.innerHTML = text;
            newRow.appendChild(newColumn);
            if (this.options.showExpandActionsColumn) {
                var actions = this.options.actions;
                var expandActionType = this.expandActionTypePrefix + "-" + this.options.bubblingLabel;
                if (actions) {
                    var userAccess = record.getData("permissions").userAccess;
                    var acessibleActions = actions.filter(function (action) {
                        return userAccess[action["permission"]] && action.evaluator.call(me, record.getData());
                    });
                    if (acessibleActions && acessibleActions.length) {
                        var actionsColumn = document.createElement('td');
                        var actionsDiv = document.createElement('div');
                        actionsDiv.id = this.id + "-coexutor-report-actions-div";
                        actionsDiv.className = "coexutor-report-actions-div";
                        var actionsHtml = '';
                        acessibleActions.forEach(function (action) {
                            actionsHtml += Substitute(me.getActionHtml(), {
                                label: action.label,
                                aClass: this.expandActionTypePrefix + " " + expandActionType,
                                rel: action.permission,
                                actionDivClass: action.id
                            });
                        });
                        actionsDiv.innerHTML = actionsHtml;
                        actionsColumn.appendChild(actionsDiv);
                        newRow.appendChild(actionsColumn);
                    }
                }
            }
            newColumn.colSpan = colSpan;
            newRow.style.display = "";
        },

        customTableSetup: function () {
            if (this.options.showExpandActionsColumn) {
                this.setupExpandActionEvents(this.expandActionTypePrefix);
            }
        },

        getDataTableColumnDefinitions: function DataGrid_getDataTableColumnDefinitions() {
            // YUI DataTable column definitions
            var columnDefinitions = [];
            if (this.options.expandable) {
                columnDefinitions.push({
                    key: "expand",
                    label: "",
                    sortable: false,
                    formatter: this.fnRenderCellExpand(),
                    width: 16
                });
            }
            if (this.options.showCheckboxColumn) {
                columnDefinitions.push({
                    key: "nodeRef",
                    label: "<input type='checkbox' id='" + this.id + "-select-all-records'>",
                    sortable: false,
                    formatter: this.fnRenderCellSelected(),
                    width: 16
                });
            }

            var inArray = function (value, array) {
                for (var i = 0; i < array.length; i++) {
                    if (array[i] == value) return true;
                }
                return false;
            };

            var column, sortable;
            for (var i = 0, ii = this.datagridColumns.length; i < ii; i++) {
                column = this.datagridColumns[i];

                if (!this.options.overrideSortingWith) {
                    sortable = column.sortable;
                } else {
                    sortable = this.options.overrideSortingWith;
                }

                if (!(this.options.excludeColumns.length > 0 && inArray(column.name, this.options.excludeColumns))) {
                    var className = "";
                    if (column.dataType == "lecm-orgstr:employee" || (this.options.nowrapColumns.length > 0 && inArray(column.name, this.options.nowrapColumns))) {
                        className = "nowrap "
                    }

                    columnDefinitions.push({
                        key: this.dataResponseFields[i],
                        label: column.label.length > 0 ? column.label : this.msg(column.name.replace(":", "_")),
                        sortable: sortable,
                        sortOptions: {
                            field: column.formsName,
                            sortFunction: this.getSortFunction()
                        },
                        formatter: this.getCellFormatter(column.dataType),
                        className: className + ((column.dataType == 'boolean') ? 'centered' : '')
                    });
                }
            }
            if (this.options.showActionColumn) {
                // Add actions as last column
                columnDefinitions.push(
                    {
                        key: this.options.showExpandActionColumn ? "expand-actions" : "actions",
                        label: this.msg("label.column.actions"),
                        sortable: false,
                        formatter: this.fnRenderCellActions(),
                        width: this.options.showExpandActionColumn ? 150 : Math.round(26.7 * this.showActionsCount)
                    }
                );
            }
            if (this.options.showExpandActionsColumn && !this.options.showActionColumn) {
                columnDefinitions.push(
                    {
                        key: "expand-actions",
                        label: "",
                        sortable: false,
                        formatter: this.getCellFormatter(column.dataType),
                        width: 150
                    }
                );
            }
            if (!this.options.overrideSortingWith && this.options.otherActions && this.options.otherActions.length > 0) {
                // Add actions as last column
                columnDefinitions.push(
                    {
                        key: "other-actions",
                        label: "",
                        sortable: false,
                        formatter: this.fnRenderCellOtherActions(),
                        width: 80
                    }
                );
            }
            return columnDefinitions;
        },

        setupExpandActionEvents: function (actionTypePrefix) {
            var me = this;
            var fnActionHandler = function DataGrid_fnActionHandler(layer, args) {
                var owner = Bubbling.getOwnerByTagName(args[1].anchor, "div");
                if (owner) {
                    if (typeof me[owner.className] == "function") {
                        args[1].stop = true;
                        var row = me.widgets.dataTable.getRecord(args[1].target.offsetParent.parentElement.previousElementSibling);
                        if (row) {
                            var asset = row.getData();

                            var confirmFunction = null;
                            if (me.options.actions) {
                                for (var i = 0; i < me.options.actions.length; i++) {
                                    if (me.options.actions[i].id == owner.className && me.options.actions[i].confirmFunction) {
                                        confirmFunction = me.options.actions[i].confirmFunction;
                                    }
                                }
                            }

                            me[owner.className].call(me, asset, owner, me.datagridMeta.actionsConfig, confirmFunction);
                        }
                    }
                }
                return true;
            };
            Bubbling.addDefaultAction(actionTypePrefix + (me.options.bubblingLabel ? "-" + me.options.bubblingLabel : ""), fnActionHandler, me.options.forceSubscribing);
        },

        getActionHtml: function () {
            var html = '<div class="{actionDivClass}">';
            html += '<span class="yui-button yui-push-button">';
            html += '<span class="first-child">';
            html += '<a rel="{rel}" class="{aClass}">{label}</a>';
            html += '</span></span></div>';
            return html;
        },
*/
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
            if (nodeRef) {
                var me = this;
                var formId = "decline-coexecutor-report";
                var declineReportDialog = new Alfresco.module.SimpleDialog(this.id + '-' + formId);
                declineReportDialog.setOptions({
                    templateUrl: Alfresco.constants.URL_SERVICECONTEXT + 'components/form',
                    actionUrl: Alfresco.constants.PROXY_URI_RELATIVE + '/lecm/errands/coexecutorReport/decline?nodeRef=' + nodeRef,
                    templateRequestParams: {
                        formId: formId,
                        itemKind: "node",
                        itemId: nodeRef,
                        mode: "edit",
                        showCancelButton: true,
                        showCaption: false,
                        submitType: 'json'
                    },
                    width: '50em',
                    destroyOnHide: true,
                    doBeforeDialogShow: {
                        fn: function (form, simpleDialog) {
                            simpleDialog.dialog.setHeader(this.msg("label.coexecutor.reports.decline"));
                            simpleDialog.dialog.subscribe('destroy', function (event, args, params) {
                                LogicECM.module.Base.Util.destroyForm(simpleDialog.id);
                                LogicECM.module.Base.Util.formDestructor(event, args, params);
                            }, {moduleId: simpleDialog.id}, this);
                        },
                        scope: this
                    },
                    onSuccess: {
                        fn: function (response) {
                            if (response.json.success) {
                                me._itemUpdate(nodeRef);
                            } else {
                                Alfresco.util.PopupManager.displayMessage(
                                    {
                                        text: Alfresco.util.message("message.details.failure")
                                    });
                            }
                            declineReportDialog.hide();
                        },
                        scope: this
                    },
                    onFailure: {
                        fn: function (response) {
                            Alfresco.util.PopupManager.displayMessage(
                                {
                                    text: Alfresco.util.message("message.details.failure")
                                });
                            declineReportDialog.hide();
                        }
                    }
                });
                declineReportDialog.show();
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
