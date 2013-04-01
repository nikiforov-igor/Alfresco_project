<import resource="classpath:/alfresco/templates/org/alfresco/import/alfresco-util.js">
<import resource="classpath:/alfresco/site-webscripts/ru/it/lecm/documents/utils/permission-utils.js">

function main() {
    AlfrescoUtil.param("nodeRef");
    AlfrescoUtil.param("tasksState");

    var hasPerm = hasPermission(model.nodeRef, '_lecmPerm_WFTaskList');
    if (hasPerm) {
        model.data = getTasks(model.nodeRef, model.tasksState);
    }
}

function getTasks(nodeRef, tasksState) {
    var url = "/lecm/statemachine/api/tasks?nodeRef=" + nodeRef + "&state=" + tasksState + "&addSubordinatesTask=true";
    var result = remote.connect("alfresco").get(url);
    if (result.status != 200) {
        AlfrescoUtil.error(result.status, 'Could not get tasks for node ' + nodeRef);
    }
    return eval('(' + result + ')');
}

main();