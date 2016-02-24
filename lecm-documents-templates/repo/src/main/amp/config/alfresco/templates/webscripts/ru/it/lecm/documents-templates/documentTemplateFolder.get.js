/* global model, documentTemplateService */

(function() {
	var serviceFolder = documentTemplateService.getDocumentTemplateFolder();
	model.nodeRef = serviceFolder.nodeRef;
	model.xpath = serviceFolder.qnamePath;
})();
