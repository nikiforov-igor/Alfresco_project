<import resource="classpath:/alfresco/templates/webscripts/ru/it/lecm/pickerchildren.lib.js">

function main() {
	var data = getPickerChildrenItems();

	model.parent = data.parent;
	model.rootNode = data.rootNode;
	model.results = actualizeRoutesList(data.results);
	model.additionalProperties = data.additionalProperties;
}

function actualizeRoutesList(routesList) {
	var currentEmployee = orgstructure.getCurrentEmployee(),
		engineerBusinessRole = "DA_ENGINEER", isEngineer, employeeOrgUnit,
		routeOwnerRef, i, isTemp, ownerAssocs, routeNode, result = [];

	if (!currentEmployee) {
		logger.log("ERROR: current employee is null!");
	} else {
		isEngineer = orgstructure.isCurrentEmployeeHasBusinessRole(engineerBusinessRole);
		employeeOrgUnit = orgstructure.getPrimaryOrgUnit(currentEmployee);

		//даже если я инженер надо удалить из списка все маршруты, у которых нет ассоциации на владельца
		for (i = 0; i < routesList.length; i++) {
			routeNode = routesList[i].item;
			isTemp = routeNode.hasAspect("lecm-workflow:temp");
			if (!isTemp) {
				// удалим из списка чужие маршруты
				ownerAssocs = routeNode.assocs["lecm-workflow:workflow-assignees-list-owner-assoc"];
				routeOwnerRef = ownerAssocs[0].nodeRef;
				if (isEngineer || routeOwnerRef.equals(currentEmployee.nodeRef) || routeOwnerRef.equals(employeeOrgUnit.nodeRef)) {
					result.push(routesList[i]);
				}
			}
		}
	}

	return result;
}

main();
