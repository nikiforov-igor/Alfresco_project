var ERRAND_TYPE = 'lecm-errands:document';
var IS_SYSTEM = 'lecm-connect:is-system';
/*VIEW_ALL, VIEW_DIRECT, VIEW_NO*/
var viewLinksMode = '' + documentGlobalSettings.getLinksViewMode();
var isMlSupported = lecmMessages.isMlSupported();

var documentNodeRef = args['documentNodeRef'];
var previosDocRef = args['previosDocRef'];

/*filtering*/
var linkedDocTypes = args['linkedDocTypes'];
var connectionTypes = args['connectionTypes'];
var onlyDirect = 'true' == args['onlyDirect'];
var onlySystem = 'true' == args['onlySystem'];
var isErrandCard = 'true' == args['isErrandCard'];
var isFirstLayer = 'true' == args['isFirstLayer'];
var filters = args['filters'] != null ? args['filters'].split(",") : [];
var substituteTitle = args['substituteTitle'] != null ? args['substituteTitle'] : (isMlSupported ? "{lecm-document:ml-ext-present-string}" : "{lecm-document:ext-present-string}");
var applyViewMode = args['applyViewMode'] ? ("" + args['applyViewMode']) == "true" : true;
var exclude = args['exclErrands'] ? ("" + args['exclErrands']) == "true" : true;

var items = [];

var item = search.findNode(documentNodeRef);

