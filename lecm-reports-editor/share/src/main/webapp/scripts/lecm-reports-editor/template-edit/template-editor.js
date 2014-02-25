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
        YAHOO.Bubbling.on("copyTemplateToReport", this._onCopyToReport, this);

        return this;
    };

    YAHOO.extend(LogicECM.module.ReportsEditor.TemplateEditor, Alfresco.component.Base, {
        reportId: null,

        templatesGrid: null,

        setReportId: function (reportId) {
            this.reportId = reportId;
        },

        onInitDataGrid: function (layer, args) {
            this.templatesGrid = args[1].datagrid;
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