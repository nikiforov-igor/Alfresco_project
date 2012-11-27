var employee = search.findNode(args["nodeRef"]);

var results = [];

// получаем набор ссылок на текущего сотрудника
var links = employee.sourceAssocs["lecm-orgstr:employee-link-employee-assoc"];
for (var index in links){
    var employeeLink = links[index];
    var position = employeeLink.getParent();
    if (position.getTypeShort() == "lecm-orgstr:staff-list") {// выбираем только Штатные расписания
        results.push(position);
    }
}

model.staffs = results;