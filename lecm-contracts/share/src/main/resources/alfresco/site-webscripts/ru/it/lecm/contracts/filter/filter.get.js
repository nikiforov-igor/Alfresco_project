function main() {
    var type = "lecm-contract:document";
    var showStatuses = args["showStatusesBlock"] ? args["showStatusesBlock"] == "true" : false;
    model.statusesGroups = getFilters(type);
    model.statusesList = getStatuses(type);
}

function getStatuses(type) {
    var statuses = [];

    var url = '/lecm/contracts/getStatuses?docType=' + type;
    var result = remote.connect("alfresco").get(url);

    if (result.status == 200) {
        var statusesList = eval('(' + result + ')');
        for each(var status in statusesList) {
            statuses.push(status.id);
        }
    }

    return statuses
}

function getFilters(type) {
    var filters = [];

    var url = '/lecm/contracts/summary';
    var result = remote.connect("alfresco").get(url);

    if (result.status == 200) {
        var json = jsonUtils.toObject(result);
        var filtersList = json.list.toArray();
        for (var index in filtersList) {
            name = filtersList[index].key;
            value = filtersList[index].filter;
            count = filtersList[index].amountContracts;
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
