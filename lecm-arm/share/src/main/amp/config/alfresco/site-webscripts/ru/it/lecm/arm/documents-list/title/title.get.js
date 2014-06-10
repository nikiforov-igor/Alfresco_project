<import resource="classpath:/alfresco/site-webscripts/ru/it/lecm/documents/utils/document-utils.js">
<import resource="classpath:/alfresco/templates/org/alfresco/import/alfresco-util.js">
<import resource="classpath:alfresco/site-webscripts/org/alfresco/callutils.js">

function main() {
	var uri = addParamToUrl('/lecm/arm/get', 'code', args["code"]);
	model.arm = doGetCall(uri);
}

main();