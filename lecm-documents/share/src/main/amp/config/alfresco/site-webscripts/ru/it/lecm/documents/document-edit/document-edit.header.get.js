<import resource="classpath:/alfresco/site-webscripts/ru/it/lecm/documents/utils/document-utils.js">
<import resource="classpath:/alfresco/templates/org/alfresco/import/alfresco-util.js">
<import resource="classpath:/alfresco/site-webscripts/ru/it/lecm/documents/utils/permission-utils.js">

function main() {
    AlfrescoUtil.param("nodeRef");
    var nodeDetails = DocumentUtils.getNodeDetails(model.nodeRef);
    if (!nodeDetails || !hasPermission(model.nodeRef, PERM_ATTR_EDIT)) {
		var accessInfo = DocumentUtils.getNodeAccess(model.nodeRef, user.id);
		if (accessInfo) {
			if (accessInfo.exists) {
				model.accessMsg = "У вас нет прав на этот документ. Обратитесь к администратору.";
			} else {
				if (accessInfo.removed) {
					model.accessMsg = "Документ был удален.";
				} else {
					model.accessMsg = "Документ не существует.";
				}
			}
		} else {
			model.accessMsg = "Документ не найден. Он мог быть удален. Или у вас нет прав. Обратитесь к администратору";
		}
	}
}

main();