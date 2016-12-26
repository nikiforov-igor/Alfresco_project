var parentNodeRef = args['nodeRef'];
var parentNode = utils.getNodeFromString(parentNodeRef);
if (parentNode) {
    baseDoc = errands.getBaseDocumentByAdditionalDocument(parentNode);
    model.baseDoc = baseDoc;
    model.isFinal = statemachine.isFinal(baseDoc.nodeRef);
}
