if (typeof json !== "undefined") {
    var document = json.get("document");
    var employee = json.get("employee");
    var permGroup = json.has("permGroup") ? json.get("permGroup") : "";
    try {
        var newMember = documentMembers.addMember(document, employee, permGroup);
        model.member = newMember;
    } catch (e) {

    }
}