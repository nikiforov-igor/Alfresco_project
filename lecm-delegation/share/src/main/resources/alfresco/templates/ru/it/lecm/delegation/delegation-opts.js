//получение delegator-а из параметров url-а
var delegator = page.url.args["delegator"];
var url;
if (delegator && delegator.length > 0) {
	model.delegator = delegator;
	url = "/lecm/delegation/get/description/for/opts?nodeRef=" + delegator;
} else {
	model.delegator = "me";
	url = "/lecm/delegation/get/description/for/opts";
}

var result = remote.connect ("alfresco").get (url);
if (200 == result.status) {
	var nativeObject = eval("(" + result + ")");
	model.nativeObject = nativeObject;
	model.response = result;
}
