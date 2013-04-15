function main() {
    model.statusesGroups = [
        {"title": "Все", "value": "-"},
        {"title": "В разработке", "value": "Регистрация,Согласование"},
        {"title": "Активные", "value": "Рассмотрение"},
        {"title": "Неактивные", "value": "Черновик"}
    ];
    model.statusesList = getStatuses();
}

function getStatuses() {
    var statuses = [];

    var url = '/lecm/contracts/getStatuses';
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
