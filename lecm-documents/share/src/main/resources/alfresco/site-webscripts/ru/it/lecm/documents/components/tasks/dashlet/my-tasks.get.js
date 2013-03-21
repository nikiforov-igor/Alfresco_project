function main() {
    var url = "/lecm/statemachine/api/tasks?nodeRef=" + args["nodeRef"] + "&state=active";
    var json = remote.connect("alfresco").get(url);
    if (json.status == 200) {
        var obj = eval("(" + json + ")");
        model.data = obj;
    }
}

main();