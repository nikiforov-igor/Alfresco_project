var ERRAND_TYPE = 'lecm-errands:document';
var IS_SYSTEM = 'lecm-connect:is-system';
var viewLinksMode = '' + documentGlobalSettings.getLinksViewMode(); /*VIEW_ALL, VIEW_DIRECT, VIEW_NO*/

var documentNodeRef = args['documentNodeRef'];
var count = parseInt(args['count']);
var exclude = args['exclErrands'] ? ("" + args['exclErrands']) == "true" : true;

var checkIndirectAccess = args['checkInAccess'] ? ("" + args['checkInAccess']) == "true" : false;
var applyViewMode = args['applyViewMode'] ? ("" + args['applyViewMode']) == "true" : true;

var document = search.findNode(documentNodeRef);
var rootFolder = document != null ? documentConnection.getRootFolder(documentNodeRef) : null;

var itemsSystem = [];
var itemsUser = [];

var hasNext = false;
var k = 0;

if (null != rootFolder) {
    var connections = rootFolder.getChildren();
    if (connections != null && connections.length > 0) {
        for (var i = 0; i < connections.length; i++) {
            if (k < count) {
                var connectedDocumentAssoc = connections[i].assocs["lecm-connect:connected-document-assoc"];
                if (connectedDocumentAssoc != null && connectedDocumentAssoc.length == 1 && connectedDocumentAssoc[0].exists()) {
                    var connectedDocument = connectedDocumentAssoc[0];

                    if (lecmPermission.hasReadAccess(connectedDocument) || (applyViewMode && 'VIEW_NO' !== viewLinksMode)) {
                        /*Системные связи с документами всех типов, кроме поручений*/
                        if (connections[i].properties[IS_SYSTEM]) {
                            if (!exclude || !connectedDocument.isSubType(ERRAND_TYPE)) {
                                itemsSystem.push(connections[i]);
                                k++;
                            }
                        } else {
                            /*Пользовательские связи с документами всех типов*/
                            itemsUser.push(connections[i]);
                            k++;
                        }
                    }
                }
            } else {
                hasNext = true;
                break;
            }
        }
    }
    if (!hasNext) { /*проверяем есть ли обратные связи */
        hasNext = documentConnection.hasConnectionsWithDocument(documentNodeRef, checkIndirectAccess);
    }
}

model.isMlSupported = lecmMessages.isMlSupported();
model.items = itemsSystem.concat(itemsUser);
model.hasNext = hasNext;
model.documentService = documentScript;
model.lecmPermission = lecmPermission;
