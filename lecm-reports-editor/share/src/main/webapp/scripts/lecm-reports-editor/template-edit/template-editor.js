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
        YAHOO.Bubbling.on("copyTemplateToRepository", this._onCopyToRepository, this);
        return this;
    };

    YAHOO.extend(LogicECM.module.ReportsEditor.TemplateEditor, Alfresco.component.Base, {
        reportId: null,
        templateId: null,
        options: {},
        defaultFilter: {},
        mayCopyToRepository: false,

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

        _onCopyToRepository: function (layer, args) {
            if (this.templateId) {
                this.copyTemplate(this.templateId, this.reportId, LogicECM.module.ReportsEditor.SETTINGS.templatesContainer);
            } else {
                Alfresco.util.PopupManager.displayMessage({
                    text: "Нет активного шаблона"
                });
            }
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
                                YAHOO.Bubbling.fire("refreshButtonState", {
                                    buttons: {
                                        "newTemplateSaveButton": "disabled"
                                    }
                                });
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
        }
    });

})();