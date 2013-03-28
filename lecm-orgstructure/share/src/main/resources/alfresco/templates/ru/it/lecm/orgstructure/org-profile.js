var settingsStr = remote.connect("alfresco").get("/lecm/orgstructure/root/org-profile");

var settings = eval("(" + settingsStr + ")");

model.settings = settings;
model.response = settingsStr;
