<import resource="classpath:/alfresco/templates/webscripts/ru/it/lecm/search/search.post.json.js">
//пробегаемся по результирующему списку маршрутов и фильтруем их
//если сотрудник технолог, то исходный список не трогаем
//если сотрудник не технолог, то показываем только личные маршруты

var currentEmployee = orgstructure.getCurrentEmployee(),
	engineerBusinessRole = "DA_ENGINEER", isEngineer,
	routeOwnerRef, i, items, itemsLength, isTemp, ownerAssocs;
if (!currentEmployee) {
	logger.log("ERROR: current employee is null!");
	destroyData();
} else {
	isEngineer = orgstructure.isCurrentEmployeeHasBusinessRole(engineerBusinessRole);

	//даже если я инженер надо удалить из списка все маршруты, у которых нет ассоциации на владельца
	items = model.data.items;
	for (i = 0; i < items.length; i++) {
		isTemp = items[i].node.hasAspect("lecm-workflow:temp");
		if (isTemp) {
			items.splice(i, 1);
			model.data.paging.totalRecords--;
			i--;
		} else {
			// удалим из списка чужие маршруты
			ownerAssocs = items[i].node.assocs["lecm-workflow:workflow-assignees-list-owner-assoc"];
			routeOwnerRef = ownerAssocs[0].nodeRef;
			if (!isEngineer && !routeOwnerRef.equals(currentEmployee.nodeRef)) {
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
