var results = [];

var active = args["onlyActive"];

var rootUnits = orgstructure.getRootUnits(active != null && active == "true");

for (var index in rootUnits){
    var root = rootUnits[index];
    results.push(getUnits(root, active));
}

model.units = results;

function getUnits(node, active) {
    var subunits = [];
    for each (var child in orgstructure.getSubUnits(node.getNodeRef().toString(), active != null && active == "true")) {
        subunits.push(getUnits(child,active));
    }
    return {
        shortName:node.properties["lecm-orgstr:element-short-name"],
        fullName: node.properties["lecm-orgstr:element-full-name"],
        code: node.properties["lecm-orgstr:unit-code"],
        type: node.properties["lecm-orgstr:unit-type"],
        active: node.properties["lecm-dic:active"],
        nodeRef: node.getNodeRef().toString(),
        subUnits:subunits
    };
}
