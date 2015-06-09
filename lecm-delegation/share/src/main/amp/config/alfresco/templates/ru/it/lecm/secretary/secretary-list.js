//скрипт для получения корневой (корневых) папок
var result = remote.connect ("alfresco").get ("/lecm/secretary/getSettingsForList");

if (200 == result.status) {
	var nativeObject = eval("(" + result + ")");
	model.nativeObject = nativeObject;
	model.response = result;
}

var deputySettings = remote.connect ("alfresco").get ("/lecm/deputy/getSubjectDictionary");
if (200 == result.status) {
	model.deputySettings = deputySettings;
}
