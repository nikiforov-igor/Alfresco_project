<import resource="classpath:/alfresco/templates/org/alfresco/import/alfresco-util.js">
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
    var documentDetails = AlfrescoUtil.getNodeDetails(model.nodeRef, model.site);
    if (documentDetails) {
        model.record = jsonUtils.toJSONString(documentDetails.item);
        model.tags = jsonUtils.toJSONString(DocumentTags.getTags(documentDetails.item));
        model.allowMetaDataUpdate = documentDetails.item.node.permissions.user["Write"] || false;
    }
};

main();