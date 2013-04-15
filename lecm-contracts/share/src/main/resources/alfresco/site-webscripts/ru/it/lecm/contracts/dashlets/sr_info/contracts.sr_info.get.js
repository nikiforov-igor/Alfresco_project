<import resource="classpath:/alfresco/templates/org/alfresco/import/alfresco-util.js">
<import resource="classpath:/alfresco/site-webscripts/ru/it/lecm/documents/utils/permission-utils.js">
<import resource="classpath:/alfresco/site-webscripts/ru/it/lecm/documents/utils/document-utils.js">

function main() {
	var dictionary = getDictionary();
	if (dictionary != null) {
		var dictionaryValues = getDictionaryValues(dictionary.nodeRef);
		if (dictionaryValues != null) {
			var data = [];
			for (var i = 0; i < dictionaryValues.length; i++) {
				data.push(DocumentUtils.getNodeDetails(dictionaryValues[i].nodeRef));
			}
			model.data = data;
		}
	}
}

function getDictionary(defaultValue) {
	var url = '/lecm/dictionary/api/getDictionary?dicName=' + encodeURI('Договора-НСИ');
	var result = remote.connect("alfresco").get(url);
	if (result.status != 200) {
		if (defaultValue !== undefined) {
			return defaultValue;
		}
		AlfrescoUtil.error(result.status, 'Could not get connections for node ' + nodeRef);
	}
	return eval('(' + result + ')');
}

function getDictionaryValues(nodeRef, defaultValue) {
	var url = '/lecm/dictionary/api/getChildrenItems.json?nodeRef=' + nodeRef;
	var result = remote.connect("alfresco").get(url);
	if (result.status != 200) {
		if (defaultValue !== undefined) {
			return defaultValue;
		}
		AlfrescoUtil.error(result.status, 'Could not get connections for node ' + nodeRef);
	}
	return eval('(' + result + ')');
}

main();

