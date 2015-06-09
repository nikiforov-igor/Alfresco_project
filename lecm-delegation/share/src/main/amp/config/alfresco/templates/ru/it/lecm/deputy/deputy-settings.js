//получение delegator-а из параметров url-а
var url = "/lecm/deputy/getSettingsNode";

var result = remote.connect ("alfresco").get (url);
if (200 == result.status) {
	model.response = result;
}
