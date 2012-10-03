<import resource="classpath:/alfresco/templates/webscripts/ru/it/lecm/orgstructure/evaluator.lib.js">
    <import resource="classpath:/alfresco/templates/webscripts/ru/it/lecm/orgstructure/filters.lib.js">
        <import resource="classpath:/alfresco/templates/webscripts/ru/it/lecm/orgstructure/parse-args.lib.js">

function getData() {
    // Use helper function to get the arguments
    var parsedArgs = ParseArgs.getParsedArgs();
    if (parsedArgs === null) {
        return;
    }

    var fields = null;
    if (json.has("fields")) {
        // Convert the JSONArray object into a native JavaScript array
        fields = [];
        var jsonFields = json.get("fields"),
            numFields = jsonFields.length();

        for (count = 0; count < numFields; count++) {
            fields.push(jsonFields.get(count).replaceFirst("_", ":"));
        }
    }

    // Try to find a filter query based on the passed-in arguments
    var filter = parsedArgs.filter,
        allNodes = [], node,
        items = [];

    if (filter == null || filter.filterId == "all") {
        // Use non-query method
        var unitNode = parsedArgs.listNode;
        if (unitNode != null) {
            //TODO Call Bean OR JS for getting results
            var uc_assocs = unitNode.assocs["lecm-orgstr:unit-composition-assoc"];
            for each (uc_assoc in uc_assocs){
                var employees = uc_assoc.assocs["lecm-orgstr:composition-employee-assoc"];
                //ТОлько один сотрудник в ассоциации
                var employee = employees[0];
                allNodes.push(employee);
            }
        }
    }

    if (allNodes.length > 0) {
        for each(node in allNodes) {
            try {
                items.push(Evaluator.run(node, fields));
            }
            catch (e) {
            }
        }
    }

    return (
    {
        fields:fields,
        paging:{
            totalRecords:items.length,
            startIndex:0
        },
        parent:{
            node:parsedArgs.listNode,
            userAccess:{
                create:parsedArgs.listNode.hasPermission("CreateChildren")
            }
        },
        items:items
    });
}

model.data = getData();