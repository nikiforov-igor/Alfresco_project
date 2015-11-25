var documentNodeRef = args['documentNodeRef'];
var previosDocRef = args['previosDocRef'];

var linkedDocTypes = args['linkedDocTypes'];
var connectionTypes = args['connectionTypes'];
var onlyDirect = 'true' == args['onlyDirect'];
var isErrandCard = 'true' == args['isErrandCard'];
var isFirstLayer = 'true' == args['isFirstLayer'];

var filters = args['filters'] != null ? args['filters'].split(",") : [];
var substituteTitle = args['substituteTitle'] != null ? args['substituteTitle'] : "{lecm-document:ext-present-string}";

var exclude = args['exclErrands'] ? ("" + args['exclErrands']) == "true" : false;

var items = [];

var document = search.findNode(documentNodeRef);
var excludeErrands = document != null && document.isSubType("lecm-eds-document:base") && exclude;

if (isFirstLayer) {
    items = [];
    var properties = documentScript.getProperties(documentNodeRef);
    var firstItem = {};
    var item = search.findNode(documentNodeRef);
    firstItem.title = item.properties["lecm-document:present-string"];
    firstItem.nodeRef = documentNodeRef.toString();
    firstItem.docType = item.properties["lecm-document:doc-type"];
    firstItem.status = item.properties["lecm-statemachine:status"];
    firstItem.connectionType = "Корневой элемент";
    firstItem.direction = '';

    var childErrands = item.sourceAssocs["lecm-errands:additional-document-assoc"];
    var primaryDocAssocs = item.sourceAssocs["lecm-connect:primary-document-assoc"];
    var connectedDocAssocs = item.sourceAssocs["lecm-connect:connected-document-assoc"];

    var numberOfChildErrands = childErrands === null ? 0 : childErrands.length;
    var numberOfPrimaryDocAssocs = primaryDocAssocs === null ? 0 : primaryDocAssocs.length;
    var numberOfConnectedDocAssocs = connectedDocAssocs === null ? 0 : connectedDocAssocs.length;

    firstItem.numberOfChildErrands = numberOfChildErrands;
    firstItem.numberOfChildElements = numberOfChildErrands  + numberOfPrimaryDocAssocs + numberOfConnectedDocAssocs;
    if ((isErrandCard && firstItem.numberOfChildErrands > 0) || (!isErrandCard && firstItem.numberOfChildElements > 0)) {
        items.push(firstItem);
    }

} else {
    var parentConnections = documentConnection.getConnectionsWithDocument(documentNodeRef, false);
    if (parentConnections != null && parentConnections.length > 0) {
        for (var i = 0; i < parentConnections.length; i++) {
            var connection = parentConnections[i];
            if (connection.assocs["lecm-connect:primary-document-assoc"]) {
                if (!previosDocRef || connection.assocs["lecm-connect:primary-document-assoc"][0].nodeRef != previosDocRef) {
                    if (checkConnectedDocType(connection.assocs["lecm-connect:connected-document-assoc"][0], linkedDocTypes)
                        && checkConnectionType(connection, connectionTypes)
                        && (!excludeErrands || !connection.assocs["lecm-connect:connected-document-assoc"][0].isSubType("lecm-errands:document"))) {
                        var skipped = false;
                        for (var j = 0; j < filters.length; j++) {
                            skipped = !applyFilter(connection.assocs["lecm-connect:connected-document-assoc"][0], filters[j]);
                            if (skipped) {
                                break;
                            }
                        }
                        if (!skipped) {
                            var obj = evaluateItem(connection, substituteTitle, true);
                            if (obj != null && (!onlyDirect || (onlyDirect && obj.direction == 'direct'))) {
                                items.push(obj);
                            }
                        }
                    }
                }
            }
        }
    }

    var rootFolder = documentConnection.getRootFolder(documentNodeRef);
    var childConnections = rootFolder.getChildren();
    if (childConnections != null && childConnections.length > 0) {
        for (var i = 0; i < childConnections.length; i++) {
            var connection = childConnections[i];
            if (connection.assocs["lecm-connect:connected-document-assoc"]) {
                if (!previosDocRef || connection.assocs["lecm-connect:connected-document-assoc"][0].nodeRef != previosDocRef) {
                    if (checkConnectedDocType(connection.assocs["lecm-connect:connected-document-assoc"][0], linkedDocTypes)
                        && checkConnectionType(connection, connectionTypes)
                        && (!excludeErrands || !connection.assocs["lecm-connect:connected-document-assoc"][0].isSubType("lecm-errands:document"))) {
                        if (filters != null) {
                            var skipped = false;
                            for (var j = 0; j < filters.length; j++) {
                                skipped = !applyFilter(connection.assocs["lecm-connect:connected-document-assoc"][0], filters[j]);
                                if (skipped) {
                                    break;
                                }
                            }
                            if (!skipped) {
                                var obj = evaluateItem(connection, substituteTitle, false);
                                if (obj != null) {
                                    items.push(obj);
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

model.items = items;
model.documentRef = documentNodeRef;

function checkConnectionType(connection, connType) {
    if (connType != null && connType.length > 0) {
        var currentConnectionCode = connection.assocs["lecm-connect:connection-type-assoc"][0].properties["lecm-connect-types:code"];
        return connType.indexOf(currentConnectionCode) >= 0;
    }
    return true;
}

function checkConnectedDocType(connectedDoc, docType) {
    if (docType != null && docType.length > 0) {
        var currentDocType = connectedDoc.typeShort;
        return docType.indexOf(currentDocType) >= 0;
    }
    return true;
}

function applyFilter(connectedDoc, filter) {
    if (filter != null && filter.length > 0) {
        var filterParts = filter.split("~");
        var propName = filterParts[0];
        var propValue = filterParts.length == 2 ? filterParts[1] : null;

        var currentValue = connectedDoc.properties[propName];
        if (currentValue == null) {
            return propValue == null;
        } else {
            return ("" + currentValue) == ("" + propValue);
        }
    }
    return true;
}

function evaluateItem(item, substituteTitle, isParent) {
    var itemObj = {};

    if (item != null) {
        var document = null;
        var direction = null;
        if (isParent) {
            document = item.assocs["lecm-connect:primary-document-assoc"][0];
            direction = "reverse";
        } else {
            document = item.assocs["lecm-connect:connected-document-assoc"][0];
            direction = "direct";
        }
        if (document != null) {

            if (isErrandCard) {
                if (document.typeShort != "lecm-errands:document") {
                    return null;
                }
            }

            itemObj.title = substitude.formatNodeTitle(document, substituteTitle);
            itemObj.nodeRef = document.nodeRef.toString();
            itemObj.docType = document.typeShort;
            itemObj.status = document.properties["lecm-statemachine:status"];
            itemObj.connectionType = item.assocs["lecm-connect:connection-type-assoc"][0].properties["cm:name"];
            itemObj.direction = direction;

            var childErrands = document.sourceAssocs["lecm-errands:additional-document-assoc"];
            var primaryDocAssocs = document.sourceAssocs["lecm-connect:primary-document-assoc"];
            var connectedDocAssocs = document.sourceAssocs["lecm-connect:connected-document-assoc"];

            var numberOfChildErrands = childErrands === null ? 0 : childErrands.length;
            var numberOfPrimaryDocAssocs = primaryDocAssocs === null ? 0 : primaryDocAssocs.length;
            var numberOfConnectedDocAssocs = connectedDocAssocs === null ? 0 : connectedDocAssocs.length;

            itemObj.numberOfChildErrands = numberOfChildErrands;
            itemObj.numberOfChildElements = numberOfChildErrands  + numberOfPrimaryDocAssocs + numberOfConnectedDocAssocs;

            return itemObj;
        }
    }
    return null;
}

