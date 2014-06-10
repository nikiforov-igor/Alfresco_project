<import resource="classpath:/alfresco/site-webscripts/ru/it/lecm/documents/utils/document-utils.js">
<import resource="classpath:/alfresco/templates/org/alfresco/import/alfresco-util.js">
<import resource="classpath:/alfresco/site-webscripts/ru/it/lecm/documents/utils/permission-utils.js">

function main() {
    AlfrescoUtil.param("nodeRef");
    var nodeDetails = DocumentUtils.getNodeDetails(model.nodeRef);
    var hasPerm = hasPermission(model.nodeRef, PERM_SET_RATE);

    if (nodeDetails && hasPerm) {
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