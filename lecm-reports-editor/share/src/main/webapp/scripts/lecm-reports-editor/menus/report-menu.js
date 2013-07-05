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

        setReportId: function(reportId){
            this.reportId = reportId;
        },

        onReady: function () {
            var context = this;

            var onButtonClick1 = function (e) {
                window.location.href = window.location.protocol + "//" + window.location.host +
                    Alfresco.constants.URL_PAGECONTEXT + "reports-editor";
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

        }
    });
})();
