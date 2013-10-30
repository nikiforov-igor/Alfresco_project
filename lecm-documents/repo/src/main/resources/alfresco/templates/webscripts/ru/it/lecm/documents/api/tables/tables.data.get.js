var tableData = search.findNode(args['nodeRef']);
model.node = tableData;
model.rows = tableData.associations["lecm-document:tableDataRows-assoc"];
model.totalRows = tableData.associations["lecm-document:tableDataTotalRow-assoc"];