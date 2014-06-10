<import resource="classpath:/alfresco/site-webscripts/ru/it/lecm/documents/utils/document-utils.js">
<import resource="classpath:/alfresco/templates/org/alfresco/import/alfresco-util.js">
<import resource="classpath:/alfresco/site-webscripts/ru/it/lecm/documents/utils/permission-utils.js">

function main()
{
    AlfrescoUtil.param('nodeRef');
    AlfrescoUtil.param('formId', "document");
    var documentDetails = DocumentUtils.getNodeDetails(model.nodeRef, model.site);
    var hasViewPermission = hasPermission(model.nodeRef, PERM_ATTR_LIST);
    if (documentDetails && hasViewPermission)
    {
        model.document = documentDetails;
    }
}

main();