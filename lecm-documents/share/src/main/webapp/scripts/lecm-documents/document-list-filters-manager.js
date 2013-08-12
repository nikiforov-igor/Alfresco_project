(function () {

    LogicECM.module.Documents.FiltersManager = function () {
        // Preferences service
        this.preferences = new Alfresco.service.Preferences();

        return this;
    };

    YAHOO.lang.augmentObject(LogicECM.module.Documents.FiltersManager.prototype, {
        PREFERENCE_DOCUMENTS: "ru.it.lecm.documents",
        PREFERENCE_ARCHIVE_DOCUMENTS: "ru.it.lecm.documents.archive",
        preferences: null,

        options: {
            docType: "lecm-document:base",
            isDocListPage: false,
            archiveDocs: false
        },

        setOptions: function (obj) {
            this.options = YAHOO.lang.merge(this.options, obj);
            return this;
        },

        _buildPreferencesKey: function (filterId) {
            var opt = this.options;
            return (this.options.archiveDocs ? this.PREFERENCE_ARCHIVE_DOCUMENTS : this.PREFERENCE_DOCUMENTS)+ "." + opt.docType.split(":").join("_") + (filterId ? ("." + filterId): "");
        },

        save: function (filter, value, reload) {
            var success;
            var isDocListPage = this.options.isDocListPage;
            var type = this.options.docType;
            if (reload){
                success = {
                    fn: function () {
                        window.location.href = window.location.protocol + "//" + window.location.host + window.location.pathname + (isDocListPage ? "?doctype=" + type + "&" : "?") + value + location.hash;
                    }
                } ;
            }

            this.preferences.set(this._buildPreferencesKey(filter), value, {successCallback: success});
        }
    }, true);
})();