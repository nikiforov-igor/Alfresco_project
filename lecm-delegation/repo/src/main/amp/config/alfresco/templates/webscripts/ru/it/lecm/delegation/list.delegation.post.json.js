<import resource="classpath:/alfresco/templates/webscripts/ru/it/lecm/search/search.post.json.js">
//пробегаемся по результирующему списку параметров делегирования и фильтруем их
//если сотрудник технолог, то исходный список не трогаем
//если сотрудник не технолог, но является руководителем, то получаем список всех его подчиненных и фильтруем список по подчиненным
//во всех остальных случаях список будет пустой
/*
logger.log ("*********************************************************************************************");
for (var prop in model) {
	logger.log (prop);
}
if (model.data) {
	logger.log ("model.data = " + jsonUtils.toJSONString (model.data));
}
logger.log ("*********************************************************************************************");
*/
//получаем текущего сотрудника который залез на эту страницу
var currentEmployee = orgstructure.getCurrentEmployee ();
if (currentEmployee) {
	logger.log ("current employee is " + currentEmployee.name + " " + currentEmployee.nodeRef);
} else {
	logger.log ("current employee is null!!!!!!!!!!");
}
//ищем бизнес роль технолога
var brEngineer = orgstructure.getBusinessRoleDelegationEngineer ();
if (!brEngineer) {
	logger.log ("ERROR: there is no engineer business role!");
}

//по этой бизнес роли находим всех сотрудников которые там есть
var employees = orgstructure.getEmployeesByBusinessRole (brEngineer.nodeRef, true);

//среди них ищем нашего текущего сотрудника
var isEngineer = false;
for (var i = 0; i < employees.length; ++i) {
	if (currentEmployee.equals (employees[i])) {
		isEngineer = true;
		break;
	}
}

var employees;
if (!isEngineer) {
	// если чувак не технолог, то получаем список его подчиненных.
	// в результирующую выборку попадут только те сотрудники которые есть в списке подчиненных
	logger.log ("getBossSubordinate");
	if (!currentEmployee.nodeRef) {
		logger.log ("ERROR: there is no nodeRef for currentEmployee");
	}
	employees = orgstructure.getBossSubordinate (currentEmployee.nodeRef);
	//добавляем самого себя в список сотрудников
	employees.push(currentEmployee);
} else {
	//Иначе, получаем всех видимых нам сотрудников 
	var employees = orgstructure.getAllEmployees();
	logger.log ("current employee " + currentEmployee.name + " is an engineer. An engineer can see delegation options for all employees");
}
	//получаем delegation-opts по сотрудникам
	var delegationOpts = [];
	for (var i = 0; i < employees.length; ++i) {
		var employeeRef = employees[i].nodeRef;
		if (!employeeRef || employees[i].properties["lecm-dic:active"] == false) {
			logger.log ("ERROR: there is no nodeRef for employee");
		} else {
            delegationOpts.push (delegation.getDelegationOpts (employeeRef));
        }
	}
	//бежим по model.data.items для каждого элемента проверяем его наличие в employees
	//если его нет то удаляем его из model.data.items
	var items = model.data.items;
	var actualItems = [];
	for (var i = 0; i < items.length; ++i) {
		var item = items[i];
		for (var j = 0; j < delegationOpts.length; ++j) {
			if (item.node.equals (delegationOpts[j])) {
				actualItems.push (item);
				break;
			}
		}
	}
	model.data.items = actualItems;
	model.data.paging.totalRecords = actualItems.length;
//} else {
//	//удалим себя из списка, к себе на страницу мы и так можем попасть
//	logger.log ("current employee " + currentEmployee.name + " is an engineer. An engineer can see delegation options for all employees");
//}
