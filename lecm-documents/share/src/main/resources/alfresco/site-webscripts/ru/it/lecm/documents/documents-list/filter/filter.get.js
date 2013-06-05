function main() {
    var type = args["itemType"] ? args["itemType"] : null;
    if (type != null) {
        var draftRoot = args["draftRoot"];
        model.statusesGroups = getFilters(type, draftRoot);
        model.statusesList = getStatuses(type);
        model.filterLabel = args["filterLabel"];
        model.linkPage = args["linkPage"];
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

function getFilters(type, draftRootName) {
    var filters = [];

    var url = '/lecm/documents/summary?docType=' + type + '&draftRoot=' + draftRootName;
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
