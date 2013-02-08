//скрипт для получения корневой (корневых) папок
var result = remote.connect ("alfresco").get ("/lecm/delegation/get/description/for/list");

if (200 == result.status) {
	var nativeObject = eval("(" + result + ")");
	model.nativeObject = nativeObject;
	model.response = result;
}
