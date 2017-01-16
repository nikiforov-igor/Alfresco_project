function main() {
    var nodeRef = args.nodeRef;
    var skipCount = args.skipCount != null ? args.skipCount : 0;
    var maxItems = args.maxItems;
    var rolesFields = args.rolesFields;

    var document = search.findNode(nodeRef);

    var errands = [];
    var nodes = [];

    if (document != null) {
        var connectedDocuments = documentConnection.getConnectedWithDocument(document, true);
        if (connectedDocuments != null && connectedDocuments.length > 0) {
            for (var i = 0; i < connectedDocuments.length; i++) {
                var connected = connectedDocuments[i];
                if (connected.typeShort == "lecm-errands:document") {
                    errands.push(connected.nodeRef.toString());
                }
            }
        }

        var rootFolder = documentConnection.getRootFolder(document.nodeRef.toString());
        var childConnections = rootFolder.getChildren();
        if (childConnections != null && childConnections.length > 0) {
            for (i = 0; i < childConnections.length; i++) {
                var connection = childConnections[i];
                if (connection.assocs["lecm-connect:connected-document-assoc"]) {
                    var connected = connection.assocs["lecm-connect:connected-document-assoc"][0];
                    if (connected.typeShort == "lecm-errands:document") {
                        errands.push(connected.nodeRef.toString());
                    }
                }
            }
        }
        if (errands.length > 0) {
            var query = "TYPE:\"lecm-errands:document\" ";

            var idQuery = "";
            for (i = 0; i < errands.length; i++) {
                idQuery += "ID:" + errands[i].replace(":", "\\:");
                if (i < errands.length - 1) {
                    idQuery += " OR "
                }
            }
            if (idQuery.length > 0) {
                query = query + " AND (" + idQuery + ")";
            }

            var currentEmployee = orgstructure.getCurrentEmployee();

            var fields = rolesFields != null ? ("" + rolesFields).split(",") : [];
            var fieldsQuery = "";
            for (i = 0; i < fields.length; i++) {
                var field = fields[i];
                if (fieldsQuery.length > 0) {
                    fieldsQuery += " OR ";
                }
                fieldsQuery += "@" + escapeQName(field) + ":\"*" + currentEmployee.nodeRef.toString() + "*\"";
            }

            if (fieldsQuery.length > 0) {
                query = query + " AND (" + fieldsQuery + ")";
            }

            // выполняем запрос с ограничением
            var queryDef = {
                query: query,
                language: "fts-alfresco",
                page: {maxItems: maxItems, skipCount: skipCount},
                onerror: "no-results",
                sort: [
                    {
                        column: "@cm:created",
                        ascending: true
                    }]
            };
            nodes = search.query(queryDef);
        }
    }

    model.records = [];
    for each (var record in nodes) {
        model.records.push(    {
            "nodeRef": record.getNodeRef().toString(),
            "record": record.properties["lecm-document:present-string"],
            "date": record.properties["lecm-errands:limitation-date"],
            "title": record.properties["lecm-errands:title"],
            "summary":record.properties["lecm-errands:content"],
            "status":record.properties["lecm-statemachine:status"],
            "number":   record.properties["lecm-errands:number"].toString(),
            "initiator": record.assocs["lecm-errands:initiator-assoc"][0].nodeRef.toString(),
            "initiator_name": record.properties["lecm-errands:initiator-assoc-text-content"],
            "executor": record.assocs["lecm-errands:executor-assoc"][0].nodeRef.toString(),
            "executor_name": record.properties["lecm-errands:executor-assoc-text-content"],
            "isExpired": record.properties["lecm-errands:is-expired"],
            "isImportant": record.properties["lecm-errands:is-important"],
            "subject": (record.properties["lecm-document:subject-assoc"] && record.properties["lecm-document:subject-assoc"].length) ?
                record.properties["lecm-document:subject-assoc"][0].nodeRef.toString() : null
        });
    }
    model.skipCount = skipCount;
    model.totalItems = nodes.length;
    model.maxItems = maxItems;
}

function escapeQName(qname) {
    var separator = qname.indexOf(':'),
        namespace = qname.substring(0, separator),
        localname = qname.substring(separator + 1);

    return escapeString(namespace) + ':' + escapeString(localname);
}

function escapeString(value) {
    var result = "";
    for (var i = 0, c; i < value.length; i++) {
        c = value.charAt(i);
        if (i == 0) {
            if (!((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || (c >= 'а' && c <= 'я') || (c >= 'А' && c <= 'Я') || c == '_')) {
                result += '\\';
            }
        }
        else {
            if (!((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || (c >= 'а' && c <= 'я') || (c >= 'А' && c <= 'Я') || c == '_' || c == '$' || c == '#')) {
                result += '\\';
            }
        }
        result += c;
    }
    return result;
}
main();