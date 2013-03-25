<import resource="classpath:/alfresco/site-webscripts/ru/it/lecm/documents/utils/document-utils.js">
<import resource="classpath:/alfresco/templates/org/alfresco/import/alfresco-util.js">

function main() {
    AlfrescoUtil.param("nodeRef");
	var nodeDetails = DocumentUtils.getNodeDetails(model.nodeRef);
	if (nodeDetails)
	{
		model.item = nodeDetails.item;
		model.node = nodeDetails.item.node;

		if (nodeDetails.item.parent != null) {
			var attachmentsFolder = DocumentUtils.getNodeDetails(nodeDetails.item.parent.nodeRef);
			if (attachmentsFolder != null && attachmentsFolder.item.parent != null) {
				var categoryFolder = DocumentUtils.getNodeDetails(attachmentsFolder.item.parent.nodeRef);
				if (categoryFolder != null && categoryFolder.item.parent != null){
					var documentFolder = DocumentUtils.getNodeDetails(categoryFolder.item.parent.nodeRef);
					if (documentFolder != null) {
						model.documentNodeRef = documentFolder.item.node.nodeRef;
				        var presentString = documentFolder.item.node.properties["lecm-document:present-string"];
				        if (presentString != null) {
				            model.documentName = presentString;
				        } else {
				            model.documentName = documentFolder.item.displayName;
				        }
					}
				}
			}
		}
	}
}

main();