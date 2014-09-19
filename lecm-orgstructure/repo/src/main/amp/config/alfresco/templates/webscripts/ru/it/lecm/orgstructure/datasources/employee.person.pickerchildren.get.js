<import resource="classpath:/alfresco/templates/webscripts/ru/it/lecm/pickerchildren.lib.js">

function main() {
    var data = getPickerChildrenItems();

    var results = [];

    for each (var item in data.results) {
        if (item.item.sourceAssocs["lecm-orgstr:employee-person-assoc"] == null) {
            results.push(item);
        }
    }

    model.parent = data.parent;
    model.rootNode = data.rootNode;
    model.results = results;
    model.additionalProperties = data.additionalProperties;

}

main();