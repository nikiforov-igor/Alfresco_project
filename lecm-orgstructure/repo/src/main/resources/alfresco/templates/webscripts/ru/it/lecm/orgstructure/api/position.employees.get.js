var results = [];
var position = search.findNode(args["nodeRef"]);
// получаем список объектов Штатное расписание для заданной позиции
var staffLists = position.sourceAssocs["lecm-orgstr:element-member-position-assoc"];
for (var index in staffLists){
    var employeeLink = staffLists[index].getChildAssocsByType("lecm-orgstr:employee-link");
    if (employeeLink && employeeLink[0]){
        // если сотрудник задан
        results.push(employeeLink[0].assocs["lecm-orgstr:employee-link-employee-assoc"][0]);// сотрудник всегда существует и только один
    }
}

model.employees = results;
