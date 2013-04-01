<import resource="classpath:/alfresco/templates/org/alfresco/import/alfresco-util.js">
<import resource="classpath:/alfresco/site-webscripts/ru/it/lecm/documents/utils/permission-utils.js">

function main() {
    AlfrescoUtil.param("nodeRef");

	model.hasViewListPerm = hasPermission(model.nodeRef, '_lecmPerm_ContentList');
	if (model.hasViewListPerm) {
        model.categories = getCategories(model.nodeRef).categories;
	}
}

function getCategories(nodeRef, defaultValue) {
    var url = '/lecm/document/attachments/api/categories?documentNodeRef=' + nodeRef;
    var result = remote.connect("alfresco").get(url);
    if (result.status != 200) {
        if (defaultValue !== undefined) {
            return defaultValue;
        }
        AlfrescoUtil.error(result.status, 'Could not get attachments for node ' + nodeRef);
    }
    return eval('(' + result + ')');
}

main();