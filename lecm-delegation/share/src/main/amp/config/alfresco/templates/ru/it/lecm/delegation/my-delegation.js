var result = remote.connect ("alfresco").get ("/lecm/delegation/get/description/for/opts");
if (200 == result.status) {
	var nativeObject = eval("(" + result + ")");
	model.nativeObject = nativeObject;
	model.response = result;
}
