<import resource="classpath:/alfresco/templates/org/alfresco/import/alfresco-util.js">
<import resource="classpath:/alfresco/site-webscripts/ru/it/lecm/documents/utils/permission-utils.js">
<import resource="classpath:/alfresco/site-webscripts/ru/it/lecm/documents/utils/document-utils.js">

function main() {
    AlfrescoUtil.param("nodeRef");
    model.containerHtmlId = args["containerHtmlId"];

    var data = getErrands(model.nodeRef);
    if (data != null) {
        model.data = data;
    }
    var nodeDetails = DocumentUtils.getNodeDetails(model.nodeRef);
    if (nodeDetails) {
        if (nodeDetails.item.node.properties.hasOwnProperty("lecm-document:subject-assoc-ref")) {
            model.subjectAssoc = nodeDetails.item.node.properties["lecm-document:subject-assoc-ref"];
        }
    }
    var uri = '/lecm/document-type/settings?docType=lecm-errands:document';
    model.errandsSettings = jsonUtils.toJSONString(remote.connect("alfresco").get(uri));
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
