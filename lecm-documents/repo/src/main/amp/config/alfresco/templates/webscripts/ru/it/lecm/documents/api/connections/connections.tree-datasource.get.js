var documentNodeRef = args['documentNodeRef'];
var previosDocRef = args['previosDocRef'];

var linkedDocTypes = args['linkedDocTypes'];
var connectionTypes = args['connectionTypes'];
var filters = args['filters'] != null ? args['filters'].split(",") : [];
var substituteTitle = args['substituteTitle'] != null ? args['substituteTitle'] : "{lecm-document:ext-present-string}";

var items = [];

var parentConnections = documentConnection.getConnectionsWithDocument(documentNodeRef, false);
if (parentConnections != null && parentConnections.length > 0) {
	for (var i = 0; i < parentConnections.length; i++) {
		var connection = parentConnections[i];
		if (connection.assocs["lecm-connect:primary-document-assoc"]) {
			if (!previosDocRef || connection.assocs["lecm-connect:primary-document-assoc"][0].nodeRef != previosDocRef) {
                if (checkConnectedDocType(connection.assocs["lecm-connect:connected-document-assoc"][0], linkedDocTypes)
                    && checkConnectionType(connection, connectionTypes)) {
                    var skipped = false;
                    for (var j = 0; j < filters.length; j++) {
                        skipped = !applyFilter(connection.assocs["lecm-connect:connected-document-assoc"][0], filters[j]);
                        if (skipped) {
                            break;
                        }
                    }
                    if (!skipped) {
                        var obj = evaluateItem(connection, substituteTitle, true);
                        if (obj != null) {
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
                    && checkConnectionType(connection, connectionTypes)) {
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

model.items = items;
model.documentRef = documentNodeRef;

function checkConnectionType(connection, connType) {
    if (connType != null && connType.length > 0) {
        var currentConnectionCode = connection.assocs["lecm-connect:connection-type-assoc"][0].properties["lecm-connect-types:code"];
        if (connType.indexOf(currentConnectionCode) >= 0) {
            return true;
        }
    }
    return true;
}

function checkConnectedDocType(connectedDoc, docType) {
    if (docType != null && docType.length > 0) {
        var currentDocType = connectedDoc.shortName;
        if (docType.indexOf(currentDocType) >= 0) {
            return true;
        }
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
            itemObj.title = substitude.formatNodeTitle(document, substituteTitle);
            itemObj.nodeRef = document.nodeRef.toString();
            itemObj.docType = document.typeShort;
            itemObj.status = document.properties["lecm-statemachine:status"];
            itemObj.connectionType = item.assocs["lecm-connect:connection-type-assoc"][0].properties["cm:name"];
            itemObj.direction = direction;
            return itemObj;
        }
    }
    return null;
}

