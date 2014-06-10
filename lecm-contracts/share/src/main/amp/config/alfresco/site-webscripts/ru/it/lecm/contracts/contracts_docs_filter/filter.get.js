function getFilters(filterType) {
    var myConfig = new XML(config.script), filters = [];
    var queryFilterId = myConfig["queryFilter"];
	model.queryFilterId = "" + queryFilterId;

    for each(var xmlFilter in myConfig[filterType].filter)
    {
        var type = xmlFilter.@type.toString();
        var value = xmlFilter.@value.toString();
        var url = "/lecm/contracts/additionalDocsCount?type=" + value + "&considerFilter=" + queryFilterId + '&active=' + args["active"];
        var addDocsStr = remote.connect("alfresco").get(stringUtils.urlEncodeComponent(url));
        var addDocs = [];
        if (addDocsStr.status == 200) {
            addDocs = eval("(" + addDocsStr + ")");
        }
        var count = addDocs.length;
        filters.push(
            {
                type: type,
                value: value,
                count: count
            });
    }

    return filters;
}

model.filters = getFilters("filters");
