//скрипт для получения корневой (корневых) папок
var result = remote.connect ("alfresco").post ("/lecm/delegation/get/description/for/list", "{}", "application/json");
if (200 == result.status) {
	var response = result.response;
	model.delegationDescription = response;
	var nativeObject = eval("(" + response + ")");
	model.props = [];
	for (var prop in nativeObject) {
		model.props.push (prop + "|" + nativeObject[prop] + "|" + typeof (nativeObject[prop]));
	}
	model.isEngineer = nativeObject.isEngineer;
	model.isBoss = nativeObject.isBoss;
	model.nativeObject = nativeObject;
}
