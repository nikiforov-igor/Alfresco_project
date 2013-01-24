//скрипт для получения корневой (корневых) папок
var jsonStr = remote.connect ("alfresco").get ("/lecm/business-journal/api/getArchiverSettings");
model.bjSettings = jsonStr;
