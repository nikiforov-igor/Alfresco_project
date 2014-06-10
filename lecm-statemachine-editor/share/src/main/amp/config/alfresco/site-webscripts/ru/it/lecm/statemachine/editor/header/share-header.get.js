var headerTitle = widgetUtils.findObject(model.jsonModel, "id", "HEADER_TITLE");
if (headerTitle != null) {
    if (page.url.args) {
        if (page.id == "statemachine") {
            var title = "Statemachine Editor";
            var url = "/lecm/statemachine/editor/title?" + page.url.queryString;
            var json = remote.connect("alfresco").get(encodeURI(url));
            if (json.status == 200) {
                title = json;
            }
            headerTitle.config.label = title;
        }
    }
}