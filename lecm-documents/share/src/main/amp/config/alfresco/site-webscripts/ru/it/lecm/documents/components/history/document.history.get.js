<import resource="classpath:/alfresco/templates/org/alfresco/import/alfresco-util.js">
<import resource="classpath:/alfresco/site-webscripts/ru/it/lecm/documents/utils/permission-utils.js">

function main()
{
    AlfrescoUtil.param('nodeRef', null);
    if (model.nodeRef) {
        model.hasViewHistoryPerm = hasPermission(model.nodeRef,PERM_HISTORY_VIEW);
    }
}

main();
