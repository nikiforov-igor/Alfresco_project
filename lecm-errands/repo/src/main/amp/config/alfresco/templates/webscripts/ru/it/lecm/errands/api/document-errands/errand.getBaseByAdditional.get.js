var parentNodeRef = args['nodeRef'];
var parentNode = utils.getNodeFromString(parentNodeRef);
var baseDoc, baseDocAssoc;
if (parentNode) {
    if (parentNode.type == "{http://www.it.ru/logicECM/resolutions/1.0}document") {
        baseDocAssoc = parentNode.assocs["lecm-errands:base-document-assoc"];
    } else if (parentNode.type == "{http://www.it.ru/logicECM/errands/1.0}document") {
        baseDocAssoc = parentNode.assocs["lecm-errands:base-assoc"];
    }
    if (!baseDocAssoc || !baseDocAssoc.size()) {
        baseDoc = parentNode;
    } else {
        baseDoc = baseDocAssoc[0];
    }
    model.baseDoc = baseDoc;
    model.isFinal = statemachine.isFinal(baseDoc.nodeRef);
}
