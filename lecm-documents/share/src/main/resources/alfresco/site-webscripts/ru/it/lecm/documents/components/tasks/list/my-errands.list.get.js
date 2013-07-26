<import resource="classpath:/alfresco/templates/org/alfresco/import/alfresco-util.js">

function main() {
    AlfrescoUtil.param("nodeRef");
    AlfrescoUtil.param("myErrandsState");

    var url = "/lecm/errands/api/documentMyErrands?nodeRef=" + args["nodeRef"];
    var json = remote.connect("alfresco").get(url);
    if (json.status == 200) {
        var obj = eval("(" + json + ")");
        model.errandsData = obj;
    }

    var settingsStr = remote.connect("alfresco").get("/lecm/document-type/settings?docType=lecm-errands:document");
    if (settingsStr.status == 200) {
        model.errandsDashletSettings = settingsStr;
    }

    var today = new Date();
    model.todayDate = today.getDate() + "/" + today.getMonth() + "/" + today.getFullYear();
    model.soonDate = new Date();
}

main();