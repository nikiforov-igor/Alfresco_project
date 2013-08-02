<import resource="classpath:/alfresco/templates/org/alfresco/import/alfresco-util.js">
<import resource="classpath:/alfresco/site-webscripts/ru/it/lecm/documents/utils/permission-utils.js">

function main() {
    AlfrescoUtil.param("nodeRef");
	model.tagsList = getTags(model.nodeRef);
}

function getTags(nodeRef) {
    var url = '/lecm/br5/semantic/cloud/document-tags-cloud?sDocument=' + nodeRef;
    var result = remote.connect("alfresco").get(url);
    if (result.status == 200) {
		return eval('(' + result + ')');
    }
	return null;
}

main();