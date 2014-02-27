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
        return this;
    };

    YAHOO.extend(LogicECM.module.ReportsEditor.TemplateEditToolbar, Alfresco.component.Base, {
        reportId: null,

        templatesGrid: null,

        toolbarButtons: {},

        doubleClickLock: false,

        setReportId: function (reportId) {
            this.reportId = reportId;
        },

        onInitDataGrid: function (layer, args) {
            this.templatesGrid = args[1].datagrid;
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

            var createDetails = new Alfresco.module.SimpleDialog(this.id + "-createDetails-" +  Alfresco.util.generateDomId());
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
                                    bubblingLabel: "templates"
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
            alert("Не реализовано!");
        },

        _onNewTemplateFromSource: function () {
            this.showCreateDialog({itemType: "lecm-rpeditor:reportTemplate", nodeRef: this.reportId, formId: "createFromDataSource"});
        }
    });
})();