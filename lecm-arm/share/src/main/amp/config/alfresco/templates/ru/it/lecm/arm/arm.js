if (page.url.args["code"] != null && page.url.args["code"] != "") {
    if (page.url.args["path"] != null && page.url.args["path"] != "") {
        var path = remote.connect("alfresco").get("/lecm/arm/convert?code=" + encodeURI(page.url.args["code"]) + "&path=" + encodeURI(page.url.args["path"]));
        if (path.status == 200) {
            model.path = path;
        }
    }

    var jsonStr = remote.connect("alfresco").get("/lecm/arm/get?code=" + encodeURI(page.url.args["code"]));
    var obj = {};
    if (jsonStr.status == 200) {
        obj = eval("(" + jsonStr + ")");
    }
    model.showCalendar = obj.showCalendar;
    model.showCreateButton = obj.showCreateButton;
}

model.currentUser = user.id;