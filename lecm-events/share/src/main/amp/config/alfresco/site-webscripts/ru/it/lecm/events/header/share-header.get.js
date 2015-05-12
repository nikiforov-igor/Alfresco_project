<import resource="classpath:/alfresco/site-webscripts/org/alfresco/share/imports/share-header.lib.js">
<import resource="classpath:/alfresco/site-webscripts/org/alfresco/callutils.js">
<import resource="classpath:/alfresco/site-webscripts/ru/it/lecm/documents/utils/document-utils.js">

var getArmUrl = function(nodeRef) {
	if (nodeRef) {
		var uri = addParamToUrl('/lecm/document/getArmUrl', 'nodeRef', nodeRef);
		var armUrl = doGetCall(uri);
		if (armUrl && armUrl.url) {
			return armUrl.url;
		}
	}
	return null;
};

var headerTitle = widgetUtils.findObject(model.jsonModel, "id", "HEADER_TITLE");
if (headerTitle != null) {
    headerTitle.config.targetUrl = page.properties["titleTargetUrl"] || headerTitle.config.targetUrl;

    if (page.url.args) {
        if (page.id == "event") {
            var nodeDetails = DocumentUtils.getNodeDetails(page.url.args.nodeRef);
            if (nodeDetails) {
                headerTitle.config.label = nodeDetails.item.typeTitle || headerTitle.config.label;
                headerTitle.config.targetUrl = getArmUrl(page.url.args.nodeRef);
            }
        }
    }
}