<import resource="classpath:/alfresco/templates/org/alfresco/import/alfresco-util.js">
<import resource="classpath:/alfresco/site-webscripts/ru/it/lecm/documents/utils/permission-utils.js">

function main() {
    AlfrescoUtil.param("nodeRef");
    AlfrescoUtil.param("tasksState");
    AlfrescoUtil.param("isAnchor");

    var hasPerm = hasPermission(model.nodeRef, PERM_WF_TASK_LIST);
    if (hasPerm) {
        var data = getTasks(model.nodeRef, model.tasksState);
        if (data != null) {
            model.data = data;
        }
    }
}

function getTasks(nodeRef, tasksState) {
    var url = "/lecm/statemachine/api/tasks?nodeRef=" + nodeRef + "&state=" + tasksState + "&addSubordinatesTask=true";
    var result = remote.connect("alfresco").get(url);
    if (result.status != 200) {
        return null;
    }
    return eval('(' + result + ')');
}

main();