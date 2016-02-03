<import resource="classpath:/alfresco/templates/webscripts/org/alfresco/repository/forms/form.post.json.js">
<import resource="classpath:/alfresco/templates/webscripts/ru/it/lecm/contractors/ds/org.contractor.form.lib.js">

if (status.code == status.STATUS_OK && model.persistedObject) {
	createLinkedOrganization(model.persistedObject);
}

