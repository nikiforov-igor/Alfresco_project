<import resource="classpath:/alfresco/templates/org/alfresco/import/alfresco-util.js">

function main() {
	AlfrescoUtil.param("nodeRef");
	AlfrescoUtil.param("site", null);
	AlfrescoUtil.param("rootPage", "documentlibrary");
	AlfrescoUtil.param("rootLabelId", "path.documents");
	AlfrescoUtil.param("showFavourite", "true");
	AlfrescoUtil.param("showLikes", "true");
	AlfrescoUtil.param("showComments", "true");
	AlfrescoUtil.param("showDownload", "true");
	AlfrescoUtil.param("showPath", "true");
	var nodeDetails = AlfrescoUtil.getNodeDetails(model.nodeRef, model.site);
	if (nodeDetails)
	{
		model.item = nodeDetails.item;
		model.node = nodeDetails.item.node;
		model.isContainer = nodeDetails.item.node.isContainer;
		model.paths = AlfrescoUtil.getPaths(nodeDetails, model.rootPage, model.rootLabelId);
		model.showComments = (nodeDetails.item.node.permissions.user["CreateChildren"] && model.showComments).toString();
		model.showDownload = (!model.isContainer && model.showDownload).toString();
		var count = nodeDetails.item.node.properties["fm:commentCount"];
		model.commentCount = (count != undefined ? count : null);
	}
//    AlfrescoUtil.param("nodeRef");
//    var nodeDetails = AlfrescoUtil.getNodeDetails(model.nodeRef);
//    if (nodeDetails) {
//        model.item = nodeDetails.item;
//
//        var presentString = nodeDetails.item.node.properties["lecm-document:present-string"];
//        if (presentString != null) {
//            model.documentName = presentString;
//        } else {
//            model.documentName = nodeDetails.item.displayName;
//        }
//
//        var listPresentString = nodeDetails.item.node.properties["lecm-document:list-present-string"];
//        if (listPresentString != null) {
//            model.listPresent = listPresentString;
//        }
//        var aspects = geAspects(model.nodeRef);
//        var subscribed = false;
//        if (aspects != null) {
//            for (var i = 0; i < aspects.length; i++) {
//                if (aspects[i] == "lecm-subscr-aspects:subscribed") {
//                    subscribed = true;
//                    break;
//                }
//            }
//        }
//
//        model.subscribed = subscribed;
//    }
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