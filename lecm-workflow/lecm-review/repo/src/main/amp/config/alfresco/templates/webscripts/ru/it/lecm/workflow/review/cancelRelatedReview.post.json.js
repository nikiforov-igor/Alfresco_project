<import resource="classpath:/alfresco/templates/webscripts/ru/it/lecm/workflow/review/form/review-ts.form.lib.js">

(function () {
    var initiatingDocument = search.findNode(json.get('initiatingDocumentRef')),
        reviewItem = search.findNode(json.get('nodeRef'));

    model.success = cancelReviewFromInitiatingDocument(initiatingDocument, reviewItem);
})();
