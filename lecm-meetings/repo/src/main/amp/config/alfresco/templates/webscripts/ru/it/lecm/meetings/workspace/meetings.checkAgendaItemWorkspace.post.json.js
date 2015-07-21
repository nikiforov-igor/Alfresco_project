function main() {
	var agendaItem = json.get("agendaItem");
	var newWorkspace = json.get("newWorkspace");
	
	model.siteShortName = meetings.editAgendaItemWorkspace(agendaItem, newWorkspace);
}

main();