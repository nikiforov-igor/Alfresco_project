function main() {
    model.nodeRef = args["nodeRef"];
    //
    model.canCreate = false;
    var url = "/lecm/statemachine/statefields?documentNodeRef=" + model.nodeRef;
    var result = remote.connect("alfresco").get(url);

    if (result.status == 200) {
        var fields = eval('(' + result + ')');
        for each (var field in fields.fields) {
            if (field.name == "lecm-contract-table-structure:stages-assoc") {
                model.canCreate = field.editable;
            }
        }
    }
}

main();