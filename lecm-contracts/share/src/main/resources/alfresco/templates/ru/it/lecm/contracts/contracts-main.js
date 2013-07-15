model.hasPermission = true;
var contractsSettings = remote.connect("alfresco").get("/lecm/contracts/settings");
if (contractsSettings.status == 200) {
    model.settings = contractsSettings;
}
