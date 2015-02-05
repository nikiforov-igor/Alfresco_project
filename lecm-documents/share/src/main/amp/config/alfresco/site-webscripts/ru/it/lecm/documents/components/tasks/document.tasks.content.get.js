<import resource="classpath:/alfresco/templates/org/alfresco/import/alfresco-util.js">
<import resource="classpath:/alfresco/site-webscripts/ru/it/lecm/documents/utils/permission-utils.js">

function main() {
    AlfrescoUtil.param("nodeRef");
    model.containerHtmlId = args["containerHtmlId"];

    var data = getTasks(model.nodeRef);
    if (data != null) {
        model.data = data;
    }
}

function getTasks(nodeRef) {
    var url = "/lecm/errands/api/documentMyErrands?nodeRef=" + args["nodeRef"] + "&filter=active&myTasksLimit=5";
    var result = remote.connect("alfresco").get(url);
    if (result.status != 200) {
        return null;
    }
    return eval('(' + result + ')');
}

main();
