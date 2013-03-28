var settingsStr = remote.connect("alfresco").get("/lecm/orgstructure/root/org-employees");

var settings = eval("(" + settingsStr + ")");

model.settings = settings;
model.response = settingsStr;
