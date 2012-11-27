<import resource="classpath:/alfresco/templates/webscripts/ru/it/lecm/orgstructure/api/orgstructure.lib.js">

function main () {
    var employee = search.findNode(args["nodeRef"]);

    // получаем основную должностную позицию
    var primaryStaff = null;
    var links = employee.sourceAssocs["lecm-orgstr:employee-link-employee-assoc"];
    for (var index in links){
        var employeeLink = links[index];
        if (employeeLink.properties["lecm-orgstr:employee-link-is-primary"]) {// основная связь
            // получаем  объект, ссылающийся на данную связь
            primaryStaff = employeeLink.getParent();
            break;
        }
    }

// надо получить подразделение для штатного расписания
    var unit = primaryStaff.getParent();
// получаем руководителя для подразделения
    var boss = findBoss(unit);
    model.boss = boss;
}

main();

