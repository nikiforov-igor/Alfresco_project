var results = [];

var organization = companyhome.childByNamePath("Организация");
var structure = organization.childByNamePath("Структура");

var rootUnits = structure.getChildAssocsByType("lecm-orgstr:organization-unit");

var active = args["onlyActive"];

for (var index in rootUnits){
    var root = rootUnits[index];
    if (!active) { // включать все
        results.push(getUnits(root, active));
    } else {
        if(root.properties["lecm-dic:active"]) {
            results.push(getUnits(root, active));
        }
    }
    results.push();
}

model.units = results;

function getUnits(node, active) {
    var subunits = [];
    for each (var child in node.getChildAssocsByType("lecm-orgstr:organization-unit")) {
        if (!active) { // включать все
            subunits.push(getUnits(child, active));
        } else {
            if(root.properties["lecm-dic:active"]) {
                subunits.push(getUnits(child,active));
            }
        }
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
