<import resource="classpath:/alfresco/templates/org/alfresco/import/alfresco-util.js">
<import resource="classpath:/alfresco/site-webscripts/ru/it/lecm/documents/utils/permission-utils.js">

function main() {
    AlfrescoUtil.param("nodeRef");
    model.containerHtmlId = args["containerHtmlId"];

    var data = getErrands(model.nodeRef);
    if (data != null) {
        model.data = data;
    }
}

function getErrands(nodeRef) {
    var url = "/lecm/errands/api/documentErrandsRightBoard?nodeRef=" + args["nodeRef"]
    + "&filter=" + (args["state"] != null && args["state"] != "" ? args["state"] : "active")
    + "&errandsLimit=" + (args["errandsLimit"] != null ? args["errandsLimit"]  : "-1");
    var result = remote.connect("alfresco").get(url);
    if (result.status != 200) {
        return null;
    }
    return eval('(' + result + ')');
}

main();
