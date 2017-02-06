if (typeof LogicECM == 'undefined' || !LogicECM) {
    LogicECM = {};
}

LogicECM.module = LogicECM.module || {};

LogicECM.module.Errands = LogicECM.module.Errands || {};

(function () {
    var Dom = YAHOO.util.Dom,
        Event = YAHOO.util.Event,
        Bubbling = YAHOO.Bubbling,
        Substitute = YAHOO.lang.substitute;

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
                type: "lecm-errands-ts:coexecutor-report"
            }
        },
        actions: [],
        errandNodeRef: null,
        currentUser: {
            nodeRef: null,
            isExecutor: false,
            isCoexecutor: false
        },
        currentDocumentStatus: null,
        editDialogOpening: false,

        onReady: function () {
            this.loadAdditionalData();
        },
        loadAdditionalData: function () {
            var me = this;
            Alfresco.util.Ajax.jsonPost(
                {
                    url: Alfresco.constants.PROXY_URI + "lecm/substitude/format/node",

                    dataObj: {
                        nodeRef: me.options.expandedReport.nodeRef,
                        substituteString: "{../../../nodeRef}"
                    },
                    successCallback: {
                        fn: function (response) {
                            var me = response.config.scope;
                            if (response && response.json.formatString) {
                                if (response.json.formatString) {
                                    me.errandNodeRef = response.json.formatString;
                                    Alfresco.util.Ajax.jsonRequest(
                                        {
                                            method: Alfresco.util.Ajax.GET,
                                            url: Alfresco.constants.PROXY_URI + "lecm/errands/api/getCurrentEmployeeRoles?errandNodeRef=" + encodeURIComponent(me.errandNodeRef),
                                            successCallback: {
                                                fn: function (response) {
                                                    var me = response.config.scope;
                                                    var roles = response.json;
                                                    if (roles) {
                                                        if (roles.isExecutor) {
                                                            me.currentUser.isExecutor = true;
                                                            me.actions.push({
                                                                handler: "onActionAcceptCoexecutorReport",
                                                                permission: "Write",
                                                                label: me.msg("actions.coexecutor.report.accept"),
                                                                evaluator: me.firstActionsEvaluator
                                                            });
                                                            me.actions.push({
                                                                handler: "onActionDeclineCoexecutorReport",
                                                                permission: "Write",
                                                                label: me.msg("actions.coexecutor.report.decline"),
                                                                evaluator: me.firstActionsEvaluator
                                                            });
                                                            me.actions.push({
                                                                handler: "onActionTransferCoexecutorReport",
                                                                permission: "Write",
                                                                label: me.msg("actions.coexecutor.report.transfer"),
                                                                evaluator: me.transferActionEvaluator
                                                            });
                                                        }
                                                        if (roles.isCoexecutor) {
                                                            me.currentUser.isCoexecutor = true;
                                                            me.actions.push({
                                                                handler: "onActionEdit",
                                                                permission: "Write",
                                                                label: me.msg("actions.edit"),
                                                                evaluator: me.editActionEvaluator
                                                            });
                                                        }
                                                    }
                                                    Alfresco.util.Ajax.jsonGet({
                                                        url: Alfresco.constants.PROXY_URI + "lecm/orgstructure/api/getCurrentEmployee",
                                                        successCallback: {
                                                            fn: function (response) {
                                                                var me = response.config.scope;
                                                                if (response && response.json.nodeRef) {
                                                                    me.currentUser.nodeRef = response.json.nodeRef;
                                                                    Alfresco.util.Ajax.jsonPost({
                                                                        url: Alfresco.constants.PROXY_URI + "lecm/substitude/format/node",
                                                                        dataObj: {
                                                                            nodeRef: me.errandNodeRef,
                                                                            substituteString: "{lecm-statemachine:status}"
                                                                        },
                                                                        successCallback: {
                                                                            fn: function (response) {
                                                                                var me = response.config.scope;
                                                                                if (response && response.json.formatString) {
                                                                                    me.currentDocumentStatus = response.json.formatString;
                                                                                    me.prepareButtons();
                                                                                }
                                                                            }
                                                                        },
                                                                        failureMesage: Alfresco.util.message("message.details.failure"),
                                                                        scope: me
                                                                    });
                                                                }
                                                            }
                                                        },
                                                        failureMesage: Alfresco.util.message("message.details.failure"),
                                                        scope: me
                                                    });

                                                }
                                            },
                                            failureMesage: Alfresco.util.message("message.details.failure"),
                                            scope: me
                                        });
                                }
                            }
                        },
                        scope: me
                    },
                    failureMesage: Alfresco.util.message("message.details.failure"),
                    scope: this
                }
            )

        },
        prepareButtons: function () {
            var me = this;
            var actionsContainer = Dom.get(me.options.formId + "-coexecutor-report-expand-actions");
            var fieldsContainer = Selector.query(".yui-u.first", Dom.get(me.options.formId + "-form-fields"), true);
            var visibleActionBlocks = this.actions.filter(function (action) {
                return me.options.expandedReport.permissions[action.permission] && action.evaluator.call(me, me.options.expandedReport);
            }).map(function (action) {
                var actionBlock = document.createElement("div");
                actionBlock.id = me.options.formId + action.handler;
                actionBlock.innerHTML = Substitute(me.getActionHtml(), {
                    label: action.label
                });
                Event.addListener(actionBlock, 'click', function () {
                    me[action.handler].call(me, me.options.expandedReport);
                });
                actionsContainer.appendChild(actionBlock);
            });

            if (visibleActionBlocks.length == 0) {
                actionsContainer.parentElement.parentElement.classList.add("hidden");
                fieldsContainer.classList.add("full-width");
            }
            var padding = (fieldsContainer.offsetHeight - actionsContainer.offsetHeight) / 2;
            Dom.setStyle(actionsContainer, "padding-top", padding + "px");
            Dom.setStyle(actionsContainer, "padding-bottom", padding + "px");

        },
        firstActionsEvaluator: function (report) {
            return report.status && report.status == "ONCONTROL";
        },
        transferActionEvaluator: function (report) {
            var isDocumentStatusOK = "На исполнении" == this.currentDocumentStatus || "На доработке" == this.currentDocumentStatus;
            return report.status && report.status == "ACCEPT" && isDocumentStatusOK;
        },
        editActionEvaluator: function (report) {
            return report.status && report.status == "PROJECT" && report.coexecutor == this.currentUser.nodeRef;
        },
        getActionHtml: function () {
            html = '<span class="yui-button yui-push-button">';
            html += '<span class="first-child">';
            html += '<button type="button">{label}</button>';
            html += '</span></span>';
            return html;
        },
        onActionAcceptCoexecutorReport: function (report) {
            var nodeRef = report.nodeRef;
            if (nodeRef != null) {
                Alfresco.util.Ajax.jsonRequest(
                    {
                        method: Alfresco.util.Ajax.GET,
                        url: Alfresco.constants.PROXY_URI + "lecm/errands/coexecutorReport/accept?nodeRef=" + nodeRef,
                        successCallback: {
                            fn: function (response) {
                                var me = response.config.scope;
                                if (response.json.success) {
                                    me.updateItem(nodeRef);
                                } else {
                                    Alfresco.util.PopupManager.displayMessage(
                                        {
                                            text: me.msg("message.details.failure")
                                        });
                                }
                            }
                        },
                        failureMesage: Alfresco.util.message("message.details.failure"),
                        scope: this
                    });
            }
        },

        onActionDeclineCoexecutorReport: function (report) {
            var nodeRef = report.nodeRef;
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
                                me.updateItem(nodeRef);
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
        onActionEdit: function (report) {
            Bubbling.fire("onActionEditCoexecutorReport", {
                report: report
            });
        },
        onActionTransferCoexecutorReport: function (report) {
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