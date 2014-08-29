<import resource="classpath:/alfresco/templates/webscripts/ru/it/lecm/dictionary/dictionary-tree.get.js">
if (!orgstructure.isCurrentEmployeeHasBusinessRole("BR_DICTIONARIES_ENGINEER")) {
    var result = [];
    for each (var item in branch) {
        var node = item.node;
        if (node.properties["lecm-arm:code"] != "ADMIN") {
            result.push(item);
        }
    }
    model.branch = result;
}