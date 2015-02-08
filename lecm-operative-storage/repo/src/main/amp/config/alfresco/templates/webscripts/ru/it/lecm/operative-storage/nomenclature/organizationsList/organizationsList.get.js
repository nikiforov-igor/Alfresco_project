var rootUnit = orgstructure.getRootUnit();
var units = rootUnit.children;
model.results = [];

for each (var unit in units) {
	if(unit.typeShort == "lecm-orgstr:organization-unit") {
		var item = {};
		item.name = unit.properties['lecm-orgstr:element-short-name'];
		item.value = unit.nodeRef.toString();
		model.results.push(item);
	}
}