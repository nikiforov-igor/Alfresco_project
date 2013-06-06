<import resource="classpath:/alfresco/templates/org/alfresco/import/alfresco-util.js">

function main() {
    var hasPermission = isStarter(args["itemType"]);
    model.showCreateBtn = (args["showCreateBtn"] == 'true') && hasPermission;
    model.showSearch = args["showSearch"] && (args["showSearch"] == 'true');
    model.showExSearchBtn = args["showExSearchBtn"] && (args["showExSearchBtn"] == 'true');
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