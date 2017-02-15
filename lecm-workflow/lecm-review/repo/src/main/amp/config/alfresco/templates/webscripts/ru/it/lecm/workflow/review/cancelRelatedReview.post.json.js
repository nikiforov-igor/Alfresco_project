/* global utils, notifications, json, model */

(function () {
    var success = false;

    var initiatingDocument = search.findNode(json.get('initiatingDocumentRef')),
        reviewItem = search.findNode(json.get('nodeRef'));

    if (initiatingDocument && reviewItem) {
        initiatingDocument.removeAssociation(reviewItem, "lecm-review-aspects:related-review-records-assoc");
        success = true;
    }

    model.success = success;

})();
