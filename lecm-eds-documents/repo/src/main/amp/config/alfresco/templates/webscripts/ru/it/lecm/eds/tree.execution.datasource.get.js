/*VIEW_ALL, VIEW_DIRECT, VIEW_NO*/
var viewLinksMode = '' + edsGlobalSettings.getLinksViewMode();
var isMlSupported = lecmMessages.isMlSupported();

var documentNodeRef = args['documentNodeRef'];

/*filtering*/
var isFirstLayer = 'true' == args['isFirstLayer'];
var substituteTitle = isMlSupported ? "{lecm-document:ml-ext-present-string}" : "{lecm-document:ext-present-string}";

var items = [];
var item = search.findNode(documentNodeRef);
if (isFirstLayer) {
    var firstItem = evaluateItem(item, substituteTitle);
    if (firstItem.numberOfChildElements > 0) {
        items.push(firstItem);
    }
} else {
    var children = [];
    var childrenErrands = errands.getChildErrands(item.nodeRef.toString());
    var childrenResolutions = errands.getChildResolutions(item.nodeRef.toString());
    if (childrenErrands) {
        children = children.concat(childrenErrands);
    }
    if (childrenResolutions) {
        children = children.concat(childrenResolutions);
    }
    children.forEach(function (child) {
        if (lecmPermission.hasReadAccess(child) || 'VIEW_NO' !== viewLinksMode) {
            items.push(evaluateItem(child, substituteTitle));
        }
    });
}

model.items = items;
model.documentRef = documentNodeRef;

function evaluateItem(document, substituteTitle) {
    var itemObj = {};

    if (document != null) {
        itemObj.title = substitude.formatNodeTitle(document, substituteTitle);
        itemObj.nodeRef = document.nodeRef.toString();
        itemObj.docType = document.typeShort;
        itemObj.status = document.properties["lecm-statemachine:status"];
        itemObj.hasAccess = lecmPermission.hasReadAccess(document);

        var childrenErrands = errands.getChildErrands(document.nodeRef.toString());
        var childrenResolutions = errands.getChildResolutions(document.nodeRef.toString());

        var numberOfChildErrands = childrenErrands === null ? 0 : childrenErrands.length;
        var numberOfChildResolutions = childrenResolutions === null ? 0 : childrenResolutions.length;

        itemObj.numberOfChildErrands = numberOfChildErrands + numberOfChildResolutions;
        itemObj.numberOfChildElements = numberOfChildErrands + numberOfChildResolutions;

        return itemObj;
    }
    return null;
}

