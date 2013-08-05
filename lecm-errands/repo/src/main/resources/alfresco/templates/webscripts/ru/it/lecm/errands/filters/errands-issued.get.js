function addToList(list, key) {
    var errandsList = errands.getIssuedErrands(key);

    var allCount = errandsList.length;
    var importantCount = getImportantCount(errandsList);

    list.push({
        key: key,
        allCount: allCount,
        importantCount: importantCount,
        filter: errands.getIssuedFilter(key),
        importantFilter: errands.getIssuedFilter(key + "_important")
    });
}

function getImportantCount(list) {
    var count = 0;
    for (var index in list) {
        var errandNode = list[index];
        if (errandNode.properties["lecm-errands:is-important"]) {
            count++;
        }
    }
    return count;
}

function main() {
    var list = [];

    addToList(list, "issued_errands_all");
    addToList(list, "issued_errands_execution");
    addToList(list, "issued_errands_expired");
    addToList(list, "issued_errands_deadline");

    model.items = list;
}

main();