<import resource="classpath:/alfresco/templates/org/alfresco/import/alfresco-util.js">
<import resource="classpath:/alfresco/site-webscripts/org/alfresco/components/documentlibrary/include/documentlist.lib.js">
<import resource="classpath:/alfresco/site-webscripts/ru/it/lecm/documents/utils/document-utils.js">

function main() {
	AlfrescoUtil.param('nodeRef');
	AlfrescoUtil.param('site', null);
	AlfrescoUtil.param('container', 'documentLibrary');

	if (isFinalStatus(model.nodeRef)) {
		var documentDetails = DocumentUtils.getNodeDetails(model.nodeRef, model.site,
			{
				actions: true
			});
		if (documentDetails) {
			model.documentDetailsJSON = jsonUtils.toJSONString(documentDetails);
			doclibCommon();
		}
	}
}

function isFinalStatus(nodeRef, defaultValue) {
	var url = '/lecm/statemachine/isFinal?nodeRef=' + nodeRef;
	var result = remote.connect("alfresco").get(url);
	if (result.status != 200) {
		if (defaultValue !== undefined) {
			return defaultValue;
		}
		AlfrescoUtil.error(result.status, 'Could not get connections for node ' + nodeRef);
	}
	return eval('(' + result + ')').isFinal;
}

main();