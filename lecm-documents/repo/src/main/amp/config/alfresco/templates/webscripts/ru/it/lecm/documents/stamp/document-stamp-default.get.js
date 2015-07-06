<import resource="classpath:/alfresco/templates/webscripts/ru/it/lecm/documents/stamp/document-stamp-lib.js">

(function() {
    var categories = documentAttachments.getCategories(args["nodeRef"]);
    var document = null;
    for each (var category in categories) {
        for each (var attach in category.children) {
            if (attach.mimetype == "application/pdf") {
                document = attach;
            }
        }
    }

    var result = {}
    if (document != null) {
        result = getDocumentStamp(document, args["code"]);
    }
    model.result = jsonUtils.toJSONString(result);

})();
