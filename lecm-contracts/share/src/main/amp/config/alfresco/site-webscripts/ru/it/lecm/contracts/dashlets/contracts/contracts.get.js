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

function isStarter() {
    var url = '/lecm/documents/employeeIsStarter?docType=lecm-contract:document';
    var result = remote.connect("alfresco").get(url);
    if (result.status != 200) {
        return false;
    }
    var perm = eval('(' + result + ')');
    return (("" + perm) == "true");
}

model.filterRanges = getFilters("filter-range");
model.filterTypes = getFilters("filter-type");
model.isStarter = isStarter();

var settingsStr = remote.connect("alfresco").get("/lecm/document-type/settings?docType=lecm-contract:document");
if (settingsStr.status == 200) {
    model.settings = eval ("(" + settingsStr + ")");
}