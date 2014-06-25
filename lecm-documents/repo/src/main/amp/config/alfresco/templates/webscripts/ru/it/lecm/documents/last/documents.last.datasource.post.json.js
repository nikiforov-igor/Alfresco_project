<import resource="classpath:/alfresco/templates/webscripts/ru/it/lecm/search/evaluator.lib.js">
<import resource="classpath:/alfresco/templates/webscripts/ru/it/lecm/search/search.lib.js">
<import resource="classpath:/alfresco/templates/webscripts/org/alfresco/slingshot/datalists/parse-args.lib.js">
<import resource="classpath:/alfresco/templates/webscripts/ru/it/lecm/search/filter.lib.js">

function main() {
    var items = [];
    var fields;
    var nameSubstituteStrings;

    if (typeof json !== "undefined" && json.has("params")) {
        var pars = json.get("params");

        fields = (pars.get("fields").length() > 0) ? pars.get("fields") : null;
        nameSubstituteStrings = (pars.get("nameSubstituteStrings") !== null) ? pars.get("nameSubstituteStrings") : null;
        items = documentScript.getLastDocuments();
    }
    model.data = processResults(items, fields, nameSubstituteStrings, 0, items.length); // call method from search.lib.js
}

main();
