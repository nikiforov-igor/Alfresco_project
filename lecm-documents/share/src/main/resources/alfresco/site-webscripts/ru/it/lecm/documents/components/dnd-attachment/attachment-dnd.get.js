<import resource="classpath:/alfresco/site-webscripts/ru/it/lecm/documents/utils/document-utils.js">
<import resource="classpath:/alfresco/templates/org/alfresco/import/alfresco-util.js">

function main() {
	AlfrescoUtil.param("nodeRef");
	var nodeDetails = DocumentUtils.getNodeDetails(model.nodeRef);
	if (nodeDetails) {
		model.item = nodeDetails.item;
		model.node = nodeDetails.item.node;
	}
}

main();