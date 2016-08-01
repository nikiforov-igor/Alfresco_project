if (typeof LogicECM == "undefined" || !LogicECM) {
    var LogicECM = {};
}

(function () {
    LogicECM.onSuccessCustomFunction = function (createdDocument, scope) {
        var _buildLastCreatedKey = function () {
            return "ru.it.lecm.documents.events.last." + Alfresco.constants.USERNAME;
        };

        if (createdDocument) {
            var lastDocuments = [];
            var prefs = localStorage.getItem(_buildLastCreatedKey());
            if (prefs != null) {
                try {
                    lastDocuments = JSON.parse(prefs);
                } catch (e) {
                }
            }

            var result = [];
            var index = 0;

            var limitDate = new Date();
            limitDate.setMinutes(limitDate.getMinutes() - 5);

            lastDocuments.forEach(function (item) {
                if (item.nodeRef != createdDocument && index < 29 && new Date(item.date).getTime() > limitDate.getTime()) {
                    result.push(item);
                    index++;
                }
            }.bind(this));

            result.unshift({
                nodeRef: createdDocument,
                date: new Date()
            });

            localStorage.setItem(_buildLastCreatedKey(), YAHOO.lang.JSON.stringify(result));
        }
    };
})();