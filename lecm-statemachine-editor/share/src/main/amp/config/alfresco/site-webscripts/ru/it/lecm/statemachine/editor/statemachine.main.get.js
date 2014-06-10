var id = args["statemachineId"];

if (id == '') {
	var url = "/lecm/statemachine/editor/list";
	var json = remote.connect("alfresco").get(url);
	if (json.status == 200) {
		model.machines = eval("(" + json + ")");
	}
} else {
	var url = "/lecm/statemachine/editor/title?statemachineId=" + id;
	var json = remote.connect("alfresco").get(encodeURI(url));
	if (json.status == 200) {
		model.title = json;
	}
}