<import resource="classpath:/alfresco/templates/webscripts/ru/it/lecm/search/search.post.json.js">
//пробегаемся по результирующему списку графиков работы и фильтруем их
//если сотрудник технолог, то исходный список не трогаем
//если сотрудник не технолог, но является руководителем, то получаем список всех его подчиненных и фильтруем список по подчиненным
//во всех остальных случаях список будет пустой
//получаем текущего сотрудника который залез на эту страницу
var currentEmployee = orgstructure.getCurrentEmployee();
if (!currentEmployee) {
	logger.log("ERROR: current employee is null!");
	destroyData();
} else {
	var isEngineer = orgstructure.isCalendarEngineer(currentEmployee.nodeRef.toString());
	var isBoss = orgstructure.isBoss(currentEmployee.nodeRef.toString(), true);

	var items = model.data.items;

	if (items.length > 0 && !isEngineer && isBoss) {
		// если чувак не технолог, то получаем список его подчиненных.
		// в результирующую выборку попадут только те сотрудники которые есть в списке подчиненных
		if (!currentEmployee.nodeRef) {
			logger.log("ERROR: there is no nodeRef for currentEmployee");
		}
		var employees = orgstructure.getBossSubordinate(currentEmployee.nodeRef, true);
		// получаем расписания по сотрудникам
		var schedules = [];
		for (var i = 0; i < employees.length; i++) {
			var employeeRef = employees[i].nodeRef;
			if (!employeeRef) {
				logger.log("ERROR: there is no nodeRef for employee");
			}
			var employeeSchedule = schedule.getScheduleByOrgSubject(employeeRef);
			if (employeeSchedule != null) {
				schedules.push(employeeSchedule);
			}

		}
		// получаем подразделения, в которых мы являемся начальником
		var bossUnits = orgstructure.getEmployeeUnits(currentEmployee.nodeRef, true);
		for (i = 0; i < bossUnits.length; i++) {
			var bossUnitRef = bossUnits[i].nodeRef;
			schedules.push(schedule.getScheduleByOrgSubject(bossUnitRef));
		}
		// получаем все дочерние подразделения тех, где мы босс
		var bossSubUnits = [];
		for (i = 0; i < bossUnits.length; i++) {

			var bossUnit = bossUnits[i];
			bossUnitRef = bossUnit.nodeRef;
			var subUnits = orgstructure.getSubUnits(bossUnitRef, true, true);
			for (var j = 0; j < subUnits.length; j++) {
				var subUnitRef = subUnits[j].nodeRef;
				var subUnitSchedule = schedule.getScheduleByOrgSubject(subUnitRef);
				if (subUnitSchedule == null) {
					continue;
				}
				var seen = false;
				for (var k = 0; k < schedules.length; k++) {
					if (schedules[k].toString() == subUnitSchedule.toString()) {
						seen = true;
					}
				}
				if (!seen) {
					schedules.push(subUnitSchedule);
				}
			}
		}

		// добавляем себя в массив с расписаниями
		schedules.push(schedule.getScheduleByOrgSubject(currentEmployee.nodeRef));

		// бежим по model.data.items для каждого элемента проверяем его наличие в employees
		// если его нет то удаляем его из model.data.items
		
		var actualItems = [];
		for (i = 0; i < items.length; i++) {
			var item = items[i];
			for (j = 0; j < schedules.length; j++) {
				if (item.node.equals(schedules[j])) {
					actualItems.push(item);
					break;
				}
			}
		}
		model.data.items = actualItems;
		model.data.paging.totalRecords = actualItems.length;
	} else if (isEngineer) {
	// не делаем ничего. все показываем
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