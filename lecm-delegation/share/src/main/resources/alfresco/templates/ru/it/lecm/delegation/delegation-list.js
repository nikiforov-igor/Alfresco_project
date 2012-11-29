//скрипт для получения корневой (корневых) папок
model.delegationContainer = remote.connect ("alfresco").post ("/lecm/delegation/getDelegationContainer", "", "json");