<import resource="classpath:/alfresco/templates/webscripts/ru/it/lecm/search/evaluator.lib.js">
<import resource="classpath:/alfresco/templates/webscripts/ru/it/lecm/search/search.lib.js">
<import resource="classpath:/alfresco/templates/webscripts/org/alfresco/slingshot/datalists/parse-args.lib.js">

function main() {
    var roles = [];
    var fields;
    var nameSubstituteStrings;

    if (typeof json !== "undefined" && json.has("params")) {
        var pars = json.get("params");
        var employeeRef = (pars.get("parent").length() > 0)  ? pars.get("parent") : null;

        roles = orgstructure.getEmployeeOnlyBusinessRoles(employeeRef);
        fields = (pars.get("fields").length() > 0) ? pars.get("fields") : null;
        nameSubstituteStrings = (pars.get("nameSubstituteStrings") !== null) ? pars.get("nameSubstituteStrings") : null;
    }

    model.data = processResults(roles, fields, nameSubstituteStrings, 0, roles.length); // call method from search.lib.js
}

main();
