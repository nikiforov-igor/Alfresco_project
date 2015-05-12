if (typeof LogicECM == "undefined" || !LogicECM) {
    LogicECM = {};
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
                var prefs = localStorage.getItem(this._buildKey());
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

                localStorage.setItem(this._buildKey(), YAHOO.lang.JSON.stringify(result));
            },

            _buildKey: function () {
                return this.PREFERENCE_KEY + ".last." + Alfresco.constants.USERNAME;
            }


        });
})();