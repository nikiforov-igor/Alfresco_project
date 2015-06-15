<import resource="classpath:/alfresco/templates/webscripts/ru/it/lecm/search/evaluator.lib.js">
<import resource="classpath:/alfresco/templates/webscripts/ru/it/lecm/search/search.lib.js">
<import resource="classpath:/alfresco/templates/webscripts/org/alfresco/slingshot/datalists/parse-args.lib.js">
<import resource="classpath:/alfresco/templates/webscripts/ru/it/lecm/search/filter.lib.js">
(function() {

	const DEFAULT_PAGE_SIZE = 20;
	const DEFAULT_INDEX = 0;

	var params = {};
    if (typeof json !== "undefined" && json.has("params")) {
        var pars = json.get("params");
        params =
        {
            searchConfig: (pars.get("searchConfig").length() > 0)  ? pars.get("searchConfig") : null,
            sort: (pars.get("sort").length() > 0)  ? pars.get("sort") : null,
            maxResults:(pars.get("maxResults") !== null) ? parseInt(pars.get("maxResults"), 10) : DEFAULT_PAGE_SIZE,
            fields:(pars.get("fields").length() > 0) ? pars.get("fields") : null,
			nameSubstituteStrings:(pars.get("nameSubstituteStrings") !== null) ? pars.get("nameSubstituteStrings") : null,
			showInactive: pars.get("showInactive") == true,
            parent: (pars.get("parent").length() > 0)  ? pars.get("parent") : null,
			searchNodes: (pars.get("searchNodes").length() > 0)  ? pars.get("searchNodes").split(",") : null,
            itemType:(pars.get("itemType").length() > 0)  ? pars.get("itemType") : null,
            startIndex: pars.has("startIndex") ? parseInt(pars.get("startIndex"), 10) : DEFAULT_INDEX,
            useChildQuery: pars.has("useChildQuery") ? ("" + pars.get("useChildQuery") == "true") : false,
            useFilterByOrg: pars.has("useFilterByOrg") ? ("" + pars.get("useFilterByOrg") == "true") : true,
            useOnlyInSameOrg: pars.has("useOnlyInSameOrg") ? ("" + pars.get("useOnlyInSameOrg") == "true") : false,
            filter: pars.has("filter")  ? pars.get("filter") : null
        };
    }


	var currentEmployee = orgstructure.getCurrentEmployee();
	if (currentEmployee) {
		logger.log("current employee is " + currentEmployee.name + " " + currentEmployee.nodeRef);
	} else {
		logger.log("current employee is null!!!!!!!!!!");
		return;
	}

	var employees = [];

	//ищем бизнес роль технолога
	var brEngineer = orgstructure.getBusinessRoleDelegationEngineer ();

	//по этой бизнес роли находим всех сотрудников которые там есть
	employees = orgstructure.getEmployeesByBusinessRole(brEngineer.nodeRef, true);

	//среди них ищем нашего текущего сотрудника
	var isEngineer = false;
	for (var i = 0; i < employees.length; ++i) {
		if (currentEmployee.equals (employees[i])) {
			isEngineer = true;
			break;
		}
	}

	var employees = [];
	var totalCount = 0;

	if(!isEngineer) {
		// Теперь не технолог не имеет доступа

		return;

	} else {
		// Иначе просто выполним запрос для сотрудников
		params.itemType = 'lecm-orgstr:employee';
		params.parent = orgstructure.getEmployeesDirectory().nodeRef.toString();

		model.data = getSearchResults(params);
		var result = model.data.items;

		for each(item in result) {
			employees.push(item.node);
		}

		totalCount = model.data.paging.totalRecords;
	}

	var delegationOpts = [];

	for each(employee in employees) {
		var employeeRef = employee.nodeRef;
		var opts = delegation.getDelegationOpts(employeeRef);
		if(opts) {
			delegationOpts.push(opts);
		}
	}

	model.data = processResults(delegationOpts, params.fields, params.nameSubstituteStrings, params.startIndex, params.total);
	model.data.paging.totalRecords = totalCount;

})();
