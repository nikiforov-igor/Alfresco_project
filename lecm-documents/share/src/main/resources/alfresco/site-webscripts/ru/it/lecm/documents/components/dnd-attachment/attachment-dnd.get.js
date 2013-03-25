<import resource="classpath:/alfresco/site-webscripts/ru/it/lecm/documents/utils/document-utils.js">

function main() {
	AlfrescoUtil.param("nodeRef");
	var nodeDetails = AlfrescoUtil.getNodeDetails(model.nodeRef);
	if (nodeDetails) {
		model.item = nodeDetails.item;
		model.node = nodeDetails.item.node;
	}
}

main();