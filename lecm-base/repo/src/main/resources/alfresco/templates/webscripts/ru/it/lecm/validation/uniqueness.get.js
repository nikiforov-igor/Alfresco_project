if (args.newValue == undefined || args.newValue.length == 0
    || args.propertyName == undefined || args.propertyName.length == 0
    || ((args.nodeRef == undefined || args.nodeRef.length == 0) && (args.typeName == undefined || args.typeName.length == 0))) {
    model.isUnique = "false";
} else {
    var typeName;
    if (args.typeName != undefined && args.typeName.length > 0) {
        typeName = args.typeName;
    } else {
        var node = search.findNode(args.nodeRef);
        typeName = node.getType();
    }

    var nodes = search.luceneSearch("TYPE:\"" + typeName + "\" AND @" + args.propertyName.replace(":", "\\:") + ":\"" + args.newValue + "\"");

    model.isUnique = (nodes.length > 0) ? "false" : "true";
}
