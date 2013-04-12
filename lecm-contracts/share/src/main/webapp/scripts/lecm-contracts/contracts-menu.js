(function () {
    LogicECM.module.Contracts.Menu = function (htmlId) {
        return LogicECM.module.Contracts.Menu.superclass.constructor.call(this, "LogicECM.module.Contracts.Menu", htmlId, ["button"]);
    };

    YAHOO.extend(LogicECM.module.Contracts.Menu, Alfresco.component.Base, {
        onReady: function () {
            var onRecordsClick1 = function (e) {
                window.location.href = window.location.protocol + "//" + window.location.host +
                    Alfresco.constants.URL_PAGECONTEXT + "contracts-main";
            };
            this.widgets.contractsMainButton = Alfresco.util.createYUIButton(this, "mainBtn", onRecordsClick1, {});

            var onRecordsClick2 = function (e) {
                window.location.href = window.location.protocol + "//" + window.location.host +
                    Alfresco.constants.URL_PAGECONTEXT + "contracts-list";
            };
            this.widgets.contractsListButton = Alfresco.util.createYUIButton(this, "listBtn", onRecordsClick2, {});

            var onRecordsClick3 = function (e) {
                window.location.href = window.location.protocol + "//" + window.location.host +
                    Alfresco.constants.URL_PAGECONTEXT + "contracts-documents";
            };
            this.widgets.contractsDocumentButton = Alfresco.util.createYUIButton(this, "documentsBtn", onRecordsClick3, {});

            var onRecordsClick4 = function (e) {
                window.location.href = window.location.protocol + "//" + window.location.host +
                    Alfresco.constants.URL_PAGECONTEXT + "contracts-reports";
            };
            this.widgets.contractsReportsButton = Alfresco.util.createYUIButton(this, "reportsBtn", onRecordsClick4, {});
        }
    });
})();
