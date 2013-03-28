var settingsStr = remote.connect("alfresco").get("/lecm/orgstructure/root/org-staff-list");

var settings = eval("(" + settingsStr + ")");

model.settings = settings;
model.response = settingsStr;
