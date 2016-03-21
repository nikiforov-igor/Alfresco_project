(function () {
	var states = ['NOT_STARTED', 'NOT_REVIEWED', 'REVIEWED'];
	var result = [];
	model.result = result;

	var reviewListRef = args['reviewListRef'];
	var excludeEmployes = [];

	var reviewTable = search.findNode(reviewListRef);
	var reviewTableData = documentTables.getTableDataRows(reviewTable.nodeRef.toString());
	for each (var row in reviewTableData) {
		var reviewer = row.associations['lecm-review-ts:reviewer-assoc'][0];
		var state = row.properties['lecm-review-ts:review-state'];
		if (states.indexOf(state) > -1) {
			excludeEmployes.push('' + reviewer.nodeRef.toString());
		}
	}
	var allEmployees = orgstructure.getAllEmployees();
	for each (var employee in allEmployees) {
		if (excludeEmployes.indexOf('' + employee.nodeRef.toString()) == -1) {
			result.push(employee);
		}
	}

})();
