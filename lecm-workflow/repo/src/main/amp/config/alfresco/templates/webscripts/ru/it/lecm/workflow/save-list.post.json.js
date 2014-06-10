model.jsonResponse = "";
try {
	model.jsonResponse = lecmWorkflowService.saveAssigneesList(json);
} catch (e) {
	var ex = e.javaException;
	var DuplicateChildNodeNameException = Packages.org.alfresco.service.cmr.repository.DuplicateChildNodeNameException;
	var dcne = new DuplicateChildNodeNameException(null, null, null, null);
	if (ex && ex.getClass().isAssignableFrom(dcne.getClass())) {
		status.code = 418;
		model.jsonResponse = '{"error":"I\'m a teapot"}';
	} else {
		throw e;
	}
}
