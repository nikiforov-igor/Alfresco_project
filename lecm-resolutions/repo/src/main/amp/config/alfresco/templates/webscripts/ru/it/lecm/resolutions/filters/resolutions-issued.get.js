var dashletSettings = resolutionsScript.getDashletSettings();

function addToList(list, key) {
    var resolutionsList = resolutionsScript.getIssuedResolutions(key);

    var allCount = resolutionsList.length;
    var controlCount = getControlCount(resolutionsList);

    var armCode = dashletSettings.properties["lecm-resolutions-settings:dashlet-settings-arm"];
    var path = "";
    var controlPath = "";
    if (key === "issued_resolutions_on_approval") {
        path = dashletSettings.properties["lecm-resolutions-settings:dashlet-settings-on-approval"];
        controlPath = dashletSettings.properties["lecm-resolutions-settings:dashlet-settings-on-approval-control"];
    } else if (key === "issued_resolutions_on_completion") {
        path = dashletSettings.properties["lecm-resolutions-settings:dashlet-settings-on-completion"];
        controlPath = dashletSettings.properties["lecm-resolutions-settings:dashlet-settings-on-completion-control"];
    } else if (key === "issued_resolutions_on_execution") {
        path = dashletSettings.properties["lecm-resolutions-settings:dashlet-settings-on-execution"];
        controlPath = dashletSettings.properties["lecm-resolutions-settings:dashlet-settings-on-execution-control"];
    } else if (key === "issued_resolutions_on_solution") {
        path = dashletSettings.properties["lecm-resolutions-settings:dashlet-settings-required-solution"];
        controlPath = dashletSettings.properties["lecm-resolutions-settings:dashlet-settings-required-solution-control"];
    } else if (key === "issued_resolutions_expired") {
        path = dashletSettings.properties["lecm-resolutions-settings:dashlet-settings-expired"];
        controlPath = dashletSettings.properties["lecm-resolutions-settings:dashlet-settings-expired-control"];
    } else if (key === "issued_resolutions_deadline") {
        path = dashletSettings.properties["lecm-resolutions-settings:dashlet-settings-approaching-deadline"];
        controlPath = dashletSettings.properties["lecm-resolutions-settings:dashlet-settings-approaching-deadline-control"];
    }
    list.push({
        key: key,
        allCount: allCount,
        controlCount: controlCount,
        path: path,
        controlPath: controlPath,
        armCode: armCode
    });
}

function getControlCount(list) {
    var count = 0;
    for (var index in list) {
        var resolutionNode = list[index];
        if (resolutionNode.properties["lecm-document-aspects:is-on-control"]) {
            count++;
        }
    }
    return count;
}

function main() {
    var list = [];

    addToList(list, "issued_resolutions_on_approval");
    addToList(list, "issued_resolutions_on_completion");
    addToList(list, "issued_resolutions_on_execution");
    addToList(list, "issued_resolutions_on_solution");
    addToList(list, "issued_resolutions_expired");
    addToList(list, "issued_resolutions_deadline");

    model.items = list;
}

main();