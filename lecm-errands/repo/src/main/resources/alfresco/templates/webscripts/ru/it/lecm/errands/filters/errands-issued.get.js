function addToList(list, key) {
    var errandsList = errands.getIssuedErrands(key);

    var allCount = errandsList.length;
    var importantCount = getImportantCount(errandsList);

    list.push({
        key: key,
        allCount: allCount,
        importantCount: importantCount
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

    addToList(list, "ALL");
    addToList(list, "EXECUTION");
    addToList(list, "EXPIRED");
    addToList(list, "DEADLINE");

    model.items = list;
}

main();