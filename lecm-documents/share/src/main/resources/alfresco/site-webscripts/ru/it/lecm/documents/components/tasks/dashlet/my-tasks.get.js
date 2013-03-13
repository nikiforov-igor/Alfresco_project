function main() {
    var url = "/lecm/statemachine/api/tasks/active?nodeRef=" + args["nodeRef"];
    var json = remote.connect("alfresco").get(url);
    if (json.status == 200) {
        var obj = eval("(" + json + ")");
        model.data = obj;
    }
}

main();