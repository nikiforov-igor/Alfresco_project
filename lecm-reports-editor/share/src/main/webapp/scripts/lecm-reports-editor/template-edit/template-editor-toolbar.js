/**
 * ReportsEditor module.
 *
 * @namespace LogicECM.module
 * @class LogicECM.module.ReportsEditor
 */
(function () {
    LogicECM.module.ReportsEditor.TemplateEditToolbar = function (htmlId) {
        LogicECM.module.ReportsEditor.TemplateEditToolbar.superclass.constructor.call(
            this,
            "LogicECM.module.ReportsEditor.TemplateEditToolbar",
            htmlId,
            ["button", "container", "connection"]);

        YAHOO.Bubbling.on("initDatagrid", this.onInitDataGrid, this);
        YAHOO.Bubbling.on("refreshTemplate", this._onRefreshTemplate, this);
        return this;
    };

    YAHOO.extend(LogicECM.module.ReportsEditor.TemplateEditToolbar, Alfresco.component.Base, {
        reportId: null,

        templateId: null,

        templatesGrid: null,

        toolbarButtons: {},

        isNewTemplate: false,

        items: [],

        doubleClickLock: false,

        markAsNewTemplate: function (isNew) {
            this.isNewTemplate = isNew;
        },

        setReportId: function (reportId) {
            this.reportId = reportId;
        },

        setTemplateId: function (templateId) {
            if (templateId && templateId.length > 0) {
                this.templateId = templateId;
            }
        },

        setDefaultFilter: function (filter) {
            this.defaultFilter = filter;
        },

        onInitDataGrid: function (layer, args) {
            this.templatesGrid = args[1].datagrid;
        },

        onReady: function () {
            this._initToolbarButtons();
            Dom.setStyle(this.id + "-toolbar-body", "visibility", "visible");
        },

        showCreateDialog: function (meta, isCopy) {
            if (this.doubleClickLock) {
                return;
            }
            this.doubleClickLock = true;

            this._showCreateForm(meta, isCopy);
        },

        _showCreateForm: function (meta, isCopy) {
            var toolbar = this;

            var doBeforeDialogShow = function (p_form, p_dialog) {
                var defaultMsg = toolbar.msg("label.create-template.title");
                Alfresco.util.populateHTML(
                    [ p_dialog.id + "-form-container_h", defaultMsg ]
                );

                Dom.addClass(p_dialog.id + "-form-container", "metadata-form-edit");


                toolbar.items = p_dialog.form.validations;
                if (isCopy){
                    var htmlItem = Dom.get(toolbar.id + '-createDetails_prop_cm_name');
                    if (htmlItem) {
                        htmlItem.setAttribute("value", "");
                    }
                }
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
                    doBeforeFormSubmit: {
                        fn: function InstantAbsence_doBeforeSubmit() {
                            if (isCopy) {
                                var form = Dom.get(this.id + "-createDetails-form");
                                form.setAttribute("action", "/share/proxy/alfresco/api/type/lecm-rpeditor%3areportTemplate/formprocessor");
                                var input = document.createElement('input');
                                input.setAttribute("id", this.id + "-createDetails-form-destination");
                                input.setAttribute("type", "hidden");
                                input.setAttribute("name", "alf_destination");
                                input.setAttribute("value", LogicECM.module.ReportsEditor.SETTINGS.templatesContainer);
                                form.appendChild(input);
                                var items = this.items;
                                for (var index in items) {
                                    var htmlItem = Dom.get(items[index].fieldId + "-added");
                                    if (htmlItem == null) {
                                        htmlItem = Dom.get(items[index].fieldId + "-cntrl-added");
                                    }
                                    var value = Dom.get(items[index].fieldId).value;
                                    if (htmlItem) {
                                        htmlItem.setAttribute("value", value);
                                    }
                                }
                            }

                        },
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

                            this.doubleClickLock = false;
                        },
                        scope: this
                    },
                    onFailure: {
                        fn: function DataGrid_onActionCreate_failure(response) {
                            alert(response.json.message);
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
                }, this.id + "-toolbar-newTemplateButton");

            this.toolbarButtons.newTemplateFromSourceButton = Alfresco.util.createYUIButton(this, 'newTemplateFromSourceButton', this._onNewTemplateFromSource,
                {
                    value: 'create'
                }, this.id + "-toolbar-newTemplateFromSourceButton");

            this.toolbarButtons.newTemplateSaveButton = Alfresco.util.createYUIButton(this, 'newTemplateSaveButton', this._onCopyToRepository,
                {
                    disabled: !this.templateId || !this.isNewTemplate
                }, this.id + "-toolbar-newTemplateSaveButton");

            // Export Template
            this.toolbarButtons.exportTemplateButton = Alfresco.util.createYUIButton(this, "exportTemplateButton", this._onExportTemplate, {});
        },

        _onNewTemplate: function () {
            this.showCreateDialog(
                {
                    itemType: "lecm-rpeditor:reportTemplate",
                    nodeRef: this.reportId,
                    formMode: "create",
                    itemKind: "type",
                    formId: "uploadTemplate"
                }, false);
        },

        _onNewTemplateFromSource: function () {
            this.showCreateDialog({itemType: "lecm-rpeditor:reportTemplate", nodeRef: this.reportId, formId: "createFromDataSource"});
        },

        _onCopyToRepository: function (layer, args) {
            this.showCreateDialog(
                {
                    itemType: this.templateId,
                    nodeRef: this.reportId,
                    formMode: "edit",
                    itemKind: "node",
                    formId: "copy-to-report"
                }, true);
        },

        _onRefreshTemplate: function (layer, args) {
            var obj = args[1];
            if ((obj !== null) && (obj.newTemplateId !== null)) {
                this.templateId = obj.newTemplateId;
            }
        },

        _onExportTemplate: function () {
            document.location.href = Alfresco.constants.PROXY_URI + "/lecm/reports-editor/exportReportTemplate?reportRef=" + this.reportId;
        }
    });
})();