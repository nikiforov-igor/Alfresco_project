<import resource="classpath:/alfresco/templates/org/alfresco/import/alfresco-util.js">
<import resource="classpath:/alfresco/site-webscripts/ru/it/lecm/documents/utils/permission-utils.js">
<import resource="classpath:/alfresco/site-webscripts/ru/it/lecm/documents/utils/document-utils.js">

function main() {
    AlfrescoUtil.param("nodeRef");
    AlfrescoUtil.param("componentHtmlId", null);
    AlfrescoUtil.param("title", null);

    var nodeDetails = DocumentUtils.getNodeDetails(model.nodeRef);
    if (nodeDetails) {
        if (nodeDetails.item.node.properties.hasOwnProperty("lecm-document:subject-assoc-ref")) {
            model.subjectAssoc = nodeDetails.item.node.properties["lecm-document:subject-assoc-ref"];
        }
    }
    var uri = '/lecm/document-type/settings?docType=lecm-errands:document';
    model.errandsSettings = jsonUtils.toJSONString(remote.connect("alfresco").get(uri));
};
main();
