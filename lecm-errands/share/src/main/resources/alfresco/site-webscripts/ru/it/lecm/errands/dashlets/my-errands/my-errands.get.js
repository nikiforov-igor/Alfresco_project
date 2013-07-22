<import resource="classpath:alfresco/site-webscripts/org/alfresco/components/workflow/workflow.lib.js">
model.hiddenTaskTypes = getHiddenTaskTypes();

var myConfig = new XML(config.script),
sort = [];

for each(var xmlSort in myConfig..sort)
{
    sort.push(
        {
            type: xmlSort.@type.toString(),
            parameters: xmlSort.@parameters.toString()
        });
}
model.sorting = sort;

model.maxItems = getMaxItems();