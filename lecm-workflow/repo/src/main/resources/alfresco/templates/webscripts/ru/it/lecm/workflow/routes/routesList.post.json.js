<import resource="classpath:/alfresco/templates/webscripts/ru/it/lecm/search/search.post.json.js">
//пробегаемся по результирующему списку маршрутов и фильтруем их
//если сотрудник технолог, то исходный список не трогаем
//если сотрудник не технолог, то показываем только личные маршруты

var currentEmployee = orgstructure.getCurrentEmployee(),
		engineerBusinessRole = "DA_ENGINEER", isEngineer,
		routeOwnerRef, i, items, itemsLength;
if (!currentEmployee) {
	logger.log("ERROR: current employee is null!");
	destroyData();
} else {
	isEngineer = orgstructure.isCurrentEmployeeHasBusinessRole(engineerBusinessRole);

	if (!isEngineer) {
		// удалим из списка чужие маршруты
		items = model.data.items;
		for (i = 0; i < items.length; i++) {

			routeOwnerRef = items[i].node.assocs["lecm-workflow:route-to-employee-assoc"][0].nodeRef;
			if (!routeOwnerRef.equals(currentEmployee.nodeRef)) {
				items.splice(i, 1);
				model.data.paging.totalRecords--;
				i--;
			}
		}
	}
}

function destroyData() {
	items = model.data.items;
	itemsLength = items.length;
	items.splice(0, itemsLength);
	model.data.paging.totalRecords = 0;
}
