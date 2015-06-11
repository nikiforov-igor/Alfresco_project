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

var getDocumentTitleName = function (type) {
    if (type) {
        var uri = addParamToUrl('/lecm/document-type/settings', 'docType', type);
        var arm = doGetCall(uri);
        if (arm && arm.title) {
            return msg.get("title.new_document").replace('{0}', arm.title);
        }
    }
    return null;
};

var getDocumentPresentString = function (nodeRef) {
    if (nodeRef) {
        var nodeDetails = DocumentUtils.getNodeDetails(nodeRef);
        if (nodeDetails) {
            return msg.get('title.edit_document').replace('{0}', nodeDetails.item.node.properties["lecm-document:ext-present-string"]);
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
        } else if (page.id == "event-create" && page.url.args.documentType) {
            headerTitle.config.label = getDocumentTitleName(page.url.args.documentType) || headerTitle.config.label;
        } else if (page.id == "event-edit" && page.url.args.nodeRef) {
            headerTitle.config.label = getDocumentPresentString(page.url.args.nodeRef) || headerTitle.config.label;
        }
    }
}