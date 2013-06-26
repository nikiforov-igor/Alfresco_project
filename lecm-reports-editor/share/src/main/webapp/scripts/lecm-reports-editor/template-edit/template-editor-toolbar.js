(function () {
    LogicECM.module.ReportsEditor.TemplateEditToolbar = function (htmlId) {
        return LogicECM.module.ReportsEditor.TemplateEditToolbar.superclass.constructor.call(this, "LogicECM.module.ReportsEditor.TemplateEditToolbar", htmlId);
    };

    YAHOO.extend(LogicECM.module.ReportsEditor.TemplateEditToolbar, LogicECM.module.Base.Toolbar);

    YAHOO.lang.augmentObject(LogicECM.module.ReportsEditor.TemplateEditToolbar.prototype,
        {
            options: {
                bubblingLabel: null,
                buttonDefaultGroup: 'defaultActive'
            },

            reportId: null,

            setReportId: function (reportId) {
                this.reportId = reportId;
            },

            _initButtons: function () {
                var group = this.options.buttonDefaultGroup;
                this.toolbarButtons[group].newTemplateButton = Alfresco.util.createYUIButton(this, 'newTemplateButton', this.onNewTemplate,
                    {
                        value: 'create'
                    });
                this.toolbarButtons[group].newTemplateFromSourceButton = Alfresco.util.createYUIButton(this, 'newTemplateFromSourceButton', this.onNewTemplateFromSource,
                    {
                        value: 'create'
                    });
                this.toolbarButtons[group].newTemplateSaveButton = Alfresco.util.createYUIButton(this, 'newTemplateSaveButton', this.onCopyToRepository,
                    {
                        disabled: true
                    });
                this.toolbarButtons[group].prevPageButton = Alfresco.util.createYUIButton(this, 'prevPageButton', this.onPrevButton);
                this.toolbarButtons[group].nextPageButton = Alfresco.util.createYUIButton(this, 'nextPageButton', this.onNextButton);
            },

            onCopyToRepository: function () {
                YAHOO.Bubbling.fire("copyTemplateToRepository");
            },

            onPrevButton: function () {
                var context = this;
                window.location.href = window.location.protocol + "//" + window.location.host +
                    Alfresco.constants.URL_PAGECONTEXT + "reports-editor-source-edit?reportId=" + context.reportId;
            },

            onNextButton: function () {
                var context = this;
                window.location.href = window.location.protocol + "//" + window.location.host +
                    Alfresco.constants.URL_PAGECONTEXT + "reports-editor?reportId=" + context.reportId;
            },

            onNewTemplateFromSource: function () {
                alert("Не реализовано");
                //this.showCreateDialog({itemType: "lecm-rpeditor:reportTemplate", nodeRef: this.reportId}, true);
            },

            onNewTemplate: function (e, p_obj) {
                this.showCreateDialog({itemType: "lecm-rpeditor:reportTemplate", nodeRef: this.reportId}, false);
            },

            showCreateDialog: function (meta, generateReport) {
                if (generateReport) {
                    Alfresco.util.Ajax.request(
                        {
                            url: Alfresco.constants.URL_SERVICECONTEXT + "lecm/reports-editor/generateTemplate",
                            dataObj: {
                                reportId: this.reportId
                            },
                            successCallback: {
                                fn: function (response) {
                                    this._showCreateForm(meta, response);
                                },
                                scope: this
                            },
                            failureMessage: "message.failure",
                            execScripts: true
                        });
                } else {
                    this._showCreateForm(meta);
                }
            },

            _showCreateForm: function (meta, response) {
                var doBeforeDialogShow = function (p_form, p_dialog) {
                    var defaultMsg = this.msg("label.create-row.title");
                    Alfresco.util.populateHTML(
                        [ p_dialog.id + "-form-container_h", defaultMsg ]
                    );

                    Dom.addClass(p_dialog.id + "-form", "metadata-form-edit");

                    if (response) {
                        // обновим форму данными шаблона
                    }
                };

                var templateUrl = YAHOO.lang.substitute(Alfresco.constants.URL_SERVICECONTEXT + "lecm/components/form?itemKind={itemKind}&itemId={itemId}&destination={destination}&mode={mode}&submitType={submitType}&formId={formId}&showCancelButton=true",
                    {
                        itemKind: "type",
                        itemId: meta.itemType,
                        destination: meta.nodeRef,
                        mode: "create",
                        submitType: "json"
                    });

                var createDetails = new Alfresco.module.SimpleDialog(this.id + "-createDetails");
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

                                YAHOO.Bubbling.fire("refreshTemplate",
                                    {
                                        newTemplateId: response.json.persistedObject
                                    });

                                YAHOO.Bubbling.fire("refreshButtonState", {
                                    buttons: {
                                        "newTemplateSaveButton": "enabled"
                                    }
                                });
                            },
                            scope: this
                        },
                        onFailure: {
                            fn: function DataGrid_onActionCreate_failure(response) {
                                Alfresco.util.PopupManager.displayMessage(
                                    {
                                        text: this.msg("message.save.failure")
                                    });
                            },
                            scope: this
                        }
                    }).show();
            }
        }, true);
})();