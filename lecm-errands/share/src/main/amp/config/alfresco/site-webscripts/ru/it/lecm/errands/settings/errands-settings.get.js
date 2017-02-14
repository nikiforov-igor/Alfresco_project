<import resource="classpath:alfresco/site-webscripts/org/alfresco/callutils.js">
(function() {
	var uri = addParamToUrl('/lecm/document-type/settings', 'docType', 'lecm-errands:document');
	model.errandsSettings = jsonUtils.toJSONString(doGetCall(uri));
})();
