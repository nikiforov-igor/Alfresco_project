(function () {
	var UUID = Packages.java.util.UUID;

	var destinationNodeStr = json.getString('alf_destination');
	var destinationNode = search.findNode(destinationNodeStr);
	var assocName = 'assoc_lecm-workflow_assignee-employee-assoc'
	var refs, i, refsLength, employeeNode, assigneeItem, assigneeItems = [], props = [];

	refs = json.getString(assocName).split(',');


	for (i = 0, refsLength = refs.length; i < refsLength; ++i) {
		props['lecm-workflow:assignee-order'] = i;
		employeeNode = search.findNode(refs[i]);
		assigneeItem = destinationNode.createNode(UUID.randomUUID().toString(), 'lecm-workflow:assignee', props);
		assigneeItem.createAssociation(employeeNode, 'lecm-workflow:assignee-employee-assoc');

		assigneeItems.push(assigneeItem);
	}

	model.assigneeItems = assigneeItems;
})();
