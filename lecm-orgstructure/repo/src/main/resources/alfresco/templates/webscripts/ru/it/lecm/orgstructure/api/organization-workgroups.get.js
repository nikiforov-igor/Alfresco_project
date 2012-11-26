var results = [];

var organization = companyhome.childByNamePath("Организация");
var structure = organization.childByNamePath("Структура");

var workgroups = structure.getChildAssocsByType("lecm-orgstr:workGroup");
var active = args["onlyActive"];

for (var x in workgroups){
    var wg = workgroups[x];
    if (!active) { // включать все
        results.push(wg);
    } else {
        if(wg.properties["lecm-dic:active"]) {
            results.push(wg);
        }
    }
}

model.workgroups = results;