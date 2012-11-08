model.logMsg = "hello from delegation module!";
var json = remote.connect ("alfresco").post ("/logicecm/delegation/getrootnode", "", "json");
model.json = json;
