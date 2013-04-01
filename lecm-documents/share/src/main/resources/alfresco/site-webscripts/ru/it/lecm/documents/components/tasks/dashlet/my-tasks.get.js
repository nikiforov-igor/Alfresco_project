<import resource="classpath:/alfresco/site-webscripts/ru/it/lecm/documents/utils/permission-utils.js">

function main() {
    var hasPerm = hasPermission(args["nodeRef"], '_lecmPerm_WFTaskList');
    if (hasPerm) {
        var url = "/lecm/statemachine/api/tasks?nodeRef=" + args["nodeRef"] + "&state=active";
        var json = remote.connect("alfresco").get(url);
        if (json.status == 200) {
            var obj = eval("(" + json + ")");
            model.data = obj;
        }
    }
}

main();