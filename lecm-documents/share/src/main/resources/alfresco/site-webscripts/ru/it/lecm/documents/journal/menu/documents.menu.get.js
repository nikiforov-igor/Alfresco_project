function main()
{
    // Actions
    var filtersSet = [],
        myConfig = new XML(config.script),
        xmlFiltersSet = myConfig.filtersSet;

    for each (var xmlFilter in xmlFiltersSet.filter)
    {
        var cases = [];
        for each (var xmlCase in xmlFilter.record)
        {
            cases.push(
                {
                    id: xmlCase.@id.toString(),
                    value: xmlCase.@value.toString(),
                    label: xmlCase.@label.toString()
                });
        }

        filtersSet.push(
            {
                type: xmlFilter.@type.toString(),
                label: xmlFilter.@label.toString(),
                cases: cases
            });
    }

    model.filtersSet = filtersSet;
}

main();
