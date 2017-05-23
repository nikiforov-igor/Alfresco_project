<import resource="classpath:/alfresco/templates/org/alfresco/import/alfresco-util.js">
<import resource="classpath:/alfresco/site-webscripts/ru/it/lecm/documents/utils/permission-utils.js">
<import resource="classpath:/alfresco/site-webscripts/ru/it/lecm/documents/utils/document-utils.js">

function main() {
    AlfrescoUtil.param("nodeRef");
    model.baseDocAssocName = AlfrescoUtil.param("baseDocAssocName", null);
    model.showBaseDocAttachmentsBottom = AlfrescoUtil.param("showBaseDocAttachmentsBottom", "false").toLowerCase() == "true";

	model.hasViewListPerm = hasPermission(model.nodeRef, PERM_CONTENT_LIST);
	model.hasViewAttachmentPerm = hasPermission(model.nodeRef, PERM_CONTENT_VIEW);
	model.hasAddAttachmentPerm = hasPermission(model.nodeRef, PERM_CONTENT_ADD);
	model.hasStatemachine = hasStatemachine(model.nodeRef);

	if (model.hasViewListPerm) {
        var cats = getCategories(model.nodeRef);
        if (cats) {
            model.categories = cats.categories;

	        if (model.baseDocAssocName) {
	            var obj = {
                    nodeRef: "base-document-attachments/" + model.nodeRef.replace(":/", "") + "/" + model.baseDocAssocName,
                    name: msg.get("label.attachments.base-document"),
                    path: "",
                    isReadOnly: true
                };
	            if (model.showBaseDocAttachmentsBottom) {
                    model.categories.push(obj);
                } else {
                    model.categories.unshift(obj);
                }
			}
        }
	}
};

function getCategories(nodeRef, defaultValue) {
    var url = '/lecm/document/attachments/api/categories?documentNodeRef=' + nodeRef;
    var result = remote.connect("alfresco").get(url);
    if (result.status != 200) {
        if (defaultValue !== undefined) {
            return defaultValue;
        }
        return null;
    }
    return eval('(' + result + ')');
}

main();
