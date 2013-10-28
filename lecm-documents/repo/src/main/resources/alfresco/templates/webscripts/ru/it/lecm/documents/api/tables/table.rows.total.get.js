var documentNodeRef = args['documentNodeRef'];
var tableDataType = args['tableDataType'];
var tableDataAssocType = args['tableDataAssocType'];

var items = [];

var rows = documentTables.getTableTotalRow(documentNodeRef, tableDataType, tableDataAssocType);
for (var i = 0; i < rows.length; i++) {
	items.push({
		row: rows[i],
		properties: rows[i].getTypePropertyNames(true)
	});
}


model.items = items;