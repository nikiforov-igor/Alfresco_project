(function() {

	function addSecretary(employee, chief) {
		if(!employee.hasAspect('lecm-secretary-aspects:is-secretary')) {
			employee.addAspect('lecm-secretary-aspects:is-secretary');
		}

		employee.createAssociation(chief, 'lecm-secretary-aspects:chief-assoc');
		secretarySecurity.addSecretary(chief, employee);
	}

	function removeSecretary(employee, chief) {

		employee.removeAssociation(chief, 'lecm-secretary-aspects:chief-assoc');
		employee.removeAssociation(chief, 'lecm-secretary-aspects:can-receive-tasks-from-chiefs');

		if(employee.assocs.length == 0) {
			employee.removeAspect('lecm-secretary-aspects:is-secretary');
		}
		secretarySecurity.removeSecretary(chief, employee);
	}

	var chief = url.templateArgs.store_type + "://" + url.templateArgs.store_id + "/" + url.templateArgs.id;;
	var chiefNode = search.findNode(chief);

	var addedString = json.get('lecm-secretary-aspects_sec-fake-assoc_added');
	var added = [];
	if(addedString && addedString != '') {
		added = addedString.split(',');
	}

	var removedString = json.get('lecm-secretary-aspects_sec-fake-assoc_removed');
	var removed = [];
	if(removedString && removedString != '') {
		removed = removedString.split(',');
	}

	for each(el in added) {
		var nodeToAdd = search.findNode(el);
		addSecretary(nodeToAdd, chiefNode);
	}

	for each(el in removed) {
		var nodeToRemove = search.findNode(el);
		removeSecretary(nodeToRemove, chiefNode);
	}

})();
