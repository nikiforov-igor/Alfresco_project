<import resource="classpath:/alfresco/templates/org/alfresco/import/alfresco-util.js">

function main() {
    AlfrescoUtil.param("bubblingLabel");
    var hasPermission = isStarter();
    model.showCreateBtn = (args["showCreateBtn"] == 'true') && hasPermission;
}

function isStarter() {
    var url = '/lecm/contracts/employeeIsStarter';
    var result = remote.connect("alfresco").get(url);
    if (result.status != 200) {
        return false;
    }
    var perm = eval('(' + result + ')');
    return (("" + perm) == "true");
}

main();