<import resource="classpath:/alfresco/site-webscripts/ru/it/lecm/documents/utils/document-utils.js">

function main() {
    AlfrescoUtil.param("nodeRef");
    var nodeDetails = AlfrescoUtil.getNodeDetails(model.nodeRef);
    if (nodeDetails) {
        model.item = nodeDetails.item;

        var aspects = nodeDetails.item.node.aspects;
        var rateable = false;

        if (aspects != null) {
            for (var i = 0; i < aspects.length; i++) {
                if (aspects[i] == "lecm-document-aspects:rateable") {
                    rateable = true;
                    break;
                }
            }
        }

        model.rateable = rateable;
    }
}

main();