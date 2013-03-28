var settingsStr = remote.connect("alfresco").get("/lecm/orgstructure/root/org-roles");

var settings = eval("(" + settingsStr + ")");

model.settings = settings;
model.response = settingsStr;
