(function () {

    LogicECM.module.Documents.FiltersManager = function () {
        // Preferences service
        this.preferences = new Alfresco.service.Preferences();

        return this;
    };

    YAHOO.lang.augmentObject(LogicECM.module.Documents.FiltersManager.prototype, {
        PREFERENCE_DOCUMENTS: "ru.it.lecm.documents",
        preferences: null,

        options: {
            docType: "lecm-document:base",
            isDocListPage: false
        },

        setOptions: function (obj) {
            this.options = YAHOO.lang.merge(this.options, obj);
            return this;
        },

        _buildPreferencesKey: function (suffix) {
            var opt = this.options;
            return this.PREFERENCE_DOCUMENTS + "." + opt.docType.split(":").join("_") + (suffix ? suffix : "");
        },

        save: function (filter, value, reload) {
            var success;
            var isDocListPage = this.options.isDocListPage;
            var type = this.options.docType;
            if (reload){
                success = {
                    fn: function () {
                        window.location.href = window.location.protocol + "//" + window.location.host + window.location.pathname + (isDocListPage ? "?doctype=" + type + "&" : "?") + value;
                    }
                } ;
            }

            this.preferences.set(this._buildPreferencesKey(filter), value, {successCallback: success});
        }
    }, true);
})();