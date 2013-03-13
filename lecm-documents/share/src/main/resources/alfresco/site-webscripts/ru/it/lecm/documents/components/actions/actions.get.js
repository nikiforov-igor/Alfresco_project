<import resource="classpath:/alfresco/templates/org/alfresco/import/alfresco-util.js">

function main() {
    model.name = "Основные сведения";
    AlfrescoUtil.param('nodeRef');
    model.nodeRef = args["nodeRef"];
    model.result = getActions(model.nodeRef)
}

function getActions(nodeRef) {
    var url = '/lecm/statemachine/actions?documentNodeRef=' + nodeRef;
    var result = remote.connect("alfresco").get(url);
    if (result.status != 200) {
        AlfrescoUtil.error(result.status, 'Could not get actions for node ' + nodeRef);
    }
    if (result == "") {
        return {};
    } else {
        return eval('(' + result + ')');
    }
}

main();