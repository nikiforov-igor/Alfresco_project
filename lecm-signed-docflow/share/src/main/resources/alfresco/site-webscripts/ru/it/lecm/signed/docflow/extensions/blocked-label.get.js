<import resource="classpath:/alfresco/templates/org/alfresco/import/alfresco-util.js">

function getBlocked(nodeRef) {
	var result = false,
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
		result = responseNative.enabled;
	}
	return result;
}
AlfrescoUtil.param('nodeRef');
var isBlocked = false;
isBlocked = getBlocked(model.nodeRef);
model.isBlocked = isBlocked;