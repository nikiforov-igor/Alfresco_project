(function () {
    LogicECM.module.Errands.Menu = function (htmlId) {
        return LogicECM.module.Errands.Menu.superclass.constructor.call(this, "LogicECM.module.Errands.Menu", htmlId, ["button"]);
    };

    YAHOO.extend(LogicECM.module.Errands.Menu, Alfresco.component.Base, {
        onReady: function () {
            var onRecordsClick1 = function (e) {
                window.location.href = window.location.protocol + "//" + window.location.host +
                    Alfresco.constants.URL_PAGECONTEXT + "errands-list";
            };
            this.widgets.contractsMainButton = Alfresco.util.createYUIButton(this, "listBtn", onRecordsClick1, {});

            var onRecordsClick2 = function (e) {
                window.location.href = window.location.protocol + "//" + window.location.host +
                    Alfresco.constants.URL_PAGECONTEXT + "errands-reports";
            };
            this.widgets.contractsListButton = Alfresco.util.createYUIButton(this, "reportsBtn", onRecordsClick2, {});

            var onRecordsClick3 = function (e) {
                window.location.href = window.location.protocol + "//" + window.location.host +
                    Alfresco.constants.URL_PAGECONTEXT + "errands-tasks";
            };
            this.widgets.contractsDocumentButton = Alfresco.util.createYUIButton(this, "tasksBtn", onRecordsClick3, {});
        }
    });
})();
