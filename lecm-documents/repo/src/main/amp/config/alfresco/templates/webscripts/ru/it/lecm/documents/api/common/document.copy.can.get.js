var document = search.findNode(args['nodeRef']);
var canCopy = false;
if(document) {
    canCopy = documentScript.canCopyDocument(document);
}
model.canCopy = canCopy == true;

