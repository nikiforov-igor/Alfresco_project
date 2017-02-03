<import resource="classpath:/alfresco/site-webscripts/ru/it/lecm/documents/utils/permission-utils.js">

function main() {
    model.hasStatemachine = (args["hasStatemachine"] == "true");
    model.nodeRef = args["nodeRef"];
    model.isErrandsStarter = (args["isErrandsStarter"] == "true");
    model.hasPermission = (args["hasPermission"] == "true");

    var url = "/lecm/errands/getIssuedToMeErrands?nodeRef=" + args["nodeRef"] +
    "&maxItems=1&rolesFields=lecm-errands:executor-assoc-ref,lecm-errands:coexecutors-assoc-ref";
    var json = remote.connect("alfresco").get(url);
    if (json.status == 200) {
        var obj = eval("(" + json + ")");
        model.errand = json;
        model.errandObj = obj;
        model.mayCreateReErrand = obj.data.length > 0;
    }

}

main();