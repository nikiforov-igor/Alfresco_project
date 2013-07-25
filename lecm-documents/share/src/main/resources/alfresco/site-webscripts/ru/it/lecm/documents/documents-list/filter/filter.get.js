function main() {
    var type = args["itemType"] ? args["itemType"] : null;
    var queryFilter = args["queryFilter"] ? args["queryFilter"] : null;
    model.docType = type;

    if (type != null) {
        model.statusesGroups = getFilters(type, queryFilter);
        model.statusesList = getStatuses(type);
    }
}

function getStatuses(type) {
    var statuses = [];

    var url = '/lecm/statemachine/getStatuses?docType=' + type;
    var result = remote.connect("alfresco").get(url);

    if (result.status == 200) {
        var statusesList = eval('(' + result + ')');
        for each(var status in statusesList) {
            statuses.push(status.id);
        }
    }

    return statuses
}

function getFilters(type, filter) {
    var filters = [];

    var url = '/lecm/documents/summary?docType=' + type + (filter ? "&considerFilter=" + filter : "");
    var result = remote.connect("alfresco").get(url);

    if (result.status == 200) {
        var json = jsonUtils.toObject(result);
        var filtersList = json.list.toArray();
        for (var index in filtersList) {
            name = filtersList[index].key;
            value = filtersList[index].filter;
            count = filtersList[index].amount;
            filters.push({
                name: name,
                value: value,
                count:count
            });
        }
    }

    return filters;
}


main();
