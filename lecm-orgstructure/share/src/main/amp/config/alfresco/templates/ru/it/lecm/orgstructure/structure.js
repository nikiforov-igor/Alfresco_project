var settingsStr = remote.connect("alfresco").get("/lecm/orgstructure/root/org-structure");

var settings = {};
if (settingsStr.status == 200) {
    settings = eval("(" + settingsStr + ")");
}

model.settings = settings;
model.response = settingsStr;
