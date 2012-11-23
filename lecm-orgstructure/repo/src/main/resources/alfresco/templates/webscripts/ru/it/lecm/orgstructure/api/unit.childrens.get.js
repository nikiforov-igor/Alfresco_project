var results = [];

var node = search.findNode(args["nodeRef"]);
var active = args["onlyActive"];

var subUnits = node.getChildAssocsByType("lecm-orgstr:organization-unit");

for (var x in subUnits){
    var sub = subUnits[x];
     if (!active) { // включать все
         results.push(sub);
     } else {
        if(sub.properties["lecm-dic:active"]) {
            results.push(sub);
        }
     }
}
model.subUnits = results;