function getFilters(filterType) {
    var myConfig = new XML(config.script), filters = [];

    for each(var xmlFilter in myConfig[filterType].filter)
    {
        var type = xmlFilter.@type.toString();
        var value = xmlFilter.@value.toString();
        var url = "/lecm/contracts/additionalDocsCount?type=" + value;
        var addDocs = eval("(" + remote.connect("alfresco").get(stringUtils.urlEncodeComponent(url)) + ")");
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
