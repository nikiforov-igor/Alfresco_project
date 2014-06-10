function main()
{
    model.bubblingLabel = args["bubblingLabel"];
    model.newRowButton = args["newRowButton"];
    model.searchButtons = args["searchButtons"];
    model.newRowLabel = args["newRowLabel"];
    model.showStructureLabel = args["showStructureLabel"];

    var showSearchBlock = args["showSearchBlock"];
    var showExSearchBtn = args["showExSearchBtn"];
    var showButtons = args["showButtons"];
    var showStructure = args["showStructure"];

    if (showSearchBlock) {
        model.showSearchBlock = (showSearchBlock == 'true');
    }
    if (showExSearchBtn){
        model.showExSearchBtn = (showExSearchBtn == 'true');
    }
    if (showStructure){
        model.showStructure = (showStructure == 'true');
    }
    if (showButtons){
        model.showButtons = (showButtons == 'true');
    }

    var rolesStr = remote.connect("alfresco").get("/lecm/orgstructure/api/getCurrentEmployeeRoles");
    var rolesList = [];
    if (rolesStr.status == 200) {
        rolesList = eval("(" + rolesStr + ")");
    }
    var hasRole = false;
    for (var i = 0; i < rolesList.length; i++) {
        if (rolesList[i].id == "BR_ORGSTRUCTURE_ENGINEER") {
            hasRole = true;
            break;
        }
    }
    model.isOrgEngineer = hasRole;
}

main();