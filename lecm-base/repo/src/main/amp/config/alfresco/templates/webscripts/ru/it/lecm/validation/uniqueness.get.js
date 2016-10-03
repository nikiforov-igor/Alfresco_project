if (!args["newValue"] || args["newValue"].length == 0
    || !args["propertyName"] || args["propertyName"].length == 0) {
    model.isUnique = false;
    model.isUniqueInArchive = false;
} else {
    var queryBase = "=@" + (args["propertyName"].replace(":", "\\:")).split("-").join("\\-") + ":\"" + args["newValue"] + "\" ";

    var typeName = null;
    if (args["typeName"] && args["typeName"].length > 0 || args["nodeRef"] && args["nodeRef"].length > 0) {
        if (args["typeName"] && args["typeName"].length > 0) {
            typeName = args["typeName"];
        } else {
            var node = search.findNode(args["nodeRef"]);
            typeName = node.typeShort;
        }
    }
    if (typeName != null) {
        queryBase = "TYPE:\"" + typeName + "\" AND " + queryBase;
    }

    if (args["nodeRef"] && args["nodeRef"].length > 0) {
        queryBase = queryBase + " AND NOT ID:\"" + args["nodeRef"] + "\"";
    }

    var queryActive = queryBase + " AND NOT @lecm\\-dic\\:active:false";
    var queryArchive = queryBase + " AND @lecm\\-dic\\:active:false";

    var nodesActive = searchCounter.query(
        {
            query: queryActive,
            language: "fts-alfresco",
            onerror: "exception"
        }, false, false);

    var nodesArchive = searchCounter.query(
        {
            query: queryArchive,
            language: "fts-alfresco",
            onerror: "exception"
        }, false, false);

    model.isUnique = nodesActive <= 0;
    model.isUniqueInArchive = nodesArchive <= 0;
}
