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
const ENGINEER_ID = "BR_ENGINEER";
var def = {
	query: "TYPE:\"lecm\\-orgstr:business\\-role\" AND (@lecm\\-orgstr:business\\-role\\-identifier:\"" + ENGINEER_ID + "\")",
	language: "fts-alfresco",
	namespace: "lecm-orgstr"
};
var results = search.query(def);
var brEngineerNodeRef = results[0].nodeRef;

//по этой бизнес роли находим всех сотрудников которые там есть
var employees = orgstructure.getEmployeesByBusinessRole (brEngineerNodeRef);

//среди них ищем нашего текущего сотрудника
var isEngineer = false;
for (var i = 0; i < employees.length; ++i) {
	if (currentEmployee.equals (employees[i])) {
		isEngineer = true;
		break;
	}
}


if (!isEngineer) {
	// если чувак не технолог, то получаем список его подчиненных.
	// в результирующую выборку попадут только те сотрудники которые есть в списке подчиненных
	logger.log ("getBossSubordinate");
	var employees = orgstructure.getBossSubordinate (currentEmployee.nodeRef);
	//получаем delegation-opts по сотрудникам
	var delegationOpts = [];
	for (var i = 0; i < employees.length; ++i) {
		delegationOpts.push (delegation.getDelegationOptsByEmployee (employees[i].nodeRef));
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
	//получаем все должностные позиции чувака и достаем оттуда руководящую позацию
//	var staffs = orgstructure.getEmployeeStaffs (currentEmployee.nodeRef);
//	var isBoss = false;
//	var bossStaff = null;
//	for (var i = 0; i < staffs.length; ++i) {
//	  bossStaff = staffs[i];
//	  isBoss = bossStaff.properties["lecm-orgstr:staff-list-is-boss"];
//	  logger.log ("staff_" + i + " " + bossStaff.nodeRef + " is boss = " + isBoss);
//	}
//	if (isBoss) {
//		//если сотрудник босс то получаем всех его подчиненных и фильтруем список параметров делегирования
//	} else {
//		//если сотрудник не босс то очищаем список
//		model.data.paging.startIndex = 0;
//		model.data.items = [];
//	}
} else {
	//удалим себя из списка, к себе на страницу мы и так можем попасть
	var delegationOpts = delegation.getDelegationOptsByEmployee (currentEmployee.nodeRef);
	logger.log (delegationOpts.name);

	var items = model.data.items;
	for (var i = 0; i < items.length; ++i) {
		var prop = items[i].node;
//		logger.log ("delegationOpts.equals (prop) = " + delegationOpts.equals (prop));
//		logger.log ("delegationOpts.nodeRef.equals (prop.nodeRef) = " + delegationOpts.nodeRef.equals (prop.nodeRef));
		if (delegationOpts.equals (prop)) {
			logger.log (prop.name + "found and must be removed from search result");
			items.splice (i, 1);
			logger.log ("item sucessfully removed");
			model.data.paging.totalRecords -= 1;
			break;
		}
	}
	logger.log ("current employee " + currentEmployee.name + " is an engineer. An engineer can see delegation options for all employees");
}
