<import resource="classpath:/alfresco/site-webscripts/ru/it/lecm/documents/utils/document-utils.js">

function main()
{
    AlfrescoUtil.param('nodeRef');
    AlfrescoUtil.param('site', null);
    AlfrescoUtil.param('formId', "document");
    var documentDetails = AlfrescoUtil.getNodeDetails(model.nodeRef, model.site);
    if (documentDetails)
    {
        model.document = documentDetails;
        model.allowMetaDataUpdate = documentDetails.item.node.permissions.user["Write"] || false;
    }
}

main();