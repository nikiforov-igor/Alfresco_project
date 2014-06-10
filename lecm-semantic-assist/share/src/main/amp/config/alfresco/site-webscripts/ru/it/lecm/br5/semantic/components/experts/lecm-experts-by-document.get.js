<import resource="classpath:/alfresco/templates/org/alfresco/import/alfresco-util.js">
<import resource="classpath:/alfresco/site-webscripts/ru/it/lecm/documents/utils/permission-utils.js">

function main() {
    AlfrescoUtil.param("nodeRef");
	model.expertsList = getExperts(model.nodeRef);
}

function getExperts(nodeRef) {
    var url = '/lecm/br5/semantic/experts/experts-by-document?sDocument=' + nodeRef;
    var result = remote.connect("alfresco").get(url);
    if (result.status == 200) {
		return eval('(' + result + ')');
    }
	return null;
}

main();