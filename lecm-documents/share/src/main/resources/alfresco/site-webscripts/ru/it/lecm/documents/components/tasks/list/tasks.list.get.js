<import resource="classpath:/alfresco/templates/org/alfresco/import/alfresco-util.js">

function main() {
    AlfrescoUtil.param("nodeRef");
    AlfrescoUtil.param("tasksType");
    model.data = getTasks(model.nodeRef, model.tasksType);
}

function getTasks(nodeRef, tasksType) {
    var url = "/lecm/statemachine/api/tasks?nodeRef=" + nodeRef + "&type=" + tasksType + "&addSubordinatesTask=true";
    var result = remote.connect("alfresco").get(url);
    if (result.status != 200) {
        AlfrescoUtil.error(result.status, 'Could not get tasks for node ' + nodeRef);
    }
    return eval('(' + result + ')');
}

main();