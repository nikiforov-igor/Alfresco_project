if (typeof json !== "undefined") {
    var document = json.get("document");
    var employee = json.get("employee");
    var params = json.has("params") ? json.get("params") : "";
    try {
        var newMember = documentMembers.add(document, employee, params);
        model.member = newMember;
    } catch (e) {

    }
}