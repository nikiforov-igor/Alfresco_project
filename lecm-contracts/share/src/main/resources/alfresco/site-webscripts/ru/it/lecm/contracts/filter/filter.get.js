function main() {
    var type = args["type"];
    var showStatuses = args["showStatusesBlock"] ? args["showStatusesBlock"] == "true" : false;
    model.statusesGroups = [
        {"title": "Все", "value": "-"},
        {"title": "В разработке", "value": "Регистрация,Согласование"},
        {"title": "Активные", "value": "Рассмотрение"},
        {"title": "Неактивные", "value": "Черновик"}
    ];
    if (showStatuses) {
        model.statusesList = getStatuses(type);
    }
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


main();
