<import resource="classpath:/alfresco/site-webscripts/ru/it/lecm/documents/utils/document-utils.js">
<import resource="classpath:/alfresco/templates/org/alfresco/import/alfresco-util.js">
<import resource="classpath:/alfresco/site-webscripts/ru/it/lecm/documents/utils/permission-utils.js">

function main() {
    AlfrescoUtil.param("nodeRef");
    var nodeDetails = DocumentUtils.getNodeDetails(model.nodeRef);
    var isAdmin = false;
	var isMlSupported;
	var mlValue;
	var presentString;
    model.viewUrl = "document";
    if (nodeDetails) {
		isMlSupported = nodeDetails.isMlSupported;
        model.item = nodeDetails.item;

		mlValue = nodeDetails.item.node.properties["lecm-document:ml-present-string"];
		presentString = isMlSupported && mlValue ? mlValue : nodeDetails.item.node.properties["lecm-document:present-string"];
		model.documentName = presentString ? presentString : nodeDetails.item.displayName;

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
        model.isDocumentStarter = isStarter(nodeDetails.item.node.type);

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

        url = '/lecm/document/api/copy/can?nodeRef=' + encodeURI(model.nodeRef);
        var res = remote.connect("alfresco").get(url);
        if (res.status == 200) {
            var result = eval('(' + res + ')');
            model.canCopy = result.canCopy;
        } else {
            model.canCopy = false;
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
