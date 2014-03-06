<import resource="classpath:/alfresco/templates/org/alfresco/import/alfresco-util.js">
<import resource="classpath:/alfresco/site-webscripts/ru/it/lecm/documents/utils/permission-utils.js">

function main() {
    AlfrescoUtil.param('nodeRef');
    var hasPerm = hasPermission(model.nodeRef, PERM_ACTION_EXEC);
    model.hasPermission = true; // hasPerm || hasOnlyInDraftPermission(model.nodeRef, "LECM_BASIC_PG_Initiator");
}

main();