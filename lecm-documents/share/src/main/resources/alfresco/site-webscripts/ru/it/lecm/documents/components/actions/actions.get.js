<import resource="classpath:/alfresco/templates/org/alfresco/import/alfresco-util.js">
<import resource="classpath:/alfresco/site-webscripts/ru/it/lecm/documents/utils/permission-utils.js">

function main() {
    AlfrescoUtil.param('nodeRef');
    var hasPerm = hasPermission(model.nodeRef, PERM_ACTION_EXEC);
    if (hasPerm || hasOnlyInDraftPermission(model.nodeRef, "LECM_BASIC_PG_Initiator")) {
        var actions = getActions(model.nodeRef);
        if (actions) {
            model.result = actions;
        }
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