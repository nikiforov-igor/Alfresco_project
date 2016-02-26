function main() {
    var node = search.findNode(args["nodeRef"]);
	
	var parentStaticNode = null;
    if (args["parentStaticNode"] && args["parentStaticNode"].indexOf("/") > -1) {
        parentStaticNode = search.findNode(args["parentStaticNode"]);
    }

    if (node) {
        var columnsArray = [];

		var allColumns = parentStaticNode ? arm.getNodeColumns(parentStaticNode) : arm.getNodeColumns(node);

        var userColumnsIds = [];
        if (args["columns"] != null) {
            userColumnsIds = args["columns"].split(";");
        }

        for (var index in userColumnsIds) {
            var value = userColumnsIds[index];
            var colIndex = findInArray(value, allColumns);
            if (colIndex >= 0) {
                var column = allColumns[colIndex];
                var columnObj = {
                    id: column.getNodeRef().toString(),
                    name: column.properties["lecm-arm:field-title"] != null ? column.properties["lecm-arm:field-title"]  : "-",
                    isDefault:column.properties["lecm-arm:field-by-default"] != null ? column.properties["lecm-arm:field-by-default"] : false,
                    fieldName: column.properties["lecm-arm:field-name"] != null ? column.properties["lecm-arm:field-name"] : "-",
                    checked: true
                };
                columnsArray.push(columnObj);

                allColumns.splice(colIndex, 1);
            }
        }

        for (var index in allColumns) {
            var column = allColumns[index];
            // подменяем фильтр на сохраненный по коду
            var columnObj = {
                id: column.getNodeRef().toString(),
                name: column.properties["lecm-arm:field-title"] != null ? column.properties["lecm-arm:field-title"]  : "-",
                isDefault:column.properties["lecm-arm:field-by-default"] != null ? column.properties["lecm-arm:field-by-default"] : false,
                fieldName: column.properties["lecm-arm:field-name"] != null ? column.properties["lecm-arm:field-name"] : "-",
                checked: false
            };
            columnsArray.push(columnObj);
        }

        model.columns = columnsArray;
    } else {
        status.code = 404;
        status.message = "NodeRef " + args["nodeRef"] + " not found. It maybe deleted or never existed";
        status.redirect = true;
    }
}

main();

function findInArray(value, testArray) {
    if (value == null) {
        return false;
    }
    for (var i = 0; i < testArray.length; i++) {
        var testValue = testArray[i];
        if (value == testValue.getNodeRef().toString()) {
            return i;
        }
    }
    return -1;
}