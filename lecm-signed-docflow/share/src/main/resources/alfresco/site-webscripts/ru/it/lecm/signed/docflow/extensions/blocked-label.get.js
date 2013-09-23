<import resource="classpath:/alfresco/templates/org/alfresco/import/alfresco-util.js">

function getBlocked(nodeRef) {
	var result = false,
		hasLockOwner = false,
		hasLockType = false,
		response,
		responseNative,
		url = "/lecm/signed-docflow/config/aspect",
		dataObj = {
			action: "get",
			node: nodeRef,
			aspect: "{http://www.alfresco.org/model/content/1.0}lockable"
		}

	response = remote.connect("alfresco").post(url, jsonUtils.toJSONString(dataObj), "application/json");
	if (response.status == 200) {
		responseNative = eval('(' + response + ')');
		if(responseNative.properties["{http://www.alfresco.org/model/content/1.0}lockOwner"] != null){
			hasLockOwner = true;
		}
		if(responseNative.properties["{http://www.alfresco.org/model/content/1.0}lockType"] != null){
			hasLockType = true;
		}
		return responseNative.enabled && hasLockOwner && hasLockType;
	}
	
	
	
	return result;
}
AlfrescoUtil.param('nodeRef');
var isBlocked = false;
isBlocked = getBlocked(model.nodeRef);
model.isBlocked = isBlocked;