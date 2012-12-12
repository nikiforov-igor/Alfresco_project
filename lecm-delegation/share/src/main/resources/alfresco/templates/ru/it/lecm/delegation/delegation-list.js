//скрипт для получения корневой (корневых) папок
var jsonStr = remote.connect ("alfresco").get ("/lecm/delegation/get/container");
//var obj = jsonUtils.toObject (jsonStr);
model.delegationContainer = jsonStr;
