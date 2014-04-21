var armNode = edsGlobalSettings.getArmDashletNode();

if (armNode == null) {
    var nodes = search.xpathSearch("/app:company_home/cm:Business_x0020_platform/cm:LECM/cm:Сервис_x0020_АРМ/lecm-dic:Настройки_x0020_АРМ/cm:АРМ_x0020_СЭД/cm:Моя_x0020_работа");
    if (nodes.length > 0) {
        // there should only be 1 result, get the first one
        armNode = nodes[0];
    }
}

if (armNode != null) {
    model.isExist = true;
    model.title = armNode.properties["cm:name"];
    model.baseQuery = armWrapper.getFullQuery(armNode);

    var filtersArray = [];

    var filters = armWrapper.getArmNodeChilds(armNode);

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

