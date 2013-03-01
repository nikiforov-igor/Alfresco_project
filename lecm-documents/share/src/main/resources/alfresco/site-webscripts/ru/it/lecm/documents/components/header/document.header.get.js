<import resource="classpath:/alfresco/templates/org/alfresco/import/alfresco-util.js">

function main() {
    AlfrescoUtil.param("nodeRef");
    var nodeDetails = AlfrescoUtil.getNodeDetails(model.nodeRef);
    if (nodeDetails) {
        model.item = nodeDetails.item;
        model.documentName = nodeDetails.item.node.properties["cm:name"];

        var aspects = geAspects(model.nodeRef);
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

function geAspects(nodeRef, defaultValue) {
    var url = '/slingshot/doclib/aspects/node/' + nodeRef.replace('://', '/');
    var result = remote.connect("alfresco").get(url);
    if (result.status != 200)
    {
        if (defaultValue !== undefined)
        {
            return defaultValue;
        }
        AlfrescoUtil.error(result.status, 'Could not blog details for post ' + nodeRef);
    }
    return eval('(' + result + ')').current;
}

main();