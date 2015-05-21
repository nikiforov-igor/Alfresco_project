<import resource="classpath:/alfresco/templates/webscripts/ru/it/lecm/pickerchildren.lib.js">

function main()
{
	var data = null;

	var meetingHoldingItemNodeRef = args['itemId'];

	if (meetingHoldingItemNodeRef != null) {
		var meetingHoldingItem = search.findNode(meetingHoldingItemNodeRef);
		 if (meetingHoldingItem != null) {
			 var meeting = documentTables.getDocumentByTableDataRow(meetingHoldingItem);
			 if (meeting != null) {
				 var technicalMembers = meetings.getHoldingTechnicalMembers(meeting);
				 if (technicalMembers != null) {
					 var filter = getFilterForAvailableElement(technicalMembers);
					 data = getPickerChildrenItems(filter);
				 }
			 }
		 }
	}
	if (data == null) {
		data = getPickerChildrenItems();
	}


	model.parent = data.parent;
	model.rootNode = data.rootNode;
	model.results = data.results;
	model.additionalProperties = data.additionalProperties;
}

main();
