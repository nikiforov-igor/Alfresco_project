<import resource="classpath:/alfresco/templates/webscripts/ru/it/lecm/search/search.post.json.js">

var currentEmployee = orgstructure.getCurrentEmployee(),
	engineerBusinessRole = "DA_ENGINEER", archivistBusinessRole = "DA_ARCHIVISTS", isEngineer,
	i, items, itemsLength;

if (!currentEmployee) {
	logger.log("ERROR: current employee is null!");
	destroyData();
} else {

	items = model.data.items;

}

function destroyData() {
	items = model.data.items;
	itemsLength = items.length;
	items.splice(0, itemsLength);
	model.data.paging.totalRecords = 0;
}
