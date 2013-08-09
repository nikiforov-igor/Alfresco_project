function main() {
	var nodeRef = args["nodeRef"],
		isDocflowable = checkDocflowable(nodeRef);

	model.isDocflowable = isDocflowable;
	if (isDocflowable) {
		model.nodeRef = nodeRef;
	}
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

main();
