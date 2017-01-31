<import resource="classpath:/alfresco/templates/org/alfresco/import/alfresco-util.js">
<import resource="classpath:/alfresco/site-webscripts/ru/it/lecm/documents/utils/permission-utils.js">
<import resource="classpath:/alfresco/site-webscripts/ru/it/lecm/documents/utils/document-utils.js">

function main() {
	AlfrescoUtil.param("nodeRef");
	AlfrescoUtil.param("view", "");
	var additionalType = AlfrescoUtil.param("additionalType");
	var additionalAssoc = AlfrescoUtil.param("additionalAssoc");
	var hasPerm = hasPermission(model.nodeRef, PERM_CONTENT_LIST);
	if (hasPerm) {
		var nodeDetails = DocumentUtils.getNodeDetails(model.nodeRef);
		if (nodeDetails) {
			var aspects = nodeDetails.item.node.aspects;
			var withoutAttachments = false;
			if (aspects != null) {
				for (var i = 0; i < aspects.length; i++) {
					if (aspects[i] == "lecm-document-aspects:without-attachments") {
						withoutAttachments = true;
						break;
					}
				}
			}

			if (!withoutAttachments) {
				var atts = getAttachments(model.nodeRef, null, additionalType, additionalAssoc);
				if (atts != null) {
					model.attachments = atts;
				}
				model.hasViewAttachmentPerm = hasPermission(model.nodeRef, PERM_CONTENT_VIEW);
			}
		}
	}
}

function getAttachments(nodeRef, defaultValue, additionalType, additionalAssoc) {
	var url = '/lecm/document/attachments/api/get?documentNodeRef=' + nodeRef + "&count=5";
	if (additionalType && additionalAssoc) {
		url += '&additionalType=' + encodeURIComponent(additionalType);
		url += '&additionalAssoc=' + encodeURIComponent(additionalAssoc);
	}
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
