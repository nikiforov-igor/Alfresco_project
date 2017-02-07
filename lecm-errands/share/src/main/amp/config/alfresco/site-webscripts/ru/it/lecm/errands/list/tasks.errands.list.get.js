<import resource="classpath:/alfresco/templates/org/alfresco/import/alfresco-util.js">
<import resource="classpath:/alfresco/site-webscripts/ru/it/lecm/documents/utils/permission-utils.js">

function main() {
	var url, result, obj, isRegistered;

	AlfrescoUtil.param("nodeRef");
	model.hasStatemachine = hasStatemachine(model.nodeRef);
	model.isErrandsStarter = isStarter("lecm-errands:document");

	url = '/lecm/document/api/getProperties?nodeRef=' + model.nodeRef;
	result = remote.connect('alfresco').get(url);
	if (result.status == 200) {
		obj = eval('(' + result + ')');
		if (obj && obj.length) {
			isRegistered = obj[0]['reg-data-is-registered'];
		} else {
			isRegistered = false;
		}
	} else {
		isRegistered = false;
	}
	model.isRegistered = !!isRegistered;
}

main();
