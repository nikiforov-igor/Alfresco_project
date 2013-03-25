<import resource="classpath:/alfresco/site-webscripts/ru/it/lecm/documents/utils/document-utils.js">
<import resource="classpath:/alfresco/templates/org/alfresco/import/alfresco-util.js">

function main() {
    AlfrescoUtil.param("nodeRef");
    var nodeDetails = DocumentUtils.getNodeDetails(model.nodeRef);
    if (nodeDetails) {
        model.item = nodeDetails.item;

        var presentString = nodeDetails.item.node.properties["lecm-document:present-string"];
        if (presentString != null) {
            model.documentName = presentString;
        } else {
            model.documentName = nodeDetails.item.displayName;
        }

        var listPresentString = nodeDetails.item.node.properties["lecm-document:list-present-string"];
        if (listPresentString != null) {
            model.listPresent = listPresentString;
        }
        var aspects = nodeDetails.item.node.aspects;
        var subscribed = false;
        if (aspects != null) {
            for (var i = 0; i < aspects.length; i++) {
                if (aspects[i] == "lecm-subscr-aspects:subscribed") {
                    subscribed = true;
                    break;
                }
            }
        }

        model.subscribed = subscribed;
    }
}

main();