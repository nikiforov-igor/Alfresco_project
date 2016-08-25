<import resource="classpath:/alfresco/site-webscripts/org/alfresco/share/imports/share-header.lib.js">
<import resource="classpath:/alfresco/site-webscripts/org/alfresco/callutils.js">
<import resource="classpath:/alfresco/site-webscripts/ru/it/lecm/documents/utils/document-utils.js">

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
	var nodeDetails;
	var presentString;
	var isMlSupported;
	var mlValue;
	if (nodeRef) {
		nodeDetails = DocumentUtils.getNodeDetails(nodeRef);
		if (nodeDetails) {
			isMlSupported = nodeDetails.isMlSupported;
			mlValue = nodeDetails.item.node.properties["lecm-document:ml-ext-present-string"];
			presentString = isMlSupported && mlValue ? mlValue : nodeDetails.item.node.properties["lecm-document:ext-present-string"];
			return msg.get('title.edit_document').replace('{0}', presentString);
		}
	}
	return null;
};

var headerTitle = widgetUtils.findObject(model.jsonModel, "id", "HEADER_TITLE");
if (headerTitle != null) {
	if (page.url.args) {
		if (page.id == "document-create" && page.url.args.documentType) {
			headerTitle.config.label = getDocumentTitleName(page.url.args.documentType) || headerTitle.config.label;
		} else if (page.id == "document-edit" && page.url.args.nodeRef) {
			headerTitle.config.label = getDocumentPresentString(page.url.args.nodeRef) || headerTitle.config.label;
		}
	}
}
