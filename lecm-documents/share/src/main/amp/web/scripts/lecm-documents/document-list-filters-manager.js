if (typeof LogicECM == "undefined" || !LogicECM) {
    LogicECM = {};
}

LogicECM.module = LogicECM.module || {};

LogicECM.module.Documents = LogicECM.module.Documents|| {};

(function () {

    LogicECM.module.Documents.FiltersManager = function () {
        return this;
    };

    YAHOO.lang.augmentObject(LogicECM.module.Documents.FiltersManager.prototype, {
        PREFERENCE_DOCUMENTS: "ru.it.lecm.documents",
        PREFERENCE_ARCHIVE_DOCUMENTS: "ru.it.lecm.documents.archive",

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
            return (this.options.archiveDocs ? this.PREFERENCE_ARCHIVE_DOCUMENTS : this.PREFERENCE_DOCUMENTS)+ "." + opt.docType.split(":").join("_") + (filterId ? ("." + filterId): "") + ("." + encodeURIComponent(LogicECM.currentUser)) ;
        },

        save: function (filter, value, reload) {
            var isDocListPage = this.options.isDocListPage;
            var type = this.options.docType;

            var date = new Date;
            date.setDate(date.getDate() + 30);
            LogicECM.module.Base.Util.setCookie(this._buildPreferencesKey(filter), value, {expires:date});

            if (reload){
                window.location.href = window.location.protocol + "//" + window.location.host + window.location.pathname + (isDocListPage ? "?doctype=" + type + "&" : "?") + value + location.hash;
            }
        }
    }, true);
})();