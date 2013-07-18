/**
 * ReportsEditor module.
 *
 * @namespace LogicECM.module
 * @class LogicECM.module.ReportsEditor
 */
(function () {

    LogicECM.module.ReportsEditor.MainMenu = function (htmlId) {
        return LogicECM.module.ReportsEditor.MainMenu.superclass.constructor.call(
            this,
            "LogicECM.module.ReportsEditor.MainMenu",
            htmlId,
            ["button"]);
    };

    YAHOO.extend(LogicECM.module.ReportsEditor.MainMenu, Alfresco.component.Base, {
        onReady: function () {
            var onButtonClick1 = function (e) {
                window.location.href = window.location.protocol + "//" + window.location.host +
                    Alfresco.constants.URL_PAGECONTEXT + "reports-editor";
            };
            this.widgets.reportsListBtn = Alfresco.util.createYUIButton(this, "reportsListBtn", onButtonClick1, {});

            var onButtonClick2 = function (e) {
                window.location.href = window.location.protocol + "//" + window.location.host +
                    Alfresco.constants.URL_PAGECONTEXT + "reports-editor-templates";
            };
            this.widgets.templatesListBtn = Alfresco.util.createYUIButton(this, "templatesListBtn", onButtonClick2, {});

            var onButtonClick3 = function (e) {
                window.location.href = window.location.protocol + "//" + window.location.host +
                    Alfresco.constants.URL_PAGECONTEXT + "reports-editor-sources";
            };
            this.widgets.sourcesListBtn = Alfresco.util.createYUIButton(this, "sourcesListBtn", onButtonClick3, {});


            var onButtonClick5 = function (e) {
                window.location.href = window.location.protocol + "//" + window.location.host +
                    Alfresco.constants.URL_PAGECONTEXT + "reports-editor-providers";
            };
            this.widgets.reportProvidersBtn = Alfresco.util.createYUIButton(this, "reportProvidersBtn", onButtonClick5, {});
        }
    });
})();
