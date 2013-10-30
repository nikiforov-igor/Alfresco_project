<import resource="classpath:/alfresco/templates/org/alfresco/import/alfresco-util.js">
<import resource="classpath:/alfresco/site-webscripts/ru/it/lecm/documents/utils/permission-utils.js">

function main() {
    AlfrescoUtil.param("nodeRef");
    model.containerHtmlId = args["containerHtmlId"];

    var hasPerm = hasPermission(model.nodeRef, PERM_WF_TASK_LIST);
    if (hasPerm) {
        var data = getTasks(model.nodeRef);
        if (data != null) {
            model.data = data;
        }
    }
}

function getTasks(nodeRef) {
    var url = "/lecm/statemachine/api/tasks?nodeRef=" + args["nodeRef"] + "&state=active&myTasksLimit=5";
    var result = remote.connect("alfresco").get(url);
    if (result.status != 200) {
        return null;
    }
    return eval('(' + result + ')');
}

main();
