var settingsStr = remote.connect("alfresco").get("/lecm/orgstructure/root/org-business-roles");

var settings = eval("(" + settingsStr + ")");

model.settings = settings;
model.response = settingsStr;
