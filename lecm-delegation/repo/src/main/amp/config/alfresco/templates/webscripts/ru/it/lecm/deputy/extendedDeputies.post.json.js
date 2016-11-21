<import resource="classpath:/alfresco/templates/webscripts/ru/it/lecm/search/evaluator.lib.js">
<import resource="classpath:/alfresco/templates/webscripts/ru/it/lecm/search/search.lib.js">
<import resource="classpath:/alfresco/templates/webscripts/org/alfresco/slingshot/datalists/parse-args.lib.js">
<import resource="classpath:/alfresco/templates/webscripts/ru/it/lecm/search/filter.lib.js">
(function() {

	function eliminateDuplicates(arr) {
		var i,
			out=[],
			obj={};

		for (i in arr) {
			obj[arr[i]]=0;
		}
		for (i in obj) {
			out.push(i);
		}
		return out;
	}

	var pars = json.get("params"),
		docNodeRefString = args['docNodeRef'],
		docNodeRef = (docNodeRefString) ? search.findNode(docNodeRefString) : null,
		currentEmployee = orgstructure.getCurrentEmployee(),
		fields = pars.get('fields'),
		nameSubstituteStrings = pars.get('nameSubstituteStrings'),
		children = [],
		employeeDeputy = [],
		deputyAssocs = currentEmployee.assocs['lecm-deputy:deputy-assoc'],
		chiefsAssocs = currentEmployee.assocs['lecm-secretary-aspects:chief-assoc'];

	var allDeputies = deputyAssocs ? deputyAssocs : [];

	if(chiefsAssocs) {
		for each(var chiefAssoc in chiefsAssocs) {
			var chiefDeputyAssocs = chiefAssoc.assocs['lecm-deputy:deputy-assoc'];

			for each(var chiefDep in chiefDeputyAssocs) {
				allDeputies.push(chiefDep);
			}
		}
	}

	eliminateDuplicates(allDeputies);

	for each(var deputy in allDeputies) {
		var employeeAssocs = deputy.assocs['lecm-deputy:employee-assoc'];
		if(employeeAssocs && employeeAssocs.length) {
			var employee = employeeAssocs[0];
			var employeeNodeRef = employee.nodeRef.toString();

			if(!employeeDeputy[employeeNodeRef]) {
				employeeDeputy[employeeNodeRef] = [];
			}
			employeeDeputy[employeeNodeRef].push(deputy);

			var canPush = true;

			for each(var child in children) {
				if(('' + child.nodeRef.toString() == '' + employeeNodeRef) || ('' + currentEmployee.nodeRef.toString() == '' + employeeNodeRef)) {
					canPush = false;
					break;
				}
			}

			if(canPush) {
				children.push(employee);
			}
		}
	}

	var data = processResults(children, fields, nameSubstituteStrings, 0, children.length);

	for each(var item in data.items) {
		var node = item.node;
		var highlight = false;
		var deputies = employeeDeputy[node.nodeRef];

		if(docNodeRef) {
			for each(deputy in deputies) {
				if(deputyService.isDeputyAcceptable(docNodeRef, deputy)) {
					highlight = true;
					break;
				}
			}
		}

		var itemData = item.nodeData;
		itemData.highlightable = {
			value: highlight,
			displayValue: highlight
		};
	}

	model.data = data;


})();
