var DEFAULT_PATH = "/Архив/#{doc.typeTitle()}/#{#formatCurrentDate('yyyy/MM/dd')}";
var DEFAULT_SIMPLE_PATH = "/#{#formatCurrentDate('yyyy/MM/dd')}";

var statemachineNode = search.findNode(args["id"]);
if (statemachineNode) {
    var simple = false;

    if (args["simple"] != null) {
        simple = (("" + args["simple"]) == "true");
    } else {
        simple = statemachineNode.properties["lecm-stmeditor:simple-document"];
        if (simple == null || simple == undefined) {
            simple = false;
        }
    }

    if (!simple) {
        model.value = DEFAULT_PATH;
    } else {
        var modelName = documentScript.getDocumentTypeLabel(statemachineNode.name.replace("_",":"));
        model.value = "/" + modelName + DEFAULT_SIMPLE_PATH;
    }
} else {
    model.value = "";
}
