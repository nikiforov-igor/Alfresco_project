<import resource="classpath:/alfresco/site-webscripts/ru/it/lecm/documents/utils/document-utils.js">
<import resource="classpath:/alfresco/templates/org/alfresco/import/alfresco-util.js">

function main() {
    AlfrescoUtil.param("nodeRef");
    var nodeDetails = DocumentUtils.getNodeDetails(model.nodeRef);
    var isAdmin = false;
    model.viewUrl = "document";
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

        url = '/lecm/document/api/url/view?nodeRef=' + encodeURI(model.nodeRef);
        var viewUrl = remote.connect("alfresco").get(url);
        if (viewUrl.status == 200) {
            var result = eval('(' + viewUrl + ')');
            model.viewUrl = result.url;
        }

    } else {
		var accessInfo = DocumentUtils.getNodeAccess(model.nodeRef, user.id);
		if (accessInfo) {
			if (accessInfo.exists) {
				if (!accessInfo.hasReadPermissions) {
					model.accessMsg = "msg.access_denied";
				}
			} else {
				if (accessInfo.removed) {
					model.accessMsg = "msg.document_removed";
				} else {
					model.accessMsg = "msg.no_such_document";
				}
			}
		} else {
			model.accessMsg = "msg.document_not_found";
		}
	}
    model.isAdmin = isAdmin;
}

main();