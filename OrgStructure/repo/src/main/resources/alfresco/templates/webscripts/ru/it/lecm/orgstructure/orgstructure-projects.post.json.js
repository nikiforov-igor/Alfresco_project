<import resource="classpath:/alfresco/templates/webscripts/org/alfresco/slingshot/datalists/evaluator.lib.js">
    <import resource="classpath:/alfresco/templates/webscripts/org/alfresco/slingshot/datalists/filters.lib.js">
        <import resource="classpath:/alfresco/templates/webscripts/org/alfresco/slingshot/datalists/parse-args.lib.js">

        const REQUEST_MAX = 1000;

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

    // Use non-query method
    var unitNode = parsedArgs.listNode;
    if (unitNode != null) {
        //TODO Getting results here
        var parentNode = parsedArgs.listNode;
        if (parentNode != null) {
        //Ищем все папки внутри Регистра Проектов
        var pagedResult = parentNode.childFileFolders(false, true, Filters.IGNORED_TYPES, -1, -1, REQUEST_MAX, "cm:name", true, null);
        allNodes = pagedResult.page;
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