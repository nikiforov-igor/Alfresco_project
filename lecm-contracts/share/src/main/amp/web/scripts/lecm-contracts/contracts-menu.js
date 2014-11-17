if (typeof LogicECM == "undefined" || !LogicECM) {
    LogicECM = {};
}

LogicECM.module = LogicECM.module || {};

LogicECM.module.Contracts = LogicECM.module.Contracts|| {};

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

            var contractsArchiveMenu = new YAHOO.widget.Menu("contractsArchiveMenu");
            contractsArchiveMenu.addItems([
                {
                    text: "Список договоров",
                    onclick: {
                        fn: function () {
                            window.location.href = window.location.protocol + "//" + window.location.host +
                                Alfresco.constants.URL_PAGECONTEXT + "contracts-archive-list";
                        },
                        scope: this
                    }
                },
                {
                    text: "Документы к договорам",
                    onclick: {
                        fn: function () {
                            window.location.href = window.location.protocol + "//" + window.location.host +
                                Alfresco.constants.URL_PAGECONTEXT + "contracts-docs-archive-list";
                        },
                        scope: this
                    }
                }
            ]);

            contractsArchiveMenu.render("contracts-archive-menu");

            var onClickContractsMenuButton = function(e) {
                contractsArchiveMenu.moveTo(e.clientX, e.clientY);
                contractsArchiveMenu.show();
            };

            this.widgets.archiveButton = Alfresco.util.createYUIButton(this, "archiveBtn", onClickContractsMenuButton, {});
        }
    });
})();
