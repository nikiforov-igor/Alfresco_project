<import resource="classpath:/alfresco/templates/org/alfresco/import/alfresco-util.js">

function main() {
    AlfrescoUtil.param("nodeRef");
    model.data = getWorkflows(model.nodeRef);
}

function getWorkflows(nodeRef) {
    var url = "/lecm/statemachine/api/workflows?nodeRef=" + nodeRef + "&state=all";
    var result = remote.connect("alfresco").get(url);
    if (result.status != 200) {
        AlfrescoUtil.error(result.status, 'Could not get workflows for node ' + nodeRef);
    }
    return eval('(' + result + ')');
}

main();