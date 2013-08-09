function main() {
	var isSignable = false,
		isDocflowable,
		nodeRef = args["nodeRef"],
		documentNodeRef = getDocument(nodeRef);

	if (documentNodeRef == null) {
		return;
	}

	isDocflowable = checkDocflowable(documentNodeRef);
	model.isDocflowable = isDocflowable;
	if (isDocflowable) {
		isSignable = checkSignable(nodeRef);
	}
	model.isSignable = isSignable;
	model.nodeRef = nodeRef;
}

function getDocument(nodeRef) {
	var result = null,
		responseNative,
		url = "/lecm/document/attachments/api/getDocumentByAttachment?nodeRef=" + nodeRef,
		response = remote.connect("alfresco").get(url);

	if (response.status == 200) {
		responseNative = eval('(' + response + ')');
		result = responseNative.nodeRef;
	}
	return result;
}

function checkDocflowable(nodeRef) {
	var response,
		responseNative,
		result = false,
		url = "/lecm/signed-docflow/config/aspect",
		dataObj = {
			action: "get",
			node: nodeRef,
			aspect: "{http://www.it.ru/lecm/model/signed-docflow/1.0}docflowable"
		};

	response = remote.connect("alfresco").post(url, jsonUtils.toJSONString(dataObj), "application/json");
	if (response.status == 200) {
		responseNative = eval('(' + response + ')');
		result = responseNative.enabled;
	}
	return result;
}

function checkSignable(nodeRef) {
	var result = false,
		response,
		responseNative,
		url = "/lecm/signed-docflow/config/aspect",
		dataObj = {
			action: "get",
			node: nodeRef,
			aspect: "{http://www.it.ru/lecm/model/signed-docflow/1.0}signable"
		}

	response = remote.connect("alfresco").post(url, jsonUtils.toJSONString(dataObj), "application/json");
	if (response.status == 200) {
		responseNative = eval('(' + response + ')');
		result = responseNative.enabled;
	}
	return result;
}

main();
