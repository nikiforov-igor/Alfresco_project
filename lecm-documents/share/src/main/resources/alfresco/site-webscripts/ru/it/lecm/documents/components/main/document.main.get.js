<import resource="classpath:/alfresco/site-webscripts/ru/it/lecm/documents/utils/document-utils.js">
<import resource="classpath:/alfresco/templates/org/alfresco/import/alfresco-util.js">

function main()
{
    AlfrescoUtil.param('nodeRef');
    AlfrescoUtil.param('site', null);
    AlfrescoUtil.param('formId', "document");
    var documentDetails = DocumentUtils.getNodeDetails(model.nodeRef, model.site);
    if (documentDetails)
    {
        model.document = documentDetails;
        model.allowMetaDataUpdate = documentDetails.item.node.permissions.user["Write"] || false;
    }
}

main();