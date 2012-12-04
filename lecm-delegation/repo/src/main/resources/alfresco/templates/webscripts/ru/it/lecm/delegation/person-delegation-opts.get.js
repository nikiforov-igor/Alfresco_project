logger.log ("current authenticated person is " + person.nodeRef);

//получили список employee которые есть в системе
var queryDef = {
	query: "TYPE:\"lecm-orgstr:employee\"",
	language: "fts-alfresco"
};

var employees = search.query(queryDef);

var personEmployee = null; //employee который связан с person

//проверяем что employees адекватен
if (employees) {
	//смотрим у каждого employee ассоциацию с person и выбираем нужного employee
	for (var i = 0; i < employees.length; ++i) {
		var currentEmployee = employees[i];
		var assocs = currentEmployee.assocs["lecm-orgstr:employee-person-assoc"];
		//проверяем что ассоциации имеются
		if (assocs) {
			var personAssoc = assocs[0]; //я знаю что ассоциация должна быть 1 к 1
			if (personAssoc.nodeRef.equals(person.nodeRef)) {
				personEmployee = currentEmployee;
				logger.log ("current authenticated employee is " + personEmployee.nodeRef);
			}
		}
	}
}

var employeeDelegationOpts = null;

//если мы нашли employee который связан с person то делаем поиск по параметрам делегирования для него
if (personEmployee) {
	queryDef = {
		query: "TYPE:\"lecm-d8n:delegation-opts\"",
		language: "fts-alfresco"
	};
	var delegationOpts = search.query (queryDef);
	if (delegationOpts) {
		for (var i = 0; i < delegationOpts.length; ++i) {
			var currentOpts = delegationOpts[i];
			var assocs = currentOpts.assocs["lecm-d8n:delegation-opts-owner-assoc"];
			//проверяем что ассоциации имеются
			if (assocs) {
				var ownerAssoc = assocs[0]; //я знаю что ассоциация должна быть 1 к 1
				if (ownerAssoc.nodeRef.equals(personEmployee.nodeRef)) {
					employeeDelegationOpts = currentOpts;
					logger.log ("employeeDelegationOpts = " + employeeDelegationOpts);
				}
			}
		}
	}
}
if (person) {
	model.person = person.nodeRef.toString();
} else {
	model.person = null;
}

if (personEmployee) {
	model.employee = personEmployee.nodeRef.toString();
} else {
	model.employee = null;
}

if (employeeDelegationOpts) {
	model.delegationOpts = employeeDelegationOpts.nodeRef.toString();
} else {
	model.delegationOpts = null;
}