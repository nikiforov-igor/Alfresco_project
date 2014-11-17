/**
 * Module Namespaces
 */
if (typeof LogicECM == "undefined" || !LogicECM) {
    LogicECM = {};
}
LogicECM.module = LogicECM.module || {};
LogicECM.module.ReportsEditor = LogicECM.module.ReportsEditor|| {};

/**
 * ReportsEditor module.
 *
 * @namespace LogicECM.module.ReportsEditor
 * @class LogicECM.module.ReportsEditor.TemplateEditToolbar
 */
(function () {

    var Dom = YAHOO.util.Dom;

    LogicECM.module.ReportsEditor.TemplateEditToolbar = function (htmlId) {
        LogicECM.module.ReportsEditor.TemplateEditToolbar.superclass.constructor.call(
            this,
            "LogicECM.module.ReportsEditor.TemplateEditToolbar",
            htmlId,
            ["button", "container", "connection"]);

        YAHOO.Bubbling.on("initDatagrid", this.onInitDataGrid, this);
        YAHOO.Bubbling.on("copyTemplateToReport", this._onCopyToReport, this);
        YAHOO.Bubbling.on("unsubscribeBubbling", this.onUnsubscribe, this);
        return this;
    };

    YAHOO.extend(LogicECM.module.ReportsEditor.TemplateEditToolbar, Alfresco.component.Base, {
        reportId: null,

        templatesGrid: null,

        toolbarButtons: {},

        doubleClickLock: false,

        bubblingLabel: null,

        selectTemplatePanel: null,

        onUnsubscribe: function (layer, args) {
            YAHOO.Bubbling.unsubscribe("copyTemplateToReport", this._onCopyToReport, this);
        },

        setLabel: function (label) {
            this.bubblingLabel = label;
        },

        setReportId: function (reportId) {
            this.reportId = reportId;
        },

        onInitDataGrid: function EditTemplateToolbar_onInitDataGrid (layer, args) {
            var datagrid = args[1].datagrid;
            if ((!this.bubblingLabel || !datagrid.options.bubblingLabel) || this.bubblingLabel == datagrid.options.bubblingLabel) {
            this.templatesGrid = args[1].datagrid;
                YAHOO.Bubbling.unsubscribe("initDatagrid", EditTemplateToolbar_onInitDataGrid, this);
            }
        },

        onReady: function () {
            this._initToolbarButtons();
            Dom.setStyle(this.id + "-body", "visibility", "visible");
        },

        showCreateDialog: function (meta) {
            if (this.doubleClickLock) {
                return;
            }
            this.doubleClickLock = true;

            this._showCreateForm(meta);
        },

        _showCreateForm: function (meta) {
            var toolbar = this;

            var doBeforeDialogShow = function (p_form, p_dialog) {
                var defaultMsg = toolbar.msg("label.create-template.title");
                Alfresco.util.populateHTML(
                    [ p_dialog.id + "-form-container_h", defaultMsg ]
                );

                Dom.addClass(p_dialog.id + "-form-container", "metadata-form-edit");

                toolbar.doubleClickLock = false;
            };

            var templateUrl = YAHOO.lang.substitute(Alfresco.constants.URL_SERVICECONTEXT + "lecm/components/form?itemKind={itemKind}&itemId={itemId}&destination={destination}&mode={mode}&submitType={submitType}&formId={formId}&showCancelButton=true",
                {
                    itemKind: meta.itemKind ? meta.itemKind : "type",
                    itemId: meta.itemType,
                    destination: meta.nodeRef,
                    mode: meta.formMode ? meta.formMode : "create",
                    submitType: "json",
                    formId: meta.formId ? meta.formId : "uploadTemplate"
                });

            var context = this;
            var createDetails = new Alfresco.module.SimpleDialog(this.id + "-createDetails-" + Alfresco.util.generateDomId());
            createDetails.setOptions(
                {
                    width: "50em",
                    templateUrl: templateUrl,
                    actionUrl: null,
                    destroyOnHide: true,
                    doBeforeDialogShow: {
                        fn: doBeforeDialogShow,
                        scope: this
                    },
                    onSuccess: {
                        fn: function DataGrid_onActionCreate_success(response) {
                            Alfresco.util.PopupManager.displayMessage(
                                {
                                    text: this.msg("message.save.success")
                                });

                            YAHOO.Bubbling.fire("dataItemCreated", // обновить данные в гриде
                                {
                                    nodeRef: response.json.persistedObject,
                                    bubblingLabel: context.bubblingLabel
                                });
                            this.doubleClickLock = false;
                        },
                        scope: this
                    },
                    onFailure: {
                        fn: function DataGrid_onActionCreate_failure(response) {
                            alert(response.serverResponse.responseText);
                            Alfresco.util.PopupManager.displayMessage(
                                {
                                    text: this.msg("message.save.failure")
                                });
                            this.doubleClickLock = false;
                        },
                        scope: this
                    }
                }).show();

        },

        _initToolbarButtons: function () {
            this.toolbarButtons.newTemplateButton = Alfresco.util.createYUIButton(this, 'newTemplateButton', this._onNewTemplate,
                {
                    value: 'create'
                });

            this.toolbarButtons.newTemplateFromSourceButton = Alfresco.util.createYUIButton(this, 'newTemplateFromSourceButton', this._onNewTemplateFromSource,
                {
                    value: 'create'
                });

            this.toolbarButtons.newFromDicButton = Alfresco.util.createYUIButton(this, 'newFromDicButton', this._onCopyFromDic,
                {
                    disabled: false
                });
        },

        _onNewTemplate: function () {
            this.showCreateDialog(
                {
                    itemType: "lecm-rpeditor:reportTemplate",
                    nodeRef: this.reportId,
                    formMode: "create",
                    itemKind: "type",
                    formId: "uploadTemplate"
                });
        },

        _onCopyFromDic: function () {
            Alfresco.util.Ajax.request(
                {
                    method: "GET",
                    url: Alfresco.constants.URL_SERVICECONTEXT + "lecm/reports-editor/template-select",
                    successCallback: {
                        fn: function (response) {
                            var formDiv = Dom.get("selectTemplatePanel-form");
                            formDiv.innerHTML = response.serverResponse.responseText;
                            this._viewSelectDialog();
                        },
                        scope: this
                    },
                    failureCallback: {
                        fn: function () {
                            Alfresco.util.PopupManager.displayMessage({
                                text: "Не удалось получить список шаблонов"
                            });
                        }
                    },
                    execScripts: true
                });
        },

        _onNewTemplateFromSource: function () {
            this.showCreateDialog({itemType: "lecm-rpeditor:reportTemplate", nodeRef: this.reportId, formId: "createFromDataSource"});
        },

        _viewSelectDialog: function () {
            if (this.selectTemplatePanel) {
                Dom.setStyle("selectTemplatePanel", "display", "block");
                this.selectTemplatePanel.show();
            } else {
                this.createSelectDialog();
                this._viewSelectDialog();
            }
        },

        createSelectDialog: function () {
            this.selectTemplatePanel = Alfresco.util.createYUIPanel("selectTemplatePanel",
                {
                    width: "600px"
                });
            YAHOO.Bubbling.on("hidePanel", this._hideSelectDialog);
            this.widgets.closeButton = Alfresco.util.createYUIButton(this, null, this._onClose, {}, Dom.get("selectTemplatePanel-close-button"));
        },

        _onClose: function () {
            this._hideSelectDialog(null);
        },

        _hideSelectDialog: function (layer, args) {
            var mayHide = false;
            if (this.selectTemplatePanel != null) {
                if (args == undefined || args == null) {
                    mayHide = true;
                } else if (args[1] && args[1].panel && args[1].panel.id == this.selectTemplatePanel.id) {
                    mayHide = true
                }
                if (mayHide) {
                    if (typeof this.selectTemplatePanel.hide == 'function') {
                        this.selectTemplatePanel.hide();
                    }
                    Dom.setStyle("selectTemplatePanel", "display", "none");
                    var formDiv = Dom.get("selectTemplatePanel-form");
                    formDiv.innerHTML = "";
                }
            }
        },

        _onCopyToReport: function (layer, args) {
            var obj = args[1];
            if ((obj !== null) && (obj.templateId !== null)) {
                if ((!obj.bubblingLabel || !this.templatesGrid.options.bubblingLabel) || obj.bubblingLabel == this.templatesGrid.options.bubblingLabel) {
                    this.copyTemplate(obj.templateId, LogicECM.module.ReportsEditor.SETTINGS.templatesContainer, this.reportId);
                }
            }
        },

        copyTemplate: function (templateId, from, to) {
            var copyRefs = [];
            copyRefs.push(templateId);

            var copyTo = new Alfresco.util.NodeRef(to);
            var context = this;
            Alfresco.util.Ajax.request(
                {
                    method: "POST",
                    dataObj: {
                        nodeRefs: copyRefs,
                        parentId: from
                    },
                    url: Alfresco.constants.PROXY_URI + "slingshot/doclib/action/copy-to/node/" + copyTo.uri,
                    successCallback: {
                        fn: function (response) {
                            Alfresco.util.PopupManager.displayMessage({
                                text: "Выполнено"
                            });
                            if (response.json.overallSuccess) {
                                var templateId = response.json.results[0].nodeRef;
                                YAHOO.Bubbling.fire("dataItemCreated", // обновить данные в гриде
                                    {
                                        nodeRef: templateId,
                                        bubblingLabel: context.templatesGrid.options.bubblingLabel
                                    });
                            }
                        },
                        scope: this
                    },
                    failureCallback: {
                        fn: function (response) {
                            alert(response.json.message);
                            Alfresco.util.PopupManager.displayMessage({
                                text: "Не удалось скопировать шаблон"
                            });
                        }
                    },
                    execScripts: true,
                    requestContentType: "application/json",
                    responseContentType: "application/json"
                });
        }
    });
})();