function getFilters(filterType)
{
    var myConfig = new XML(config.script),
        filters = [];

    for each (var xmlFilter in myConfig[filterType].filter)
    {
        filters.push(
            {
                type: xmlFilter.@type.toString(),
                label: xmlFilter.@label.toString()
            });
    }

    return filters;
}

var type = args["itemType"] ? args["itemType"] : null;
model.type = type;

model.filtersAuthor = getFilters("filter-author");

