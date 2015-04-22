<import resource="classpath:/alfresco/templates/webscripts/ru/it/lecm/pickeritems.lib.js">

function main()
{
	var items = getPickerItems();

	if (json.has("eventNodeRef")) {
		for (var i = 0; i < items.results.length; i++) {
			var tableRow = events.getEmployeeEventMemberRow(json.get("eventNodeRef"), items.results[i].item.nodeRef);
			if (tableRow != null) {
				items.results[i].memberStatus = tableRow.properties["lecm-events-ts:members-status"];
				items.results[i].memberMandatory = tableRow.properties["lecm-events-ts:members-participation-required"];
			}
		}
	}

	model.results = items.results;
	model.additionalProperties = items.additionalProperties;
}

main();
