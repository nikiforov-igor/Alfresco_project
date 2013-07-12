var nodeRef = args["nodeRef"];

var links = documentConnection.getConnections(nodeRef);
var resultLinks = [];
for each (var link in links) {
    resultLinks.push({
        label: link.assocs["lecm-connect:connected-document-assoc"][0].properties["lecm-document:present-string"],
        nodeRef: link.assocs["lecm-connect:connected-document-assoc"][0].nodeRef.toString()
    });
}

var resultAttachments = [];

var categories = documentAttachments.getCategories(nodeRef);
if (categories != null) {
    for (var i = 0; i < categories.length; i++) {
        var categoryAttachments = [];
        var attachments = categories[i].getChildren();
        if (attachments != null && attachments.length > 0) {
            for (var j = 0; j < attachments.length; j++) {
                categoryAttachments.push({
                    label: attachments[j].properties["cm:name"],
                    nodeRef: attachments[j].nodeRef.toString()
                });
            }
            resultAttachments.push({
                name: categories[i].properties["cm:name"],
                items: categoryAttachments
            })
        }
    }
}

result = {
    links: resultLinks,
    attachments: resultAttachments
};

model.result = jsonUtils.toJSONString(result);
