<import resource="classpath:/alfresco/templates/org/alfresco/import/alfresco-util.js">
<import resource="classpath:/alfresco/site-webscripts/ru/it/lecm/documents/utils/permission-utils.js">

function main() {
    AlfrescoUtil.param("nodeRef");
    model.containerHtmlId = args["containerHtmlId"];

    var hasPerm = hasPermission(model.nodeRef, PERM_WF_LIST);
    if (hasPerm) {
        var data = getWorkflows(model.nodeRef);
        if (data != null) {
            model.data = data;
        }
    }
}

function getWorkflows(nodeRef) {
    var url = "/lecm/statemachine/api/workflows?nodeRef=" + args["nodeRef"] + "&state=active&activeWorkflowsLimit=5";
    var result = remote.connect("alfresco").get(url);
    if (result.status != 200) {
        return null;
    }
    return eval('(' + result + ')');
}

main();
