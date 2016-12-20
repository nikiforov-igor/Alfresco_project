var document = utils.getNodeFromString(args['nodeRef']);
var canCopy = false;
if (document) {
    canCopy = documentScript.canCopyDocument(document);
}
model.canCopy = canCopy;

