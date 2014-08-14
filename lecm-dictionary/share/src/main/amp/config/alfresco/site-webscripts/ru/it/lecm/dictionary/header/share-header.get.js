<import resource="classpath:/alfresco/site-webscripts/org/alfresco/share/imports/share-header.lib.js">

var headerTitle = widgetUtils.findObject(model.jsonModel, "id", "HEADER_TITLE");
if (headerTitle != null) {
    headerTitle.config.targetUrl = page.properties["titleTargetUrl"] || headerTitle.config.targetUrl;

    if (page.url.args) {
        if (page.id == "dictionary") {
            headerTitle.config.targetUrl = "allDictionary";
        }
    }
}