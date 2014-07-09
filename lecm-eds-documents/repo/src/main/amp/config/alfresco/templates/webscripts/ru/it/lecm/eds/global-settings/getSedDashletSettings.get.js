var arm = edsGlobalSettings.getArm();
var armNode = edsGlobalSettings.getArmDashletNode();
if (arm == null) {
    var arms = search.xpathSearch("/app:company_home/cm:Business_x0020_platform/cm:LECM/cm:Сервис_x0020_АРМ/lecm-dic:Настройки_x0020_АРМ/cm:АРМ_x0020_СЭД");
    if (arms.length > 0) {
        arm = arms[0];
    }
}
if (arm != null && armNode == null) {
    var nodes = arm.getChildAssocsByType("lecm-arm:accordion");
    if (nodes != null) {
        for (var i = 0; i < nodes.length; i++) {
            var node = nodes[i];
            if (node.properties["cm:name"] == "Моя работа") {
                armNode = node;
                break;
            }
        }
    }
}

if (arm != null && armNode != null) {
    model.dashletTitle = arm.properties["cm:name"];
    model.isExist = true;
    model.title = armNode.properties["cm:name"];
    model.baseQuery = armWrapper.getFullQuery(armNode);

    var filtersArray = [];

    var filters = armWrapper.getArmNodeChilds(armNode, true);

    for (var i = 0; i < filters.size(); i++) {
        var filter = filters.get(i);
        filtersArray.push({
            "title": filter.get("title"),
            "query": filter.get("query")
        });
    }
    model.filters = filtersArray;
} else {
    model.isExist = false;
}

