<import resource="classpath:/alfresco/site-webscripts/ru/it/lecm/documents/utils/document-utils.js">
<import resource="classpath:/alfresco/templates/org/alfresco/import/alfresco-util.js">

function main() {
    AlfrescoUtil.param("nodeRef");
    var nodeDetails = DocumentUtils.getNodeDetails(model.nodeRef);
    var isAdmin = false;
    if (nodeDetails) {
        model.item = nodeDetails.item;

        var presentString = nodeDetails.item.node.properties["lecm-document:present-string"];
        if (presentString != null) {
            model.documentName = presentString;
        } else {
            model.documentName = nodeDetails.item.displayName;
        }

        var listPresentString = nodeDetails.item.node.properties["lecm-document:list-present-string"];
        if (listPresentString != null) {
            model.listPresent = listPresentString;
        }
        var aspects = nodeDetails.item.node.aspects;
        var subscribed = false;
        if (aspects != null) {
            for (var i = 0; i < aspects.length; i++) {
                if (aspects[i] == "lecm-subscr-aspects:subscribed") {
                    subscribed = true;
                    break;
                }
            }
        }

        model.subscribed = subscribed;

        // Get the user name of the person to get
        var login = user.id;
        var url = '/lecm/security/api/isAdmin?login=' + encodeURI(login);
        var isAdminResponse = remote.connect("alfresco").get(url);
        if (isAdminResponse.status == 200) {
            var result = eval('(' + isAdminResponse + ')');
            isAdmin = result.isAdmin;
        }
    } else {
		var accessInfo = DocumentUtils.getNodeAccess(model.nodeRef, user.id);
		if (accessInfo) {
			if (accessInfo.exists) {
				if (!accessInfo.hasReadPermissions) {
					model.accessMsg = "У вас нет прав на этот документ. Обратитесь к администратору.";
				}
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
    model.isAdmin = isAdmin;
}

main();