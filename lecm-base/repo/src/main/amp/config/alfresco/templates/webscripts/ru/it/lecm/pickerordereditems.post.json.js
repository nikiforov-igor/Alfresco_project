<import resource="classpath:/alfresco/templates/webscripts/ru/it/lecm/pickerordereditems.lib.js">

function main()
{
    var items = getPickerItems();
    model.results = items.results;
    model.additionalProperties = items.additionalProperties;
}

main();