var documentNodeRef = args['documentNodeRef'];
var tableDataType = args['tableDataType'];
var tableDataAssocType = args['tableDataAssocType'];

model.rows = documentTables.getTableTotalRow(documentNodeRef, tableDataType, tableDataAssocType);