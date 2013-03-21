<import resource="classpath:/alfresco/templates/org/alfresco/import/alfresco-util.js">

function main() {
    AlfrescoUtil.param('nodeRef');
    model.nodeRef = args["nodeRef"];
    var actions = getActions(model.nodeRef);
    if (actions) {
        model.result = actions;
    }
}

function getActions(nodeRef) {
    var url = '/lecm/statemachine/actions?documentNodeRef=' + nodeRef;
    var result = remote.connect("alfresco").get(url);
    if (result.status != 200) {
        return null;
    }
    if (result == "") {
        return {};
    } else {
        return eval('(' + result + ')');
    }
}

main();