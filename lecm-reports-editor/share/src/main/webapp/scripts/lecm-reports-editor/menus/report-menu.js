/**
 * ReportsEditor module.
 *
 * @namespace LogicECM.module
 * @class LogicECM.module.ReportsEditor
 */
(function () {

    LogicECM.module.ReportsEditor.ReportMenu = function (htmlId) {
        return LogicECM.module.ReportsEditor.ReportMenu.superclass.constructor.call(
            this,
            "LogicECM.module.ReportsEditor.ReportMenu",
            htmlId,
            ["button"]);
    };

    YAHOO.extend(LogicECM.module.ReportsEditor.ReportMenu, Alfresco.component.Base, {
        reportId: null,
        splashScreen: null,

        setReportId: function(reportId){
            this.reportId = reportId;
        },

        onReady: function () {
            var context = this;

            var onButtonClick1 = function (e) {
                if (LogicECM.module.ReportsEditor.REPORT_SETTINGS && LogicECM.module.ReportsEditor.REPORT_SETTINGS.isSubReport == "true") {
                    window.location.href = window.location.protocol + "//" + window.location.host +
                        Alfresco.constants.URL_PAGECONTEXT + "report-settings?reportId=" + LogicECM.module.ReportsEditor.REPORT_SETTINGS.parentReport;
                } else {
                    window.location.href = window.location.protocol + "//" + window.location.host +
                        Alfresco.constants.URL_PAGECONTEXT + "reports-editor";
                }
            };
            this.widgets.reportsListBtn = Alfresco.util.createYUIButton(this, "reportsListBtn", onButtonClick1, {});

            var onButtonClick2 = function (e) {
                window.location.href = window.location.protocol + "//" + window.location.host +
                    Alfresco.constants.URL_PAGECONTEXT + "report-settings?reportId=" + context.reportId;
            };
            this.widgets.reportsSettingsBtn = Alfresco.util.createYUIButton(this, "reportSettingsBtn", onButtonClick2, {});

            var onButtonClick3 = function (e) {
                window.location.href = window.location.protocol + "//" + window.location.host +
                    Alfresco.constants.URL_PAGECONTEXT + "report-source-edit?reportId=" + context.reportId;
            };
            this.widgets.editDataSourceBtn = Alfresco.util.createYUIButton(this, "editDataSourceBtn", onButtonClick3, {});

            var onButtonClick4 = function (e) {
                window.location.href = window.location.protocol + "//" + window.location.host +
                    Alfresco.constants.URL_PAGECONTEXT + "report-template-edit?reportId=" + context.reportId;
            };
            this.widgets.editTemplateBtn = Alfresco.util.createYUIButton(this, "editTemplateBtn", onButtonClick4, {});

            var onButtonClick5 = function (e) {
                context._deployReport();
            };
            this.widgets.deployReportBtn = Alfresco.util.createYUIButton(this, "deployReportBtn", onButtonClick5, {});

            var onButtonClick6 = function (e) {
                window.location.href = window.location.protocol + "//" + window.location.host +
                    Alfresco.constants.URL_PAGECONTEXT + "report-subreports?reportId=" + context.reportId;
            };
            this.widgets.subReportsBtn = Alfresco.util.createYUIButton(this, "subReportsBtn", onButtonClick6, {});

        },

        _deployReport: function () {
            var me = this;
            Alfresco.util.PopupManager.displayPrompt({
                title: "Регистрация отчета",
                text: "Вы действительно хотите добавить отчет в систему?",
                buttons: [
                    {
                        text: "Да",
                        handler: function dlA_onActionDeploy() {
                            this.destroy();
                            var sUrl = Alfresco.constants.PROXY_URI + "/lecm/reports/rptmanager/deployReport?reportDescNode={reportDescNode}";
                            sUrl = YAHOO.lang.substitute(sUrl, {
                                reportDescNode: me.reportId
                            });
                            me._showSplash();
                            var callback = {
                                success: function (oResponse) {
                                    oResponse.argument.parent._hideSplash();
                                    Alfresco.util.PopupManager.displayMessage(
                                        {
                                            text: "Отчет зарегистрирован в системе",
                                            displayTime: 3
                                        });
                                },
                                failure: function (oResponse) {
                                    oResponse.argument.parent._hideSplash();
                                    alert(oResponse.responseText);
                                    Alfresco.util.PopupManager.displayMessage(
                                        {
                                            text: "При регистрации отчета произошла ошибка",
                                            displayTime: 3
                                        });
                                },
                                argument: {
                                    parent: me
                                },
                                timeout: 30000
                            };
                            YAHOO.util.Connect.asyncRequest('GET', sUrl, callback);
                        }
                    },
                    {
                        text: "Нет",
                        handler: function dlA_onActionDelete_cancel() {
                            this.destroy();
                        },
                        isDefault: true
                    }
                ]
            });
        },

        _showSplash: function() {
            this.splashScreen = Alfresco.util.PopupManager.displayMessage(
                {
                    text: Alfresco.util.message("label.loading"),
                    spanClass: "wait",
                    displayTime: 0
                });
        },

        _hideSplash: function() {
            YAHOO.lang.later(2000, this.splashScreen, this.splashScreen.destroy);
        }
    });
})();
