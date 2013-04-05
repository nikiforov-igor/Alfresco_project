var settingsStr = remote.connect("alfresco").get("/lecm/documents-journal/root");

var settings = eval("(" + settingsStr + ")");

model.settings = settings;
model.response = settingsStr;