main();

function main() {
	var exclusions = [];
	var sortField;
	var sortAsc;
	var startIndex = 0;
	var maxResults = -1;

	if (json && json.has("params")) {
		var pars = json.get("params");
		startIndex = pars.has("startIndex") ? pars.get("startIndex") : 0;
		maxResults = pars.has("maxResults") ? pars.get("maxResults") : -1;

		var sort = pars.get("sort");
		if (sort) {
			sortField = sort.split("\\\|")[0];
			sortAsc = (sort.split("\\\|")[1] == "true");
		}

		var template = search.findNode(pars.get("parent"));
		var exclusionsProp = template.properties["lecm-notification-template:exclusions-list"];
		if (exclusionsProp && exclusionsProp.length) {
			var exclusionsJson = eval('(' + exclusionsProp.toString() + ')');
			if (exclusionsJson.rows) {
				exclusions = sortResults(exclusionsJson.rows, sortField, sortAsc);
			}
		}
	}
	model.data = processResults(exclusions, startIndex, maxResults, exclusions.length);
}

function sortResults(list, sortField, sortAsc) {
	if (!list || !sortField) {
		return list;
	}
	list.sort(function (a, b) {
		var value1 = a[sortField];
		var value2 = b[sortField];
		if (!value1) {
			if (value2) {
				return -1;
			} else {
				return 0;
			}
		} else if (!value2) {
			return 1;
		} else if (value1 < value2) {
			return -1;
		} else if (value1 > value2) {
			return 1;
		} else {
			return 0;
		}
	});
	if (!sortAsc) {
		list.reverse();
	}
	return list;
}

function processResults(exclusions, startIndex, maxResults, total) {
	var results = [],
		added = 0,
		i, j;

	for (i = 0, j = exclusions.length; i < j; i++) {
		if (i < startIndex) {
			continue;
		}
		if (maxResults > 0 && maxResults <= added) {
			break;
		}

		var exclusion = exclusions[i];

		var employeeNode = search.findNode(exclusion.employee);
		var employeeShortName = employeeNode ? substitude.getObjectDescription(employeeNode) : "";
		var creatorNode = search.findNode(exclusion.creator);
		var creatorShortName = creatorNode ? substitude.getObjectDescription(creatorNode) : "";

		var nodeData = {};
		nodeData["prop_employee"] = {
			type: "text",
			value: employeeShortName,
			displayValue: employeeShortName
		};
		nodeData["prop_created"] = {
			type: "datetime",
			value: exclusion.created,
			displayValue: exclusion.created
		};
		nodeData["prop_creator"] = {
			type: "text",
			value: creatorShortName,
			displayValue: creatorShortName
		};
		results.push({
			node: {
				nodeRef: exclusion.employee,
				typeShort: "lecm-orgstr:employee",
				properties: {
					created: new Date(),
					modified: new Date()
				}
			},
			nodeData: nodeData,
			actionPermissions: {
				"create": true,
				"edit": true,
				"delete": true
			},
			createdBy: {userName: exclusion.creator, displayName: exclusion.creator},
			modifiedBy: {userName: exclusion.creator, displayName: exclusion.creator},
			tags: []
		});
		added++;
	}

	return ({
		versionable: false,
		userAccess: {
			view: true
		},
		paging: {
			totalRecords: total,
			startIndex: startIndex
		},
		items: results
	});
}
