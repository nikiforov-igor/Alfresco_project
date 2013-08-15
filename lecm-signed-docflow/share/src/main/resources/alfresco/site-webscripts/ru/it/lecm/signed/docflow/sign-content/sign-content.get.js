function main() {
	var nodeRef = args["nodeRef"];

	model.isSignable = checkSignable(nodeRef);
	model.nodeRef = nodeRef;

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
