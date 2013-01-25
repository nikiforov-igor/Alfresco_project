//получение delegator-а из параметров url-а
var delegator = page.url.args["delegator"];
var body;
if (delegator && delegator.length > 0) {
	model.delegator = delegator;
} else {
	model.delegator = "current";
}
body = '{"subject": "delegator", "nodeRef": "' + model.delegator + '"}';

var result = remote.connect ("alfresco").post ("/lecm/delegation/get/description/for/opts", body, "application/json");
if (200 == result.status) {
	var response = result.response;
	var nativeObject = eval("(" + response + ")");
	model.props = [];
	for (var prop in nativeObject) {
		model.props.push (prop + "|" + nativeObject[prop] + "|" + typeof (nativeObject[prop]));
	}
	model.nativeObject = nativeObject;
	model.response = response;
}
