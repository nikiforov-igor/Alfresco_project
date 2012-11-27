var employee = search.findNode("workspace://SpacesStore/041663d9-9ad7-435e-8e46-52eabf30e060");

var results = [];

// получаем набор ссылок на текущего сотрудника
var links = employee.sourceAssocs["lecm-orgstr:employee-link-employee-assoc"];
for (var index in links){
    var employeeLink = links[index];
    // получаем список объектов, ссылающихся на данную связь (Штатные расписания и Участники Рабочих групп)
    var positions = employeeLink.sourceAssocs["lecm-orgstr:element-member-employee-assoc"];
    for (var i in positions){
        var position = positions[i];
        if (position.getTypeShort() == "lecm-orgstr:staff-list") {// выбираем только Штатные расписания
            results.push(position);
        }
    }
}

model.staffs = results;