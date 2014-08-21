<import resource="classpath:/alfresco/templates/webscripts/ru/it/lecm/search/evaluator.lib.js">
<import resource="classpath:/alfresco/templates/webscripts/ru/it/lecm/search/search.lib.js">
<import resource="classpath:/alfresco/templates/webscripts/org/alfresco/slingshot/datalists/parse-args.lib.js">
<import resource="classpath:/alfresco/templates/webscripts/ru/it/lecm/search/filter.lib.js">

function main() {
    var items = [];
    var fields;
    var nameSubstituteStrings;
    var dateHash = [];

    if (typeof json !== "undefined" && json.has("params")) {
        var pars = json.get("params");

        nameSubstituteStrings = (pars.get("nameSubstituteStrings") !== null) ? pars.get("nameSubstituteStrings") : null;
        fields = (pars.get("fields").length() > 0) ? pars.get("fields") : null;
        var activeNodes = pars.get("searchNodes");
        var activeNodesEval = eval("(" + activeNodes + ")");
        var items = [];
        activeNodesEval.forEach(function (item) {
            var node = search.findNode(item.nodeRef);
            if (node != null) {
                items.push(node);
                dateHash[item.nodeRef] = item.date;
            }
        });
    }
    var data = processResults(items, fields, nameSubstituteStrings, 0, items.length); // call method from search.lib.js
    data.items.forEach(function (item) {
        var uid = item.node.nodeRef.toString();
        item.nodeData["prop_cm_created"].value = dateHash[uid];
    });
    model.data = data;
}

main();
