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

	if(params.searchConfig && params.searchConfig.contains('fullTextSearch') && isEngineer) {
		model.data = getSearchResults(params);
	} else {

		if(isEngineer) {

			var tmpParams = {};
			tmpParams.fields = "lecm-orgstr_employee-last-name,lecm-orgstr_employee-first-name,lecm-orgstr_employee-middle-name";
			tmpParams.itemType = 'lecm-orgstr:employee';
			tmpParams.maxResults = params.maxResults;
			tmpParams.nameSubstituteStrings = ",,,{..lecm-orgstr:employee-link-employee-assoc(lecm-orgstr:employee-link-is-primary = true)/../../lecm-orgstr:element-short-name},{..lecm-orgstr:employee-link-employee-assoc(lecm-orgstr:employee-link-is-primary = true)/../lecm-orgstr:element-member-position-assoc/cm:name}"
			tmpParams.parent = orgstructure.getEmployeesDirectory().nodeRef.toString();
			tmpParams.searchConfig = params.searchConfig;
			tmpParams.searchNodes = null;
			tmpParams.showInactive = false;
			tmpParams.sort = 'lecm-orgstr:employee-last-name|true';
			tmpParams.startIndex = params.startIndex;
			tmpParams.useChildQuery = false;
			tmpParams.useFilterByOrg = false;
			tmpParams.useOnlyInSameOrg = false;
			tmpParams.filter = [];

			model.data = getSearchResults(tmpParams);
			var result = model.data.items;

			for each(item in result) {
				employees.push(item.node);
			}

			totalCount = model.data.paging.totalRecords;
		}

		var delegationOpts = [];

		for each(employee in employees) {
			var employeeRef = employee.nodeRef;
            if (!employeeRef || employees[i].properties["lecm-dic:active"] == false) {
                logger.log ("ERROR: there is no nodeRef for employee");
            } else {
                var opts = delegation.getDelegationOpts(employeeRef);
                if (opts) {
                    delegationOpts.push(opts);
                }
            }
		}

		model.data = processResults(delegationOpts, params.fields, params.nameSubstituteStrings, params.startIndex, totalCount);
		model.data.paging.totalRecords = totalCount;
	}

})();
