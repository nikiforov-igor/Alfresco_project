<import resource="classpath:/alfresco/templates/org/alfresco/import/alfresco-util.js">

function main() {
    AlfrescoUtil.param("nodeRef");
    model.data = getTasks(model.nodeRef);
}

function getTasks(nodeRef) {
    var url = "/lecm/statemachine/api/tasks?nodeRef=" + args["nodeRef"] + "&type=active&myTasksLimit=5";
    var result = remote.connect("alfresco").get(url);
    if (result.status != 200) {
        AlfrescoUtil.error(result.status, 'Could not get connections for node ' + nodeRef);
    }
    return eval('(' + result + ')');
}

main();
