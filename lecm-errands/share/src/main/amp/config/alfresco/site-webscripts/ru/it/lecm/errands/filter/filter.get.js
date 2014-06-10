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

model.filtersAssign = getFilters("filter-assign");
model.filtersDate = getFilters("filter-date");

