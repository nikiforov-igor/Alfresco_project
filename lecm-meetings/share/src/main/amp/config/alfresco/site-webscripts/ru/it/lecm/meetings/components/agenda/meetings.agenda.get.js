<import resource="classpath:/alfresco/templates/org/alfresco/import/alfresco-util.js">
<import resource="classpath:/alfresco/site-webscripts/ru/it/lecm/documents/utils/permission-utils.js">
<import resource="classpath:/alfresco/site-webscripts/ru/it/lecm/documents/utils/document-utils.js">

function main() {
	AlfrescoUtil.param("nodeRef");
		var nodeDetails = DocumentUtils.getNodeDetails(model.nodeRef);
		if (nodeDetails) {
			model.agendaInfo = getAgendaInfo(model.nodeRef);
		}
}

function getAgendaInfo(nodeRef) {
	var url = '/lecm/meetings/getAgendaInfo?nodeRef=' + nodeRef;
	var result = remote.connect("alfresco").get(url);
	if (result.status != 200) {
		return null;
	}
	return eval('(' + result + ')');
}

main();
