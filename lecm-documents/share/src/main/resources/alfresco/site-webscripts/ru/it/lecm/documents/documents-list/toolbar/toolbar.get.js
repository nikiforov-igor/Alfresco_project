<import resource="classpath:/alfresco/templates/org/alfresco/import/alfresco-util.js">

function main() {
    AlfrescoUtil.param("bubblingLabel");
    AlfrescoUtil.param("itemType");

    var hasPermission = isStarter(model.itemType);

    model.showCreateBtn = (args["showCreateBtn"] == 'true') && hasPermission;
    model.showSearch = args["showSearch"] && (args["showSearch"] == 'true');
    model.showExSearchBtn = args["showExSearchBtn"] && (args["showExSearchBtn"] == 'true');

    model.newRowLabel = args["newRowLabel"];
    model.newRowDialogTitle = args["newRowDialogTitle"];
}

function isStarter(docType) {
    var url = '/lecm/documents/employeeIsStarter?docType=' + docType;
    var result = remote.connect("alfresco").get(url);
    if (result.status != 200) {
        return false;
    }
    var perm = eval('(' + result + ')');
    return (("" + perm) == "true");
}

main();