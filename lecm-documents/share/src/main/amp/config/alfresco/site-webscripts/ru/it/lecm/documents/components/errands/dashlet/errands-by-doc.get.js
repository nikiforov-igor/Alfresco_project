<import resource="classpath:/alfresco/site-webscripts/ru/it/lecm/documents/utils/permission-utils.js">

(function () {
	var nodeRef = args["nodeRef"];
	var url = '/lecm/document/api/getProperties?nodeRef=' + nodeRef;
	var result = remote.connect('alfresco').get(url);
	var obj;
	var isRegistered;

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


	model.nodeRef = nodeRef;
	model.hasStatemachine = hasStatemachine(nodeRef);
	model.isErrandsStarter = isStarter("lecm-errands:document");
	model.hasPermission = hasPermission(nodeRef, PERM_ACTION_EXEC);
	model.isRegistered = isRegistered;
})();
