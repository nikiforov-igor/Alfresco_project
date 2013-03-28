var settingsStr = remote.connect("alfresco").get("/lecm/orgstructure/root/org-work-groups");

var settings = eval("(" + settingsStr + ")");

model.settings = settings;
model.response = settingsStr;
