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

model.result = jsonUtils.toJSONString(result);