function main() {
	var nodeRef = args["nodeRef"];

	model.isSignable = checkSignable(nodeRef);
	model.nodeRef = nodeRef;

}

function checkSignable(nodeRef) {
	var result = false;
	var url = "/lecm/signed-docflow/isSignable?nodeRef=" + nodeRef;
	var response = remote.connect("alfresco").get(url);
	if (response.status == 200) {
		var responseNative = eval('(' + response + ')');
		result = responseNative.isSignable;
	}
	return result;
}

main();
