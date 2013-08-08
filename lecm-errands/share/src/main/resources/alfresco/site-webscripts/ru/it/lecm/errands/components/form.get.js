<import resource="classpath:/alfresco/site-webscripts/ru/it/lecm/documents/utils/document-utils.js">
<import resource="classpath:/alfresco/templates/org/alfresco/import/alfresco-util.js">
<import resource="classpath:alfresco/site-webscripts/org/alfresco/callutils.js">

function main() {
    AlfrescoUtil.param("formId");
    AlfrescoUtil.param("nodeRef");
    var nodeDetails = DocumentUtils.getNodeDetails(model.nodeRef);
    if (nodeDetails) {
        model.node = nodeDetails.item.node;
    }

    var uri = '';

    uri = addParamToUrl('/lecm/document/attachments/api/getAttachmentsByCategory', 'documentNodeRef', model.nodeRef);
    uri = addParamToUrl(uri, 'category', encodeURIComponent("Поручение"));
    model.attachments  = doGetCall(uri);

    uri = addParamToUrl('/lecm/document/attachments/api/getAttachmentsByCategory', 'documentNodeRef', model.nodeRef);
    uri = addParamToUrl(uri, 'category', encodeURIComponent("Исполнение"));
    model.attachmentsExec  = doGetCall(uri);

    uri = addParamToUrl('/lecm/document/attachments/api/getAttachmentsByCategory', 'documentNodeRef', model.nodeRef);
    uri = addParamToUrl(uri, 'category', encodeURIComponent("Контроль"));
    model.attachmentsControl  = doGetCall(uri);

    //todo: change members to coexecutors
    uri = addParamToUrl('/lecm/document/api/getMembers', 'nodeRef', model.nodeRef);
    uri = addParamToUrl(uri, 'skipCount', 0);
    uri = addParamToUrl(uri, 'loadCount', 10);
    model.coexecs  = doGetCall(uri);

    uri = addParamToUrl('/lecm/errands/api/getAdditionalDoc', 'nodeRef', model.nodeRef);
    model.additionalDoc  = doGetCall(uri);

    uri = addParamToUrl('/lecm/errands/api/getLinks', 'nodeRef', model.nodeRef);
    uri = addParamToUrl(uri, 'assocType', 'lecm-errands:links-assoc');
    model.links  = doGetCall(uri);

    uri = addParamToUrl('/lecm/errands/api/getLinks', 'nodeRef', model.nodeRef);
    uri = addParamToUrl(uri, 'assocType', 'lecm-errands:execution-links-assoc');
    model.executeLinks  = doGetCall(uri);

    model.limitDate = new Date(nodeDetails.item.node.properties["lecm-errands:limitation-date"]["value"]);
}

main();