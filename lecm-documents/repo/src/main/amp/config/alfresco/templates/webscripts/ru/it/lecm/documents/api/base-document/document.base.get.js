var nodeRef = args['nodeRef'];
var document = utils.getNodeFromString(nodeRef);
var baseDocAssocName = args['baseDocAssocName'];
var baseDocAssoc = document.assocs[baseDocAssocName];
if (baseDocAssoc && baseDocAssoc.length > 0) {
    var baseDoc = baseDocAssoc[0];
    model.baseDocNodeRef = baseDoc.getNodeRef().toString();
} else {
    model.baseDocNodeRef = "";
}