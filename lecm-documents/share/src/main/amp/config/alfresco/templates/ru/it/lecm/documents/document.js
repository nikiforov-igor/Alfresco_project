<import resource="classpath:/alfresco/site-webscripts/ru/it/lecm/documents/utils/document-utils.js">

function main() {
    var url = '/lecm/document/api/getPermissions?nodeRef=' + page.url.args.nodeRef;
    var result = remote.connect("alfresco").get(url);
    model.hasPermission = false;
    if (result.status == 200) {
        var access = eval('(' + result + ')');
        if (access && (access.readAccess)) {
            model.hasPermission = (("" + access.readAccess) == "true");
        }
    }

    url = '/lecm/documents/hasStatemachine?nodeRef=' + page.url.args.nodeRef;
    result = remote.connect("alfresco").get(url);
    if (result.status == 200) {
        var hasStatemachine = eval('(' + result + ')');
        model.hasStatemachine = hasStatemachine;
    } else {
        model.hasStatemachine = false;
    }

    url = '/lecm/node/typeShort?nodeRef=' + page.url.args.nodeRef;
    result = remote.connect("alfresco").get(url);
    if (result.status == 200) {
        var obj = eval('(' + result +')');
        model.documentType = obj.typeShort;
    }

    url = '/lecm/document/default-expand-view?docType=' + model.documentType;
    result = remote.connect("alfresco").get(url);
    if (result.status == 200) {
        var obj = eval('(' + result +')');
        model.defaultExpandComponent = obj.defaultExpandView;
    }

    model.dependencies = DocumentUtils.getDependencies("LecmDocumentDetails");
}

main();

