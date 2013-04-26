<import resource="classpath:/alfresco/templates/org/alfresco/import/alfresco-util.js">

var DOCUMENT_TYPES = "document,additionalDocument";   //document type local names

function main() {
    model.myTasks = [];

    var url = "/lecm/statemachine/api/documentsTasks?types=" + DOCUMENT_TYPES;
    var json = remote.connect("alfresco").get(url);
    if (json.status == 200) {
        model.myTasks = eval("(" + json + ")");
    }
}

main();