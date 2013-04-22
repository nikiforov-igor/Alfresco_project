var nodeRef = args["nodeRef"];

var links = documentConnection.getConnectionsWithDocument(nodeRef);
var resultLinks = [];
for each (var link in links) {
    resultLinks.push({
        label: link.assocs["lecm-connect:primary-document-assoc"][0].properties["lecm-document:present-string"],
        nodeRef: link.assocs["lecm-connect:primary-document-assoc"][0].nodeRef.toString()
    });
}

var resultAttachments = [];

var categories = documentAttachments.getCategories(nodeRef);
if (categories != null) {
    for (var i = 0; i < categories.length; i++) {
        var attachments = categories[i].getChildren();
        if (attachments != null && attachments.length > 0) {
            for (var j = 0; j < attachments.length; j++) {
                resultAttachments.push({
                    label: attachments[j].properties["cm:name"],
                    nodeRef: attachments[j].nodeRef.toString()
                });
            }
        }
    }
}

result = {
    links: resultLinks,
    attachments: resultAttachments
};

model.result = jsonUtils.toJSONString(result);
