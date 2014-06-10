var tableDataRef = args['tableDataRef'];

var items = null;

var rows = documentTables.getTableTotalRow(tableDataRef);
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