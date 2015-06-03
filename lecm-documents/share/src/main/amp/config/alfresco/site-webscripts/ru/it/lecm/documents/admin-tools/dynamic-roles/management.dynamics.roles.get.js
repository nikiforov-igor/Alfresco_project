<import resource="classpath:/alfresco/templates/org/alfresco/import/alfresco-util.js">

function main (){
	AlfrescoUtil.param("nodeRef");
	model.dynamicRoles = getDynamicRoles(model.nodeRef);
}

function getDynamicRoles(nodeRef, defaultValue) {
	var url = '/lecm/statemachine/getDynamicRoles?nodeRef=' + nodeRef;
	var result = remote.connect("alfresco").get(url);
	if (result.status != 200) {
		if (defaultValue !== undefined) {
			return defaultValue;
		}
		return null;
	}
	return result;
}

main ();