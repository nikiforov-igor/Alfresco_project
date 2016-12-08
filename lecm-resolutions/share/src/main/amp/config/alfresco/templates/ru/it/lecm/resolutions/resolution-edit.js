<import resource="classpath:/alfresco/site-webscripts/ru/it/lecm/documents/utils/permission-utils.js">

	function main() {
	model.hasPermission = hasPermission(page.url.args.nodeRef, PERM_ATTR_EDIT);
}

	main();