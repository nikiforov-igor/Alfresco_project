<import resource="classpath:/alfresco/templates/webscripts/org/alfresco/repository/forms/form.post.js">
<import resource="classpath:/alfresco/templates/webscripts/ru/it/lecm/controls-editor/formProcessor/formProcessor.lib.js">

if (status.code == status.STATUS_OK && model.persistedObject) {
	createControlParams(model.persistedObject);
}
