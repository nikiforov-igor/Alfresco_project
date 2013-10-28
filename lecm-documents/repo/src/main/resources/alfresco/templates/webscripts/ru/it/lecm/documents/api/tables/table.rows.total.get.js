var documentNodeRef = args['documentNodeRef'];
var tableDataType = args['tableDataType'];
var tableDataAssocType = args['tableDataAssocType'];

var items = null;

var rows = documentTables.getTableTotalRow(documentNodeRef, tableDataType, tableDataAssocType);
if (rows != null) {
	items = [];
	for (var i = 0; i < rows.length; i++) {
		items.push({
			row: rows[i],
			properties: rows[i].getTypePropertyNames(true)
		});
	}
}

model.items = items;