<import resource="classpath:/alfresco/site-webscripts/ru/it/lecm/documents/utils/permission-utils.js">

function main() {
    var hasPerm = hasPermission(args["nodeRef"], PERM_WF_TASK_LIST);
    if (hasPerm) {
        var url = "/lecm/statemachine/api/tasks?nodeRef=" + args["nodeRef"] + "&state=active";
        var json = remote.connect("alfresco").get(url);
        if (json.status == 200) {
            var obj = eval("(" + json + ")");
            model.data = obj;
        }
    }

    var statuses = "В работе,На доработке,На утверждении контролером,На утверждении инициатором";

//    var url = "/lecm/errands/api/documentMyErrands?nodeRef=" + args["nodeRef"] + "&statuses=" + statuses;
    var url = "/lecm/errands/api/documentMyErrands?nodeRef=" + args["nodeRef"];
    var json = remote.connect("alfresco").get(url);
    if (json.status == 200) {
        var obj = eval("(" + json + ")");
        model.myErrandsData = obj;
    }

//    var url = "/lecm/errands/api/documentErrandsIssuedByMe?nodeRef=" + args["nodeRef"] + "&statuses=" + statuses;
    var url = "/lecm/errands/api/documentErrandsIssuedByMe?nodeRef=" + args["nodeRef"];
    var json = remote.connect("alfresco").get(url);
    if (json.status == 200) {
        var obj = eval("(" + json + ")");
        model.errandsIssuedByMeData = obj;
    }

    var settingsStr = remote.connect("alfresco").get("/lecm/document-type/settings?docType=lecm-errands:document");
    if (settingsStr.status == 200) {
        model.errandsDashletSettings = settingsStr;
    }
}

main();