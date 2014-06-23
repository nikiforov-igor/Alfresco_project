var nodeRef = args["nodeRef"];
var nodes = dictionary.getRecordByParamValue("Контрагенты", "lecm-contractor:INN", args["number"]);

var result = [];

for each (var node in nodes) {
    if (node.nodeRef.toString() != nodeRef) {
        result.push(node.properties["lecm-contractor:shortname"]);
    }
}
model.result = jsonUtils.toJSONString(result);
