var result = [];

var node = search.findNode(args["nodeRef"]);
var table = node.assocs["lecm-contract-table-structure:stages-assoc"][0]
var data = documentTables.getTableDataRows(table.nodeRef.toString());

for each (var row in data) {
    var status = row.properties["lecm-contract-table-structure:stage-status"];
    if (status == null || status != "Закрыт") {
        result.push({
            nodeRef: row.nodeRef.toString(),
            title: row.properties["lecm-contract-table-structure:name"],
            number: row.properties["lecm-document:indexTableRow"]
        });
    }
}

result.sort(function(a, b)
{
	return (a.number < b.number) ? -1 : (a.number > b.number) ? 1 : 0;
});

model.result = jsonUtils.toJSONString(result);