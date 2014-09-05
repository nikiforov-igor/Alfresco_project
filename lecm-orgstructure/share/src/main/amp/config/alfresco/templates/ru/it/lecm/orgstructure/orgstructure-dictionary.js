if (page.url.args["code"] != null && page.url.args["code"] != "" && page.url.args["path"] != null && page.url.args["path"] != "") {
    var path = remote.connect("alfresco").get("/lecm/arm/convert?code=" + encodeURI(page.url.args["code"]) + "&path=" + encodeURI(page.url.args["path"]));
    if (path.status == 200) {
        model.path = path;
    }
}

model.currentUser = user.id;