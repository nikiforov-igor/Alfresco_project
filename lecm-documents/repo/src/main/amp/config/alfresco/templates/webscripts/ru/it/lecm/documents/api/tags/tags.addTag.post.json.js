<import resource="classpath:/alfresco/templates/org/alfresco/import/alfresco-util.js">
<import resource="classpath:/alfresco/site-webscripts/ru/it/lecm/documents/utils/permission-utils.js">

function main() {
    if (json.has("tag") == false || json.get("tag").length == 0) {
        status.setCode(status.STATUS_BAD_REQUEST, "Name missing when adding tag");
        return;
    }
    if (json.has("nodeRef") == false || json.get("nodeRef").length == 0
        || json.has("store") == false || json.get("store").length == 0) {
        status.setCode(status.STATUS_BAD_REQUEST, "NodeRef or store missing when adding tag");
        return;
    }

    var tagName = json.get("tag");
    var store = json.get("store");
    var nodeRef = json.get("nodeRef");

    // Check permissions
    var mayAdd = hasPermission(nodeRef, PERM_TAG_CREATE);
    if (!mayAdd) {
        return;
    }

    // Check for tag existing, if not then create
    var tag = taggingService.getTag(store, tagName),
        tagExists = (tag != null);

    if (!tagExists) {
        tag = taggingService.createTag(store, tagName);
        if (tag == null) {
            status.setCode(status.STATUS_INTERNAL_SERVER_ERROR, "error.cannotCreateTag");
            return;
        }
    }

    // Get the node
    var node = search.findNode(nodeRef);

    // 404 if the node is not found
    if (node == null) {
        status.setCode(status.STATUS_NOT_FOUND, "The node could not be found");
        return;
    }

    // Add the tag
    node.addTag(tagName);

    // save the node
    node.save();

    // Get the tags of the node
    model.tags = node.tags;
}

main();