var organization = companyhome.childByNamePath("Организация");

var root = "";

if (args["type"] == null || args["type"] == "") {
    root = "";
} else {
    var t = args["type"];
    if (t == "organization-structure") {
        var structure = organization.getChildAssocsByType("lecm-orgstr:org-structure-assoc")[0];
        root = structure.nodeRef.toString();
    } else if (t == "offices") {
        var offices = organization.childByNamePath("Офисы");
        root = offices.nodeRef.toString();
    } else if (t == "employees") {
        var employeers = organization.childByNamePath("Сотрудники");
        root = employeers.nodeRef.toString();
    } else if (t == "personal_data") {
        var personal_data = organization.childByNamePath("Персональные данные");
        root = personal_data.nodeRef.toString();
    } else if (t == "officials") {
        var officials = organization.childByNamePath("Должностные лица");
        root = officials.nodeRef.toString();
    } else if (t == "workforces") {
        var workforces = organization.childByNamePath("Трудовые ресурсы");
        root = workforces.nodeRef.toString();
    } else if (t == "unit-compositions") {
        var composition = organization.childByNamePath("Составы подразделений");
        root = composition.nodeRef.toString();
    } else if (t == "project_register") {
        var projects = organization.getChildAssocsByType("lecm-orgstr:org-projects-assoc")[0];
        root = projects.nodeRef.toString();
    } else if (t == "staff-list"){
        var staffList = organization.getChildAssocsByType("lecm-orgstr:staff-list")[0];
        root = staffList.nodeRef.toString();
    }
}

model.root = root;
