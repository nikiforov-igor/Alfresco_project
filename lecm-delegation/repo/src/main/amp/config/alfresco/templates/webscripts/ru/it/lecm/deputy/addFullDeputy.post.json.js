(function() {

	var chief = url.templateArgs.store_type + "://" + url.templateArgs.store_id + "/" + url.templateArgs.id;
	var chiefNodeRef = search.findNode(chief);

	var addedString = json.get('lecm-deputy_complete-deputy-assoc_added');
	var added = [];
	if(addedString && addedString != '') {
		added = addedString.split(',');
	}

	var removedString = json.get('lecm-deputy_complete-deputy-assoc_removed');
	var removed = [];
	if(removedString && removedString != '') {
		removed = removedString.split(',');
	}

	for each(el in added) {
		var deputyEmployee = search.findNode(el);
		deputyService.addCompleteDeputy(chiefNodeRef, deputyEmployee);
	}

	for each(el in removed) {
		var deputyEmployee = search.findNode(el);
		var deputyNodeAssoc = deputyEmployee.sourceAssocs['lecm-deputy:employee-assoc'];
		chiefNodeRef.removeAssociation(deputyNodeAssoc[0], 'lecm-deputy:deputy-assoc');
	}
})();