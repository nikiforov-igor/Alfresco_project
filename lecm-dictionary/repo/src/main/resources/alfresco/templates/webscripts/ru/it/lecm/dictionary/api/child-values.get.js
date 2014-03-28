<import resource="classpath:/alfresco/templates/webscripts/ru/it/lecm/dictionary/api/dictionary.lib.js">

var children = dictionary.getChildren(args["nodeRef"]);
if (children != null && children.length > 0) {
    model.children = children;

    var items = [];
    for (var i = 0; i < children.length; i++) {
        var child = children[i];
        var item = {
            node: child,
            hasChildren:searchCounter.hasChildren(child.getNodeRef().toString(), null),
            propertiesName: getTypePropertiesName(child)
        };

        items.push(item);
    }
    model.items = items;
}
