if (typeof LogicECM == 'undefined' || !LogicECM) {
    LogicECM = {};
}

LogicECM.module = LogicECM.module || {};

LogicECM.module.Errands = LogicECM.module.Errands || {};

(function () {
    var Dom = YAHOO.util.Dom,
        Event = YAHOO.util.Event,
        Bubbling = YAHOO.Bubbling,
        Substitute = YAHOO.lang.substitute,
        Selector = YAHOO.util.Selector;

    LogicECM.module.Errands.CoexecutorReportExpandActions = function (containerId) {
        return LogicECM.module.Errands.CoexecutorReportExpandActions.superclass.constructor.call(this, containerId);
    };

    YAHOO.lang.extend(LogicECM.module.Errands.CoexecutorReportExpandActions, Alfresco.component.Base);

    YAHOO.lang.augmentObject(LogicECM.module.Errands.CoexecutorReportExpandActions.prototype, {

        options: {
            formId: "",
            fieldId: "",
            expandedReport: {
                nodeRef: null,
                status: null,
                coexecutor: null,
                permissions: [],
                actions: [],
                type: "lecm-errands-ts:coexecutor-report"
            }
        },
        errandNodeRef: null,
        currentUser: {
            nodeRef: null,
            isExecutor: false,
            isCoexecutor: false
        },
        currentDocumentStatus: null,
        editDialogOpening: false,
        doubleClickLock: false,

        onReady: function () {
            this.loadAdditionalData();
        },
        loadAdditionalData: function () {
            this.options.expandedReport.actions = [];
            Alfresco.util.Ajax.jsonPost(
                {
                    url: Alfresco.constants.PROXY_URI + "lecm/substitude/format/node",
                    dataObj: {
                        nodeRef: this.options.expandedReport.nodeRef,
                        substituteString: "{../../../nodeRef}"
                    },
                    successCallback: {
                        fn: function (response) {
                            if (response && response.json.formatString) {
                                this.errandNodeRef = response.json.formatString;
                                Alfresco.util.Ajax.jsonRequest(
                                    {
                                        method: Alfresco.util.Ajax.GET,
                                        url: Alfresco.constants.PROXY_URI + "lecm/errands/api/getCurrentEmployeeRoles?errandNodeRef=" + encodeURIComponent(this.errandNodeRef),
                                        successCallback: {
                                            fn: function (response) {
                                                var roles = response.json;
                                                if (roles) {
                                                    if (roles.isExecutor) {
                                                        this.currentUser.isExecutor = true;
                                                        this.options.expandedReport.actions.push({
                                                            handler: "onActionAcceptCoexecutorReport",
                                                            permission: "Write",
                                                            label: this.msg("actions.coexecutor.report.accept"),
                                                            evaluator: this.firstActionsEvaluator
                                                        });
                                                        this.options.expandedReport.actions.push({
                                                            handler: "onActionDeclineCoexecutorReport",
                                                            permission: "Write",
                                                            label: this.msg("actions.coexecutor.report.decline"),
                                                            evaluator: this.firstActionsEvaluator
                                                        });
                                                        this.options.expandedReport.actions.push({
                                                            handler: "onActionTransferCoexecutorReport",
                                                            permission: "Write",
                                                            label: this.msg("actions.coexecutor.report.transfer"),
                                                            evaluator: this.transferActionEvaluator
                                                        });
                                                    }
                                                    if (roles.isCoexecutor) {
                                                        this.currentUser.isCoexecutor = true;
                                                        this.options.expandedReport.actions.push({
                                                            handler: "onActionEdit",
                                                            permission: "Write",
                                                            label: this.msg("actions.edit"),
                                                            evaluator: this.editActionEvaluator
                                                        });
                                                    }
                                                }
                                                Alfresco.util.Ajax.jsonGet({
                                                    url: Alfresco.constants.PROXY_URI + "lecm/orgstructure/api/getCurrentEmployee",
                                                    successCallback: {
                                                        fn: function (response) {
                                                            if (response && response.json.nodeRef) {
                                                                this.currentUser.nodeRef = response.json.nodeRef;
                                                                Alfresco.util.Ajax.jsonPost({
                                                                    url: Alfresco.constants.PROXY_URI + "lecm/substitude/format/node",
                                                                    dataObj: {
                                                                        nodeRef: this.errandNodeRef,
                                                                        substituteString: "{lecm-statemachine:status}"
                                                                    },
                                                                    successCallback: {
                                                                        fn: function (response) {
                                                                            if (response && response.json.formatString) {
                                                                                this.currentDocumentStatus = response.json.formatString;
                                                                                this.processDeclineReasonField();
                                                                                this.prepareButtons();
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

                            }
                        },
                        scope: this
                    },
                    failureMessage: Alfresco.util.message("message.details.failure"),
                    scope: this
                }
            )

        },
        processDeclineReasonField: function () {
            var declineReasonFields = Selector.query(".hiddenFields.hidden1", Dom.get(this.options.formId), false);
            //Берем nodeRef для идентификации нужного блока
            var nodeId = encodeURIComponent(this.options.expandedReport.nodeRef);
            if (declineReasonFields && declineReasonFields.length > 0) {
                for (var i = 0; i < declineReasonFields.length; i++) {
                    if (declineReasonFields[i] && declineReasonFields[i].className.indexOf(nodeId) != -1
                        && this.options.expandedReport.status == "DECLINE") {
                        Dom.removeClass(declineReasonFields[i], "hidden1");
                    }
                }
            }
        },
        prepareButtons: function () {
            var me = this;
            var actionsContainer = Dom.get(me.options.formId + "-coexecutor-report-expand-actions");
            var fieldsContainer = Selector.query(".yui-u.first", Dom.get(me.options.formId + "-form-fields"), true);
            var visibleActionBlocks = this.options.expandedReport.actions.filter(function (action) {
                return me.options.expandedReport.permissions[action.permission] && action.evaluator.call(me, me.options.expandedReport);
            });
            visibleActionBlocks.forEach(function (action, index) {
                var actionBlock = document.createElement("div");
                actionBlock.id = me.options.formId + action.handler;

                var actionBtn = new YAHOO.widget.Button({
                    container: actionBlock.id,
                    label: action.label,
                    onclick:
                        {
                            fn: me[action.handler],
                            obj: me.options.expandedReport,
                            scope: me
                        }
                });

                if (index == visibleActionBlocks.length - 1) {
                    actionBtn.addListener("appendTo", me.setPadding, null, me);
                }

                actionsContainer.appendChild(actionBlock);
            });

            if (visibleActionBlocks.length == 0) {
                actionsContainer.parentElement.parentElement.classList.add("hidden");
                fieldsContainer.classList.add("full-width");
            }
        },
        setPadding: function() {
            var me = this,
                actionsContainer = Dom.get(me.options.formId + "-coexecutor-report-expand-actions"),
                fieldsContainer = Selector.query(".yui-u.first", Dom.get(me.options.formId + "-form-fields"), true),
                padding = (fieldsContainer.offsetHeight - actionsContainer.offsetHeight
                    - parseInt(Dom.getStyle(actionsContainer, "margin-top")) - parseInt(Dom.getStyle(actionsContainer, "margin-bottom"))) / 2;

            Dom.setStyle(actionsContainer, "padding-top", padding + "px");
            Dom.setStyle(actionsContainer, "padding-bottom", padding + "px");
        },
        firstActionsEvaluator: function (report) {
            return report.status && report.status == "ONCONTROL";
        },
        transferActionEvaluator: function (report) {
            var isDocumentStatusOK = "На исполнении" == this.currentDocumentStatus || "На доработке" == this.currentDocumentStatus;
            isDocumentStatusOK = isDocumentStatusOK || Alfresco.util.message("lecm.errands.statemachine-status.on-execution") == this.currentDocumentStatus || Alfresco.util.message("lecm.errands.statemachine-status.on-rework") == this.currentDocumentStatus;
            return report.status && report.status == "ACCEPT" && isDocumentStatusOK;
        },
        editActionEvaluator: function (report) {
            return report.status && report.status == "PROJECT" && report.coexecutor == this.currentUser.nodeRef;
        },
        onActionAcceptCoexecutorReport: function (evt, report) {
            var nodeRef = report.nodeRef;
            if (nodeRef != null && !this.doubleClickLock) {
                this.doubleClickLock = true;
                Alfresco.util.Ajax.jsonRequest(
                    {
                        method: Alfresco.util.Ajax.GET,
                        url: Alfresco.constants.PROXY_URI + "lecm/errands/coexecutorReport/accept?nodeRef=" + nodeRef,
                        successCallback: {
                            fn: function (response) {
                                if (response.json.success) {
                                    this.updateItem(nodeRef);
                                } else {
                                    Alfresco.util.PopupManager.displayMessage(
                                        {
                                            text: this.msg("message.details.failure")
                                        });
                                }
                                this.doubleClickLock = false;
                            },
                            scope: this
                        },
                        failureCallback: {
                            fn: function (res) {
                                Alfresco.util.PopupManager.displayPrompt(
                                    {
                                        title: Alfresco.util.message("message.failure"),
                                        text: Alfresco.util.message("message.details.failure")
                                    });
                                this.doubleClickLock = false;
                            },
                            scope: this
                        },
                        scope: this
                    });
            }
        },

        onActionDeclineCoexecutorReport: function (evt, report) {
            var nodeRef = report.nodeRef;
            if (nodeRef && !this.doubleClickLock) {
                this.doubleClickLock = true;
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
                                this.doubleClickLock = false;
                            }, {moduleId: simpleDialog.id}, this);
                        },
                        scope: this
                    },
                    onSuccess: {
                        fn: function (response) {
                            if (response.json.success) {
                                this.updateItem(nodeRef);
                            } else {
                                Alfresco.util.PopupManager.displayMessage(
                                    {
                                        text: Alfresco.util.message("message.details.failure")
                                    });
                            }
                            declineReportDialog.hide();
                            this.doubleClickLock = false;
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
                            this.doubleClickLock = false;
                        }
                    }
                });
                declineReportDialog.show();
            }
        },
        onActionEdit: function (evt, report) {
            Bubbling.fire("onActionEditCoexecutorReport", {
                report: report
            });
        },
        onActionTransferCoexecutorReport: function (evt, report) {
            Bubbling.fire("onActionTransferCoexecutorReport", {
                nodeRef: report.nodeRef
            });
        },
        updateItem: function (nodeRef) {
            Bubbling.fire("onCoexecutorReportUpdated", {
                nodeRef: nodeRef
            });
        }
    }, true);
})();