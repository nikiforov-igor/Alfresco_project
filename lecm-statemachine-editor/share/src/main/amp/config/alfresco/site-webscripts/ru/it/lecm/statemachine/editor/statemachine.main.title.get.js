var id = args["statemachineId"];

if (id != null && id != "") {
    var url = "/lecm/statemachine/editor/title?statemachineId=" + id;
    var json = remote.connect("alfresco").get(encodeURI(url));
    if (json.status == 200) {
        model.title = json;
    }
}
