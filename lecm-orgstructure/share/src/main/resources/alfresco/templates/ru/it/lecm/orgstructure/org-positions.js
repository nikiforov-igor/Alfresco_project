var settingsStr = remote.connect("alfresco").get("/lecm/orgstructure/root/org-positions");

var settings = eval("(" + settingsStr + ")");

model.settings = settings;
model.response = settingsStr;
