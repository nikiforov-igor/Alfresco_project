if (typeof LogicECM == "undefined" || !LogicECM) {
    var LogicECM = {};
}

LogicECM.module = LogicECM.module || {};

LogicECM.module.Document = LogicECM.module.Document|| {};

(function () {
    var Dom = YAHOO.util.Dom;

    LogicECM.module.Document.ViewHistory = function DocumentAjaxContent_constructor(htmlId) {
        LogicECM.module.Document.ViewHistory.superclass.constructor.call(this, "LogicECM.module.Document.ViewHistory", htmlId, ["button", "container"]);
        return this;
    };

    YAHOO.extend(LogicECM.module.Document.ViewHistory, Alfresco.component.Base,
        {
            PREFERENCE_KEY: "ru.it.lecm.documents",

            options: {
                nodeRef: null
            },

            save: function () {
                var lastDocuments = [];
                var prefs = LogicECM.module.Base.Util.getCookie(this._buildKey());
                if (prefs != null) {
                    try {
                        lastDocuments = JSON.parse(prefs);
                    } catch (e) {
                    }
                }

                var result = [];
                var index = 0;
                lastDocuments.forEach(function (item) {
                    if (item.nodeRef != this.options.nodeRef && index < 59) {
                        result.push(item);
                        index++;
                    }
                }.bind(this));

                result.unshift({
                    nodeRef: this.options.nodeRef,
                    date: new Date()
                });

                var date = new Date;
                date.setDate(date.getDate() + 30);
                LogicECM.module.Base.Util.setCookie(this._buildKey(), YAHOO.lang.JSON.stringify(result), {expires: date});
            },

            _buildKey: function () {
                return this.PREFERENCE_KEY + ".last." + Alfresco.constants.USERNAME;
            }


        });
})();