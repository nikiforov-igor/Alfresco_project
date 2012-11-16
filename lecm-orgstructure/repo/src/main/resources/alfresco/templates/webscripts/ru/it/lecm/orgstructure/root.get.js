var organization = companyhome.childByNamePath("Организация");

var root = "";

if (args["type"] == null || args["type"] == "") {
    root = "";
} else {
    var t = args["type"];
    if (t == "structure") {
        var structure = organization.getChildAssocsByType("lecm-orgstr:org-structure-assoc")[0];
        root = structure.nodeRef.toString();
    } else if (t == "employees") {
        var employeers = organization.childByNamePath("Сотрудники");
        root = employeers.nodeRef.toString();
    } else if (t == "personal_data") {
        var personal_data = organization.childByNamePath("Персональные данные");
        root = personal_data.nodeRef.toString();
    }
}

model.root = root;
