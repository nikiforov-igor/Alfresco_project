function main()
{
	var nodeRef = args['nodeRef'];

	if (nodeRef != null) {
		var data = meetings.getAgendaInfo(nodeRef);
		if (data != null) {
			model.data=data;
		}
	}
	
	
}

main();
