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

model.filterRanges = getFilters("filter-range");
model.filterTypes = getFilters("filter-type");

var settingsStr = remote.connect("alfresco").get("/lecm/contracts/draft-root");
if (settingsStr.status == 200) {
    model.settings = eval ("(" + settingsStr + ")");
}