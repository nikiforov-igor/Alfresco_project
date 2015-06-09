(function() {
	var secretaryStr = url.templateArgs.store_type + "://" + url.templateArgs.store_id + "/" + url.templateArgs.id;
	var chiefStr = args['chief'];
	var secretary = search.findNode(secretaryStr);
	var chief = search.findNode(chiefStr);
	model.chief = chief.nodeRef.toString();
	model.newTasksSecretary = secretary.nodeRef.toString();
	var tasksSecretaries = chief.sourceAssocs['lecm-secretary-aspects:can-receive-tasks-from-chiefs'];
	if (tasksSecretaries && tasksSecretaries.length) {
		model.oldTasksSecretary = tasksSecretaries[0].nodeRef.toString();
		tasksSecretaries[0].removeAssociation(chief, 'lecm-secretary-aspects:can-receive-tasks-from-chiefs');
	}
	secretary.createAssociation(chief, 'lecm-secretary-aspects:can-receive-tasks-from-chiefs');
})();
