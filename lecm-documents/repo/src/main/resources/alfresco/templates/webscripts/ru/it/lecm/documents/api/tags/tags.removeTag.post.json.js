function main() {
    if (json.has("tag") == false || json.get("tag").length == 0) {
        status.setCode(status.STATUS_BAD_REQUEST, "Name missing when removing tag");
        return;
    }
    if (json.has("nodeRef") == false || json.get("nodeRef").length == 0
        || json.has("store") == false || json.get("store").length == 0) {
        status.setCode(status.STATUS_BAD_REQUEST, "NodeRef or store missing when removing tag");
        return;
    }

    var tagName = json.get("tag");
    var store = json.get("store");
    var nodeRef = json.get("nodeRef");

    // Check for tag existing
    var tag = taggingService.getTag(store, tagName),
        tagExists = (tag != null);

    if (!tagExists) {
        status.setCode(status.STATUS_INTERNAL_SERVER_ERROR, "Tag for deleting doesn't exist");
        return;
    }

    // Get the node
    var node = search.findNode(nodeRef);

    // 404 if the node is not found
    if (node == null) {
        status.setCode(status.STATUS_NOT_FOUND, "The node could not be found");
        return;
    }

    // Remove tag from the node
    node.removeTag(tagName);

    // save the node
    node.save();

    // Get the tags of the node
    model.tags = node.tags;
}

main();