function main() {
	var nodeRef = args["nodeRef"];
	var isDocflowable = checkDocflowable(nodeRef);
	model.isDocflowable = isDocflowable;
	if (isDocflowable) {
		model.nodeRef = nodeRef;
	}
}

function checkDocflowable(nodeRef) {
	var result = false;
	var url = "/lecm/signed-docflow/isDocflowable?nodeRef=" + nodeRef;
	var response = remote.connect("alfresco").get(url);
	if (response.status == 200) {
		var responseNative = eval('(' + response + ')');
		result = responseNative.isDocflowable;
	}
	return result;
}

main();
