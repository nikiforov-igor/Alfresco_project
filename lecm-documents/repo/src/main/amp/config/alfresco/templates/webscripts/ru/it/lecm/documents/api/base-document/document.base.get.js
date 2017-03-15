var nodeRef = args['nodeRef'];
var document = utils.getNodeFromString(nodeRef);
var baseDocAssocName = args['baseDocAssocName'];
var baseDocAssoc = document.assocs[baseDocAssocName];
if (baseDocAssoc) {
    if (baseDocAssoc && baseDocAssoc.length > 0) {
        var baseDoc = baseDocAssoc[0];
        model.baseDocNodeRef = baseDoc.getNodeRef().toString();
    } else {
        model.baseDocNodeRef = "";
    }
} else {
    logger.error("ERROR: Base document association with name \"" + baseDocAssocName + "\" not found on " + nodeRef);
}