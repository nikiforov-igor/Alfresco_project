(function () {
    LogicECM.module.Documents.Menu = function (htmlId) {
        return LogicECM.module.Documents.Menu.superclass.constructor.call(this, "LogicECM.module.Documents.Menu", htmlId, ["button"]);
    };

    YAHOO.extend(LogicECM.module.Documents.Menu, Alfresco.component.Base, {

        doctype: "lecm-document:base",

        setDocType: function(type) {
            this.doctype = type;
        },

        onReady: function () {
            var current = this;
            var onListClick = function (e) {
                window.location.href = window.location.protocol + "//" + window.location.host +
                    Alfresco.constants.URL_PAGECONTEXT + "documents-list?doctype=" + current.doctype;
            };
            this.widgets.documentsList = Alfresco.util.createYUIButton(this, "listBtn", onListClick, {});

            var onReportsClick = function (e) {
                window.location.href = window.location.protocol + "//" + window.location.host +
                    Alfresco.constants.URL_PAGECONTEXT + "reports-list?doctype=" + current.doctype;
            };
            this.widgets.reportsButton = Alfresco.util.createYUIButton(this, "reportsBtn", onReportsClick, {});

            var onArchiveClick = function (e) {
                window.location.href = window.location.protocol + "//" + window.location.host +
                    Alfresco.constants.URL_PAGECONTEXT + "archive-documents-list?doctype=" + current.doctype;
            };
            this.widgets.archiveButton = Alfresco.util.createYUIButton(this, "archiveBtn", onArchiveClick, {});
        }
    });
})();
