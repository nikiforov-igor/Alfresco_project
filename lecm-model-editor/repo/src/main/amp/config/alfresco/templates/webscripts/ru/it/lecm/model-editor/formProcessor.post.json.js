<import resource="classpath:/alfresco/templates/webscripts/org/alfresco/repository/forms/form.post.json.js">
<import resource="classpath:/alfresco/templates/webscripts/ru/it/lecm/model-editor/formProcessor.lib.js">

if (status.code == status.STATUS_OK && model.persistedObject) {
	createFormAttributes(model.persistedObject);
}
