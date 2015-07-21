function main() {
	var agendaItem = json.get("agendaItem");
	var newWorkspace = json.get("newWorkspace");
	
	meetings.editAgendaItemWorkspace(agendaItem, newWorkspace);
}

main();