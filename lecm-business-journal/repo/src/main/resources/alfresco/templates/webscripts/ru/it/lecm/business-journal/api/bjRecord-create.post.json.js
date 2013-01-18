if (typeof json !== "undefined") {
    var initiator = json.get("initiator");
    var mainObject = json.get("mainObject");
    var category = json.has("category") ? json.get("category") : null;
    var description = json.has("description") ? json.get("description") : null;
    var objects = [];
    if (json.has("objects")) {
        var jsonFields = json.get("objects"),
            numFields = jsonFields.length();

        for (count = 0; count < numFields; count++) {
            objects.push(jsonFields.get(count));
        }
    }
    model.record = businessJournal.fire(initiator, mainObject, category, description, objects);
}