<import resource="classpath:/alfresco/templates/webscripts/ru/it/lecm/search/search.post.json.js">
//пробегаемся по результирующему списку отсутствий и фильтруем их
//если сотрудник зашел на страницу через свой профиль, то показываем только его отсутсвия
//если сотрудник технолог, то исходный список не трогаем
//если сотрудник не технолог, но является руководителем, то получаем список всех его подчиненных и фильтруем список по подчиненным
//во всех остальных случаях список будет пустой
//получаем текущего сотрудника который залез на эту страницу

var pageContext = url.templateArgs["context"];

var showMyself = true;

var currentEmployee = orgstructure.getCurrentEmployee();
if (!currentEmployee) {
	logger.log("ERROR: current employee is null!");
	destroyData();
} else {
	var myAbsences = [];
	var items, i, j, abscentEmployeeRef;
	if (absence.isAbsenceAssociated(currentEmployee.nodeRef.toString())) {
		myAbsences = absence.getAbsenceByEmployee(currentEmployee.nodeRef.toString());
	}

	// в профиле показать только свои отсутствия
	if (pageContext.toString() == "profile") {
		items = model.data.items;
		for (i = 0; i < items.length; ++i) {
			abscentEmployeeRef =  items[i].node.assocs["lecm-absence:abscent-employee-assoc"][0].nodeRef;
			if (!abscentEmployeeRef.equals(currentEmployee.nodeRef)){
				items.splice(i, 1);
				model.data.paging.totalRecords -= 1;
				i--;
			}
		}

	} else if (pageContext.toString() == "admin") {
		var isEngineer = orgstructure.isCalendarEngineer(currentEmployee.nodeRef.toString());
		var isBoss = orgstructure.isBoss(currentEmployee.nodeRef.toString(), true);

		if (!isEngineer && isBoss) {
			// если чувак не технолог, то получаем список его подчиненных.
			// в результирующую выборку попадут только те сотрудники которые есть в списке подчиненных
			if (!currentEmployee.nodeRef) {
				logger.log("ERROR: there is no nodeRef for currentEmployee");
			}
			var employees = orgstructure.getBossSubordinate(currentEmployee.nodeRef, true);
			// получаем nodeRef сотрудников
			var emplyeeRefs = [];
			for (i = 0; i < employees.length; ++i) {
				var employeeRef = employees[i].nodeRef;
				if (employeeRef) {
					emplyeeRefs.push(employeeRef);
				} else {
					logger.log("ERROR: there is no nodeRef for employee");
				}
			}
			// бежим по model.data.items для каждого элемента проверяем его ассоциацию с сотрудником и наличие сотрудника в emplyeeRefs
			// если его нет, то удаляем его из model.data.items
			items = model.data.items;
			for (i = 0; i < items.length; ++i) {
				var seen = false;

				abscentEmployeeRef = items[i].node.assocs["lecm-absence:abscent-employee-assoc"][0].nodeRef;
				for (j = 0; j < emplyeeRefs.length; ++j) {
					if (abscentEmployeeRef.equals(emplyeeRefs[j])) {
						seen = true;
						break;
					}
				}
				if (!seen) {
					items.splice(i, 1);
					model.data.paging.totalRecords -= 1;
					i--;
				}
			}
		} else if (isEngineer) {
			if (!showMyself) {
				// удалим себя из списка, к себе на страницу мы и так можем попасть
				items = model.data.items;
				for (i = 0; i < items.length; ++i) {
					abscentEmployeeRef = items[i].node.assocs["lecm-absence:abscent-employee-assoc"][0].nodeRef;
					if (abscentEmployeeRef.equals(currentEmployee.nodeRef)){
						items.splice(i, 1);
						model.data.paging.totalRecords -= 1;
						i--;
					}
				}
			}
		} else {
			// непонятно, как сюда пришел. не показываем ничего.
			destroyData();
		}
	} else {
		// непонятно, как сюда пришел. не показываем ничего.
		destroyData();
	}
}

function destroyData() {
	items = model.data.items;
	itemsLength = items.length;
	items.splice(0, itemsLength);
	model.data.paging.totalRecords = 0;
}