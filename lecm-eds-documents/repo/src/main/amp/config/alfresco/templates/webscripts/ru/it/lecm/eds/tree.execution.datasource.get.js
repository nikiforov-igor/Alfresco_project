/*VIEW_ALL, VIEW_DIRECT, VIEW_NO*/
var viewLinksMode = '' + edsGlobalSettings.getLinksViewMode();
var isMlSupported = lecmMessages.isMlSupported();

var documentNodeRef = args['documentNodeRef'];
var substituteTitle = isMlSupported ? "{lecm-document:ml-ext-present-string}" : "{lecm-document:ext-present-string}";

var items = [];
var item = search.findNode(documentNodeRef);

var children = getChildren(item);
children.sort(function(a, b) {
    return (a.properties["cm:created"] < b.properties["cm:created"]) ? -1 : (a.properties["cm:created"] > b.properties["cm:created"]) ? 1 : 0;
});
children.forEach(function (child) {
    items.push(evaluateItem(child, substituteTitle));
});

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

        var children = getChildren(document);
        var numberOfChildren = children === null ? 0 : children.length;

        itemObj.numberOfChildErrands = numberOfChildren;
        itemObj.numberOfChildElements = numberOfChildren;

        return itemObj;
    }
    return null;
}

function getChildren(item) {
    var result = [];
    var children = [];

    lecmPermission.pushAuthentication();
    lecmPermission.setRunAsUserSystem();
    var childrenErrands = errands.getChildErrands(item.nodeRef.toString());
    var childrenResolutions = errands.getChildResolutions(item.nodeRef.toString());
    lecmPermission.popAuthentication();

    if (childrenErrands) {
        children = children.concat(childrenErrands);
    }
    if (childrenResolutions) {
        children = children.concat(childrenResolutions);
    }
    children.forEach(function (child) {
        if (lecmPermission.hasReadAccess(child) || 'VIEW_NO' !== viewLinksMode) {
            result.push(child);
        }
    });

    return result;
}

