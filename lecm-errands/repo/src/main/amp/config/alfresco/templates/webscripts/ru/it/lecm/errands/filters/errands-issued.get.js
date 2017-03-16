var dashletSettings = errands.getDashletSettings();

function addToList(list, key) {
    var errandsList = errands.getIssuedErrands(key);

    var allCount = errandsList.length;
    var importantCount = getImportantCount(errandsList);

    var armCode = dashletSettings.properties["lecm-errands:dashlet-settings-arm"];
    var path = "";
    var importantPath = "";
    if (key === "issued_errands_all") {
        path = dashletSettings.properties["lecm-errands:dashlet-settings-unexecuted"];
        importantPath = dashletSettings.properties["lecm-errands:dashlet-settings-unexecuted-important"];
    } else if (key === "issued_errands_execution") {
        path = dashletSettings.properties["lecm-errands:dashlet-settings-await-execution"];
        importantPath = dashletSettings.properties["lecm-errands:dashlet-settings-await-execution-important"];
    } else if (key === "issued_errands_on_execution") {
        path = dashletSettings.properties["lecm-errands:dashlet-settings-on-execution"];
        importantPath = dashletSettings.properties["lecm-errands:dashlet-settings-on-execution-important"];
    } else if (key === "issued_errands_on_check_report") {
        path = dashletSettings.properties["lecm-errands:dashlet-settings-on-check-report"];
        importantPath = dashletSettings.properties["lecm-errands:dashlet-settings-on-check-report-important"];
    } else if (key === "issued_errands_on_completion") {
        path = dashletSettings.properties["lecm-errands:dashlet-settings-on-completion"];
        importantPath = dashletSettings.properties["lecm-errands:dashlet-settings-on-completion-important"];
    } else if (key === "issued_errands_expired") {
        path = dashletSettings.properties["lecm-errands:dashlet-settings-expired"];
        importantPath = dashletSettings.properties["lecm-errands:dashlet-settings-expired-important"];
    } else if (key === "issued_errands_deadline") {
        path = dashletSettings.properties["lecm-errands:dashlet-settings-approaching-deadline"];
        importantPath = dashletSettings.properties["lecm-errands:dashlet-settings-approaching-deadline-important"];
    }
    list.push({
        key: key,
        allCount: allCount,
        importantCount: importantCount,
        path: path,
        importantPath: importantPath,
        armCode: armCode
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
    addToList(list, "issued_errands_on_execution");
    addToList(list, "issued_errands_on_check_report");
    addToList(list, "issued_errands_on_completion");
    addToList(list, "issued_errands_expired");
    addToList(list, "issued_errands_deadline");

    model.items = list;
}

main();