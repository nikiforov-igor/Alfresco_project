function main() {
	var isSignable = false;
	var nodeRef = args["nodeRef"];
	var documentNodeRef = getDocument(nodeRef);
	if (documentNodeRef == null) {
		return;
	}
	var isDocflowable = checkDocflowable(documentNodeRef);
	model.isDocflowable = isDocflowable;
	if (isDocflowable) {
		isSignable = checkSignable(nodeRef);
	}
	model.isSignable = isSignable;
	model.nodeRef = nodeRef;

}

function getDocument(nodeRef) {
	var result = null;
	var url = "/lecm/document/attachments/api/getDocumentByAttachment?nodeRef=" + nodeRef;
	var response = remote.connect("alfresco").get(url);
	if (response.status == 200) {
		var responseNative = eval('(' + response + ')');
		result = responseNative.nodeRef;
	}
	return result;
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
