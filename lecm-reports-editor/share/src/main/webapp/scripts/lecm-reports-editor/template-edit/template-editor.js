/**
 * ReportsEditor module.
 *
 * @namespace LogicECM.module
 * @class LogicECM.module.ReportsEditor
 */
(function () {
    LogicECM.module.ReportsEditor.TemplateEditor = function (htmlId) {
        LogicECM.module.ReportsEditor.TemplateEditor.superclass.constructor.call(
            this,
            "LogicECM.module.ReportsEditor.TemplateEditor",
            htmlId,
            ["button", "container", "connection"]);

        YAHOO.Bubbling.on("initDatagrid", this.onInitDataGrid, this);
        YAHOO.Bubbling.on("refreshTemplate", this._onRefreshTemplate, this);
        YAHOO.Bubbling.on("copyTemplateToReport", this._onCopyToReport, this);
        return this;
    };

    YAHOO.extend(LogicECM.module.ReportsEditor.TemplateEditor, Alfresco.component.Base, {
        reportId: null,
        templateId: null,

        options: {},

        defaultFilter: {},

        mayCopyToRepository: false,

        toolbarButtons: {},

        isNewTemplate: false,

        isCopy: false,

        formMode: "create",

        itemKind: "type",

        items: [],

        formId: "",

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

        onInitDataGrid: function () {
            //начальная загрузка грида
            this._populateTemplateList(this.widgets.types.value);
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
                var defaultMsg = this.msg("label.create-template.title");
                Alfresco.util.populateHTML(
                    [ p_dialog.id + "-form-container_h", defaultMsg ]
                );

                Dom.addClass(p_dialog.id + "-form", "metadata-form-edit");

                if (response) {
                    // обновим форму данными шаблона
                }
                this.items = p_dialog.form.validations;
            };

            var templateUrl = YAHOO.lang.substitute(Alfresco.constants.URL_SERVICECONTEXT + "lecm/components/form?itemKind={itemKind}&itemId={itemId}&destination={destination}&mode={mode}&submitType={submitType}&formId={formId}&showCancelButton=true",
                {
                    itemKind: this.itemKind,
                    itemId: meta.itemType,
                    destination: meta.nodeRef,
                    mode: this.formMode,
                    submitType: "json",
                    formId: this.formId
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
                    doBeforeFormSubmit: {
                        fn: function InstantAbsence_doBeforeSubmit() {
                            if (this.isCopy) {
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

                            this.toolbarButtons.newTemplateSaveButton.set("disabled", this.isCopy);
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
        },

        _onRefreshTemplate: function (layer, args) {
            var obj = args[1];
            if ((obj !== null) && (obj.newTemplateId !== null)) {
                this.setTemplateId(obj.newTemplateId);
            }
            var reportId = this.reportId;
            var htmlId = reportId.replace("workspace://SpacesStore/", "").replace("-", "");
            Alfresco.util.Ajax.request(
                {
                    url: Alfresco.constants.URL_SERVICECONTEXT + "components/form",
                    dataObj: {
                        htmlid: htmlId,
                        itemKind: "node",
                        itemId: reportId,
                        formId: "template-info",
                        mode: "view",
                        submitType: "json",
                        showSubmitButton: "false",
                        showCancelButton: "false"
                    },
                    successCallback: {
                        fn: function (response) {
                            var formEl = Dom.get(this.id + "-reportTemplateInfo");
                            formEl.innerHTML = response.serverResponse.responseText;
                            Dom.setStyle(this.id + "-footer", "opacity", "1");
                        },
                        scope: this
                    },
                    failureMessage: "message.failure",
                    execScripts: true
                });
        },

        draw: function () {
            // инициация выпадающего списка
            this.widgets.types = Alfresco.util.createYUIButton(this, "reportType", this._onTypeFilterChanged,
                {
                    type: "menu",
                    menu: "reportType-menu",
                    lazyloadmenu: false
                });

            // загрузка начального значения
            if (this.defaultFilter && this.defaultFilter.name && this.defaultFilter.name.length > 0) {
                this.widgets.types.set("label", this.defaultFilter.name);
                this.widgets.types.value = this.defaultFilter.nodeRef;
            }

            // инициализация кнопок тулбара
            this._initToolbarButtons();

            Dom.setStyle(this.id + "-toolbar-body", "visibility", "visible");
        },

        _onTypeFilterChanged: function (p_sType, p_aArgs) {
            var menuItem = p_aArgs[1];
            if (menuItem) {
                this.widgets.types.set("label", menuItem.cfg.getProperty("text"));
                this.widgets.types.value = menuItem.value;
                this._populateTemplateList(this.widgets.types.value);
            }
        },

        _populateTemplateList: function (typeFilter) {
            YAHOO.Bubbling.fire("activeGridChanged", {
                datagridMeta: {
                    itemType: "lecm-rpeditor:reportTemplate",
                    nodeRef: LogicECM.module.ReportsEditor.SETTINGS.templatesContainer,
                    actionsConfig: {
                        fullDelete: true,
                        trash: false
                    },
                    sort: "cm:name|desc",
                    searchConfig: {
                        filter: '@lecm\\-rpeditor:reportTemplateType\\-ref:\"' + typeFilter + '\"'
                    }
                },
                bubblingLabel: "template-edit"
            });
        },

        copyTemplate: function (templateId, from, to) {
            var copyRefs = [];
            copyRefs.push(templateId);

            var copyTo = new Alfresco.util.NodeRef(to);

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
                                YAHOO.Bubbling.fire("refreshTemplate", {
                                    newTemplateId: templateId
                                });
                                this.toolbarButtons.newTemplateSaveButton.set("disabled", true);
                            }
                        },
                        scope: this
                    },
                    failureCallback: {
                        fn: function () {
                            Alfresco.util.PopupManager.displayMessage({
                                text: "Не удалось скопировать шаблон"
                            });
                        }
                    },
                    execScripts: true,
                    requestContentType: "application/json",
                    responseContentType: "application/json"
                });
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
        },

        _onNewTemplate: function () {
            if (this.templateId) {
                this.isCopy = false;
                this.formMode = "create";
                this.itemKind = "type";
                this.formId = "";
                this.showCreateDialog({itemType: "lecm-rpeditor:reportTemplate", nodeRef: this.reportId}, false);
            } else {
                Alfresco.util.PopupManager.displayMessage({
                    text: "Нет активного шаблона"
                });
            }
        },

        _onNewTemplateFromSource: function () {
            alert("Не реализовано");
            //this.showCreateDialog({itemType: "lecm-rpeditor:reportTemplate", nodeRef: this.reportId}, true);
        },

        _onCopyToRepository: function (layer, args) {
            this.isCopy = true;
            this.formMode = "edit";
            this.itemKind = "node";
            this.formId = "copy-to-report";
            this.showCreateDialog({itemType: this.templateId, nodeRef: this.reportId}, false);
        },

        _onCopyToReport: function (layer, args) {
            var obj = args[1];
            if ((obj !== null) && (obj.templateId !== null)) {
                if (obj.templateId != this.templateId) {
                    this.copyTemplate(obj.templateId, LogicECM.module.ReportsEditor.SETTINGS.templatesContainer, this.reportId);
                } else {
                    Alfresco.util.PopupManager.displayMessage({
                        text: "Данный шаблон уже выбран!"
                    });
                }
            }
        }
    });

})();