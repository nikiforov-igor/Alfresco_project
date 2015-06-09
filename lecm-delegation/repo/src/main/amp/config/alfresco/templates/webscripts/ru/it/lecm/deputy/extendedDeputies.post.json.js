<import resource="classpath:/alfresco/templates/webscripts/ru/it/lecm/search/evaluator.lib.js">
<import resource="classpath:/alfresco/templates/webscripts/ru/it/lecm/search/search.lib.js">
<import resource="classpath:/alfresco/templates/webscripts/org/alfresco/slingshot/datalists/parse-args.lib.js">
<import resource="classpath:/alfresco/templates/webscripts/ru/it/lecm/search/filter.lib.js">
(function() {

	function eliminateDuplicates(arr) {
		var i,
			len=arr.length,
			out=[],
			obj={};

		for (i=0;i<len;i++) {
		  obj[arr[i]]=0;
		}
		for (i in obj) {
		  out.push(i);
		}
		return out;
	  }

	var pars = json.get("params");
	var docNodeRefString = args['docNodeRef'];
	var docNodeRef = null;
	var currentEmployee = orgstructure.getCurrentEmployee();

	if(docNodeRefString) {
		docNodeRef = search.findNode(docNodeRefString);
	}

	var fields = pars.get('fields'),
	nameSubstituteStrings = pars.get('nameSubstituteStrings'),
	children = [],
	employeeDeputy = [],
	deputyAssocs = null;

	deputyAssocs = currentEmployee.assocs['lecm-deputy:deputy-assoc'];

	chiefsAssocs = currentEmployee.assocs['lecm-secretary-aspects:chief-assoc'];

	if(chiefsAssocs) {
		for each(chiefAssoc in chiefsAssocs) {
			chiefDeputyAssocs = chiefAssoc.assocs['lecm-deputy:deputy-assoc'];

			for each(chiefDep in chiefDeputyAssocs) {
				deputyAssocs.push(chiefDep);
			}
		}
	}

	eliminateDuplicates(deputyAssocs);

	for each(deputy in deputyAssocs) {
		var employeeAssocs = deputy.assocs['lecm-deputy:employee-assoc'];
		if(employeeAssocs) {
			var employee = employeeAssocs[0];
			var employeeNodeRef = employee.nodeRef.toString();

			if(!employeeDeputy[employeeNodeRef]) {
				employeeDeputy[employeeNodeRef] = [];
			}
			employeeDeputy[employeeNodeRef].push(deputy);

			var canPush = true;

			for each(child in children) {
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

	for each(item in data.items) {
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
		itemData['highlightable'] = {
			value: highlight,
			displayValue: highlight
		}
	}

	model.data = data;


})();