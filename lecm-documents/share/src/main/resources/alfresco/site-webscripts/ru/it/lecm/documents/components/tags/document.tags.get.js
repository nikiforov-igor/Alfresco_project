<import resource="classpath:/alfresco/site-webscripts/ru/it/lecm/documents/utils/document-utils.js">
<import resource="classpath:/alfresco/templates/org/alfresco/import/alfresco-util.js">
<import resource="classpath:/alfresco/site-webscripts/ru/it/lecm/documents/utils/permission-utils.js">

var DocumentTags = {
    PROP_TAGGABLE: "cm:taggable",

    getTags: function DocumentTags_getTags(record) {
        prop_taggable = record.node.properties[DocumentTags.PROP_TAGGABLE] || [];

        return prop_taggable;
    }
};

function main() {
    AlfrescoUtil.param('nodeRef');
    AlfrescoUtil.param('site', null);
    var documentDetails = DocumentUtils.getNodeDetails(model.nodeRef, model.site);
    var hasPerm = hasPermission(model.nodeRef, "_lecmPerm_TagView");
    if (documentDetails && hasPerm) {
        model.tags = jsonUtils.toJSONString(DocumentTags.getTags(documentDetails.item));
        model.record = jsonUtils.toJSONString(documentDetails.item);
        model.mayEdit = jsonUtils.toJSONString(hasPermission(model.nodeRef, "_lecmPerm_TagCreate")
                    && hasPermission(model.nodeRef, "_lecmPerm_TagDelete"));
    }
};

main();