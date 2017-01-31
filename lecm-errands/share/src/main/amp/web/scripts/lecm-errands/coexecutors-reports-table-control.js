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
                    showActionColumn: true,
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

        addExpandedRow: function(record, text){
            var me = this;
            var colSpan = this.datagridColumns.length;
            if (this.options.showCheckboxColumn) {
                colSpan++;
            }
            if (this.options.expandable) {
                colSpan++;
            }
            if (this.options.showActionColumn) {
                colSpan++;
            }

            var newRow = Dom.get(this.getExpandedRecordId(record));

            var newColumn = document.createElement('td');
            newColumn.colSpan = colSpan;
            newColumn.innerHTML = text;
            newRow.appendChild(newColumn);

            var actions = this.options.actions;
            if (actions) {
                var userAccess = record.getData("permissions").userAccess;
                var acessibleActions = actions.filter(function(action){
                    return userAccess[action["permission"]] && action.evaluator.call(me, record.getData());
                });
                if (acessibleActions && acessibleActions.length){
                    var actionsColumn = document.createElement('td');
                    var actionsDiv = document.createElement('div');
                    actionsDiv.id=this.id+"coexutor-report-actions-div";
                    //var div = '<div class="coexutor-report-actions-div">{actions}</div>';
                    var onSetupActions = function onSetupActions(actions, id, className) {

                        if (actionsDiv.children.length == 0) {
                            for (var i = 0; i < actions.length; i++) {
                                var action = actions[i];

                                var actionDiv = document.createElement("div");
                                actionDiv.className = action.id;

                                var actionA = document.createElement("a");
                                actionA.rel = action.permission;
                                actionA.className = className + action.type;
                                actionA.title = action.label;

                                var actionSpan = document.createElement("span");
                                actionSpan.innerHTML = action.label;

                                actionA.appendChild(actionSpan);
                                actionDiv.appendChild(actionA);
                                actionsDiv.appendChild(actionDiv);
                            }
                        }
                    }
                    if (this.options.actions != null) {
                        onSetupActions(this.options.actions, actionsDiv.id,"datagrid-action-link ");
                    }

                    if (this.options.otherActions != null && this.options.otherActions.length > 0) {
                        onSetupActions(this.options.otherActions, actionsDiv.id,"datagrid-other-action-link ");
                    }
                    actionsColumn.appendChild(actionsDiv);
                    newRow.appendChild(actionsColumn);
                   /* var actions = '';
                    acessibleActions.forEach(function(action){
                        actions += Substitute(me.getActionHtml(), {
                            label: action.label,
                            className: action.id
                        });
                    });
                    actionsColumn.innerHTML = Substitute(div, {
                        actions:actions
                    });
                    newRow.appendChild(actionsColumn);*/
                }
            }

            newRow.style.display="";
        },
        onReady: function DataGrid_onReady()
        {
            var me = this;

            if (this.options.actions.length > this.showActionsCount) {
                this.showActionsCount = this.options.splitActionsAt;
            }
            this.splitActionsAtStore = this.options.splitActionsAt;

            if (this.options.showActionColumn){
                // Hook action events
                var fnActionHandler = function DataGrid_fnActionHandler(layer, args)
                {
                    var owner = Bubbling.getOwnerByTagName(args[1].anchor, "div");
                    if (owner !== null)
                    {
                        if (typeof me[owner.className] == "function")
                        {
                            args[1].stop = true;
                            var row = me.widgets.dataTable.getRecord(args[1].target.offsetParent);
                            if (row) {
                                var asset = row.getData();

                                var confirmFunction = null;
                                if (me.options.actions != null) {
                                    for (var i = 0; i < me.options.actions.length; i++) {
                                        if (me.options.actions[i].id == owner.className && me.options.actions[i].confirmFunction != null) {
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
                Bubbling.addDefaultAction("datagrid-action-link" + (me.options.bubblingLabel ? "-"+ me.options.bubblingLabel : ""), fnActionHandler, me.options.forceSubscribing);
                Bubbling.addDefaultAction("show-more"  + (me.options.bubblingLabel ? "-"+ me.options.bubblingLabel : ""), fnActionHandler, me.options.forceSubscribing);
            }

            if (!this.options.overrideSortingWith && me.options.otherActions != null && me.options.otherActions.length > 0){
                // Hook action events
                var fnOtherActionHandler = function DataGrid_fnActionHandler(layer, args)
                {
                    var owner = Bubbling.getOwnerByTagName(args[1].anchor, "div");
                    if (owner !== null)
                    {
                        if (typeof me[owner.className] == "function")
                        {
                            args[1].stop = true;
                            var row = me.widgets.dataTable.getRecord(args[1].target.offsetParent.parentElement.previousElementSibling);
                            if (row) {
                                var asset = row.getData();

                                var confirmFunction = null;
                                if (me.options.otherActions != null) {
                                    for (var i = 0; i < me.options.otherActions.length; i++) {
                                        if (me.options.otherActions[i].id == owner.className && me.options.otherActions[i].confirmFunction != null) {
                                            confirmFunction = me.options.otherActions[i].confirmFunction;
                                        }
                                    }
                                }

                                me[owner.className].call(me, asset, owner, me.datagridMeta.actionsConfig, confirmFunction);
                            }
                        }
                    }
                    return true;
                };
                Bubbling.addDefaultAction("datagrid-other-action-link" + (me.options.bubblingLabel ? "-"+ me.options.bubblingLabel : ""), fnOtherActionHandler, me.options.forceSubscribing);
                Bubbling.addDefaultAction("show-more", fnOtherActionHandler, me.options.forceSubscribing);
            }

            // Actions module
            this.modules.actions = new LogicECM.module.Base.Actions();

            // Reference to Data Grid component (required by actions module)
            this.modules.dataGrid = this;

            this.deferredListPopulation.fulfil("onReady");

            // Finally show the component body here to prevent UI artifacts on YUI button decoration
            Dom.setStyle(this.id + "-body", "visibility", "visible");

            Bubbling.fire("initDatagrid",
                {
                    datagrid:this
                });
        },
        getActionHtml: function(){
            var html = '<span class="yui-button yui-push-button {className}">';
            html += '<span class="first-child"><button type="button" onclick="{handler}">{label}</button>';
            html += '</span></span>';
            return html;
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