if (isFirstLayer) {
    items = [];
    var properties = documentScript.getProperties(documentNodeRef);
    var firstItem = {};

    firstItem.title = (isMlSupported && item.properties["lecm-document:ml-present-string"]) ? item.properties["lecm-document:ml-present-string"] : item.properties["lecm-document:present-string"];
    firstItem.nodeRef = documentNodeRef.toString();
    firstItem.docType = item.properties["lecm-document:doc-type"];
    firstItem.status = item.properties["lecm-statemachine:status"];
    firstItem.connectionType = "Корневой элемент";
    firstItem.direction = '';
    firstItem.hasAccess = lecmPermission.hasReadAccess(item);

    var childErrands = item.sourceAssocs["lecm-errands:additional-document-assoc"];
    var primaryDocAssocs = item.sourceAssocs["lecm-connect:primary-document-assoc"];
    var connectedDocAssocs = item.sourceAssocs["lecm-connect:connected-document-assoc"];

    var numberOfChildErrands = childErrands === null ? 0 : childErrands.length;
    var numberOfPrimaryDocAssocs = primaryDocAssocs === null ? 0 : primaryDocAssocs.length;
    var numberOfConnectedDocAssocs = connectedDocAssocs === null ? 0 : connectedDocAssocs.length;

    firstItem.numberOfChildErrands = numberOfChildErrands;
    firstItem.numberOfChildElements = numberOfChildErrands + numberOfPrimaryDocAssocs + numberOfConnectedDocAssocs;
    if ((isErrandCard && firstItem.numberOfChildErrands > 0) || (!isErrandCard && firstItem.numberOfChildElements > 0)) {
        items.push(firstItem);
    }
} else {
    var connectedDocument, skipped;
    var rootFolder = documentConnection.getRootFolder(documentNodeRef);
    if (rootFolder != null) {
        var directConnections = rootFolder.getChildren();

        var directItemsSystem = [];
        var directItemsUser = [];

        if (directConnections != null && directConnections.length > 0) {
            for (var i = 0; i < directConnections.length; i++) {
                var directConnection = directConnections[i];

                if (directConnection.assocs["lecm-connect:connected-document-assoc"]) {
                    if (!previosDocRef || directConnection.assocs["lecm-connect:connected-document-assoc"][0].nodeRef != previosDocRef) {
                        connectedDocument = directConnection.assocs["lecm-connect:connected-document-assoc"][0];
                        if (checkConnectedDocType(connectedDocument, linkedDocTypes)
                            && checkConnectionType(directConnection, connectionTypes)) {
                            skipped = false;
                            for (var j = 0; j < filters.length; j++) {
                                skipped = !applyFilter(connectedDocument, filters[j]);
                                if (skipped) {
                                    break;
                                }
                            }
                            if (!skipped) {
                                var objDirect = evaluateItem(directConnection, substituteTitle, false);
                                if (objDirect != null) {
                                    if (!isErrandCard) {
                                        if (lecmPermission.hasReadAccess(connectedDocument) || (applyViewMode && 'VIEW_NO' !== viewLinksMode)) {
                                            /*Системные связи с документами всех типов, кроме поручений*/
                                            if (directConnection.properties[IS_SYSTEM]) {
                                                if (!exclude || !connectedDocument.isSubType(ERRAND_TYPE)) {
                                                    directItemsSystem.push(objDirect);
                                                }
                                            } else {
                                                /*Пользовательские связи с документами всех типов*/
                                                directItemsUser.push(objDirect);
                                            }
                                        }
                                    } else {
                                        if (lecmPermission.hasReadAccess(connectedDocument)) {
                                            if (!onlySystem || directConnection.properties[IS_SYSTEM]) {
                                                directItemsUser.push(objDirect);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    var parentConnections = documentConnection.getConnectionsWithDocument(documentNodeRef, false);

    var inDirectItemsSystem = [];
    var inDirectItemsUser = [];

    if (parentConnections != null) {
        for (var i = 0; i < parentConnections.length; i++) {
            var inDirectConnection = parentConnections[i];
            if (inDirectConnection.assocs["lecm-connect:primary-document-assoc"]) {
                if (!previosDocRef || inDirectConnection.assocs["lecm-connect:primary-document-assoc"][0].nodeRef != previosDocRef) {
                    connectedDocument = inDirectConnection.assocs["lecm-connect:primary-document-assoc"][0];
                    if (checkConnectedDocType(connectedDocument, linkedDocTypes)
                        && checkConnectionType(inDirectConnection, connectionTypes)) {
                        skipped = false;
                        for (var j = 0; j < filters.length; j++) {
                            skipped = !applyFilter(inDirectConnection.assocs["lecm-connect:connected-document-assoc"][0], filters[j]);
                            if (skipped) {
                                break;
                            }
                        }
                        if (!skipped) {
                            var inDirectObj = evaluateItem(inDirectConnection, substituteTitle, true);
                            if (inDirectObj != null && !onlyDirect) {
                                if (!isErrandCard) {
                                    if (lecmPermission.hasReadAccess(connectedDocument) || (applyViewMode && 'VIEW_ALL' == viewLinksMode)) {
                                        /*Системные связи с документами всех типов, кроме поручений*/
                                        if (inDirectConnection.properties[IS_SYSTEM]) {
                                            if (!connectedDocument.isSubType(ERRAND_TYPE)) {
                                                inDirectItemsSystem.push(inDirectObj);
                                            }
                                        } else {
                                            /*Пользовательские связи с документами всех типов*/
                                            inDirectItemsUser.push(inDirectObj);
                                        }
                                    }
                                } else {
                                    if (lecmPermission.hasReadAccess(connectedDocument)) {
                                        if (!onlySystem || inDirectConnection.properties[IS_SYSTEM]) {
                                            inDirectItemsUser.push(inDirectObj);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    var itemsSystem = [];
    var itemsUsers = [];

    itemsSystem = itemsSystem.concat(directItemsSystem).concat(inDirectItemsSystem.reverse());
    itemsUsers = itemsUsers.concat(directItemsUser).concat(inDirectItemsUser.reverse());

    items = itemsSystem.concat(itemsUsers);
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
            itemObj.hasAccess = lecmPermission.hasReadAccess(document);

            var childErrands = document.sourceAssocs["lecm-errands:additional-document-assoc"];
            var primaryDocAssocs = document.sourceAssocs["lecm-connect:primary-document-assoc"];
            var connectedDocAssocs = document.sourceAssocs["lecm-connect:connected-document-assoc"];

            var numberOfChildErrands = childErrands === null ? 0 : childErrands.length;
            var numberOfPrimaryDocAssocs = primaryDocAssocs === null ? 0 : primaryDocAssocs.length;
            var numberOfConnectedDocAssocs = connectedDocAssocs === null ? 0 : connectedDocAssocs.length;

            itemObj.numberOfChildErrands = numberOfChildErrands;
            itemObj.numberOfChildElements = numberOfChildErrands + numberOfPrimaryDocAssocs + numberOfConnectedDocAssocs;

            return itemObj;
        }
    }
    return null;
}

