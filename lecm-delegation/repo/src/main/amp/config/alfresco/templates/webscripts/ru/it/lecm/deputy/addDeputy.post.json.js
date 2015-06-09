(function() {

	var chief = url.templateArgs.store_type + "://" + url.templateArgs.store_id + "/" + url.templateArgs.id;
	var chiefNodeRef = search.findNode(chief);

	var addedString = json.get('assoc_lecm-deputy_employee-assoc');
	var addedEmployee = search.findNode(addedString);

	var addedSubjectsString = json.get('assoc_lecm-deputy_subject-assoc');

	var node = deputyService.addDeputy(chiefNodeRef, addedEmployee, addedSubjectsString);
	model.persistedObject = node.nodeRef.toString();
})();