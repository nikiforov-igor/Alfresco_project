function main() {
    var type = args["itemType"] ? args["itemType"] : null;
    model.docType = type;

    var queryFilter = args["queryFilter"] ? args["queryFilter"] : null; // из настроек в шаблоне страницы
    var filterOver = page.url.args.filterOver; // если параметр пришел - значит, перегрузим прописанный в настройках фильтр

    if (type != null) {
        model.statusesGroups = getArchiveFilters(type, (filterOver != null && filterOver.length > 0 )? filterOver : queryFilter);
        model.statusesList = getArchiveStatuses(type);
    }
}

function getArchiveStatuses(type) {
    var statuses = [];

    var url = '/lecm/statemachine/getStatuses?docType=' + type + "&active=false&final=true";
    var result = remote.connect("alfresco").get(url);

    if (result.status == 200) {
        var statusesList = eval('(' + result + ')');
        for each(var status in statusesList) {
            statuses.push(status.id);
        }
    }

    return statuses
}

function getArchiveFilters(type, filter) {
    var filters = [];

    var url = '/lecm/documents/summary?docType=' + type + "&archive=true" + (filter ? "&considerFilter=" + filter : "");
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
