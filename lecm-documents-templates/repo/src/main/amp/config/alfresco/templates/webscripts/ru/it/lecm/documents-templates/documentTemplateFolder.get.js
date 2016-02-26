/* global model, documentTemplateService */

(function() {
	var serviceFolder = documentTemplateService.getDocumentTemplateFolder();
	model.nodeRef = serviceFolder.nodeRef.toString();
	model.xpath = serviceFolder.qnamePath;
})();
