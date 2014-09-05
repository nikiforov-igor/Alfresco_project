<import resource="classpath:/alfresco/site-webscripts/org/alfresco/share/imports/share-header.lib.js">
<import resource="classpath:/alfresco/site-webscripts/org/alfresco/callutils.js">
<import resource="classpath:/alfresco/site-webscripts/ru/it/lecm/documents/utils/document-utils.js">

 var getArmName = function(code) {
    if (code) {
        var uri = addParamToUrl('/lecm/arm/get', 'code', code);
        var arm = doGetCall(uri);
        if (arm && arm.name) {
            return arm.name;
        }
    }
    return null;
};

var headerTitle = widgetUtils.findObject(model.jsonModel, "id", "HEADER_TITLE");
if (headerTitle != null) {
    if (page.url.args) {
        if (page.id == "arm" && page.url.args.code) {
            headerTitle.config.label = getArmName(page.url.args.code) || headerTitle.config.label;
        } else if (page.id == "my-profile") {
            headerTitle.config.label = getArmName("lecm_my_profile") || headerTitle.config.label;
        } else if (page.id == "orgstructure-dictionary") {
            headerTitle.config.label = getArmName("orgstructure") || headerTitle.config.label;
        }
    }
}