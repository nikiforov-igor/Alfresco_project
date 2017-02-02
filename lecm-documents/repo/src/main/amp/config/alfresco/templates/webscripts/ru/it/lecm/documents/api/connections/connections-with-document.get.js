var ERRAND_TYPE = 'lecm-errands:document';
var IS_SYSTEM = 'lecm-connect:is-system';
var viewLinksMode = '' + documentGlobalSettings.getLinksViewMode();  /*VIEW_ALL, VIEW_DIRECT, VIEW_NO*/

var documentNodeRef = args['documentNodeRef'];
var exclude = args['exclErrands'] ? ("" + args['exclErrands']) == "true" : true;
var applyViewMode = args['applyViewMode'] ? ("" + args['applyViewMode']) == "true" : true;

var itemsSystem = [];
var itemsUser = [];

var document = search.findNode(documentNodeRef);
if (document) {
    var connections = documentConnection.getConnectionsWithDocument(documentNodeRef, false);

    for (var i = 0; i < connections.length; i++) {
        var connectedDocumentAssoc = connections[i].assocs["lecm-connect:primary-document-assoc"];
        if (connectedDocumentAssoc != null && connectedDocumentAssoc.length == 1 && connectedDocumentAssoc[0].exists()) {
            var connectedDocument = connectedDocumentAssoc[0];

            if (lecmPermission.hasReadAccess(connectedDocument) || (applyViewMode && 'VIEW_ALL' == viewLinksMode)){ /*фильтр по доступу*/
                /*Системные связи с документами всех типов, кроме поручений*/
                if (connections[i].properties[IS_SYSTEM]) {
                    if (!exclude || !connectedDocument.isSubType(ERRAND_TYPE)) {
                        itemsSystem.push(connections[i]);
                    }
                } else {
                    /*Пользовательские связи с документами всех типов*/
                    itemsUser.push(connections[i]);
                }
            }
        }
    }
}

/*для Source ассоциаций - порядок обратный, сортируем по дате создания*/
itemsSystem = itemsSystem.reverse();
itemsUser = itemsUser.reverse();

model.isMlSupported = lecmMessages.isMlSupported();
model.items = itemsSystem.concat(itemsUser);
model.documentService = documentScript;
model.lecmPermission = lecmPermission;
