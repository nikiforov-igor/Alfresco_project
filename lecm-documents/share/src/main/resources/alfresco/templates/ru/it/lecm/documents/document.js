function main() {
    var url = '/lecm/document/api/getPermissions?nodeRef=' + page.url.args.nodeRef;
    var result = remote.connect("alfresco").get(url);
    if (result.status == 200) {
        var access = eval('(' + result + ')');
        if (access && (access.readAccess)) {
            model.hasPermission = (("" + access.readAccess) == "true");

            var errandsSettings = remote.connect("alfresco").get("/lecm/document-type/settings?docType=lecm-errands:document");
            if (errandsSettings.status == 200) {
                model.errandsSettings = errandsSettings;
            }
        }
    } else {
        model.hasPermission = false;
    }

    url = '/api/metadata?nodeRef=' + page.url.args.nodeRef;
    result = remote.connect("alfresco").get(url);
    if (result.status == 200) {
        var metadata = eval('(' + result + ')');
        if (metadata && (metadata.type)) {
            model.documentType = metadata.type;
        }
    }
}

main();

