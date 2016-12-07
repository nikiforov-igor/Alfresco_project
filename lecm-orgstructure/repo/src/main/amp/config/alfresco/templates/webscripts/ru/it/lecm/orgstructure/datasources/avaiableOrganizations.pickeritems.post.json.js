<import resource="classpath:/alfresco/templates/webscripts/ru/it/lecm/pickeritems.lib.js">

    function main() {
        var items = getPickerItems();
        model.results = items.results;

        for each (var result in model.results)
        {
            var res = search.findNode("" + result.item.nodeRef);
            var orgUnit = orgstructure.getUnitByOrganization(res);
            if (orgUnit) {
                result.orgUnitPath = orgUnit.getQnamePath();
            }
        }

        model.additionalProperties = items.additionalProperties;
    };

main();
