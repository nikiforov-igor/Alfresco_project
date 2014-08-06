<import resource="classpath:/alfresco/templates/webscripts/ru/it/lecm/pickeritems.lib.js">

function main()
{
    var items = getPickerItems();
    var filteredResults = [];

    for (var i = 0; i < items.results.length; i++) {
        if (items.results[i].item.hasAspect("lecm-orgstr-aspects:is-organization-aspect")) {
            filteredResults.push(items.results[i]);
        }
    }
    model.results = filteredResults;
    model.additionalProperties = items.additionalProperties;
}

main();
