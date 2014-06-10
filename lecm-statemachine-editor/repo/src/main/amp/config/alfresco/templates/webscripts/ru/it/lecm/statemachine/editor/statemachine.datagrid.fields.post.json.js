var params = {};
if (typeof json !== "undefined") {
    var nodeRef = json.get("nodeRef");
    var stateMachine = search.findNode(nodeRef);

    var requestedFields = json.getJSONArray("fields");
    var responseFields = [];

    var children = stateMachine.childByNamePath("statuses").getChildren();
    for each (var status in children) {
        var statusName = status.properties["cm:name"]
        var fields = status.childByNamePath("fields");
        if (fields == null) {
            fields = status.createNode("fields", "cm:folder", "cm:contains");
        }

        for (var i = 0; i < requestedFields.length(); i++) {
            var field =  requestedFields.get(i);
            var fieldNode = fields.childByNamePath(field.replace(":", "_"));
            if (fieldNode == null) {
                fieldNode = fields.createNode(field.replace(":", "_"), "lecm-stmeditor:documentField", "cm:contains");
                fieldNode.properties["lecm-stmeditor:editableField"] = false;
                fieldNode.save();
            }
            if (responseFields[field] == null) {
                responseFields[field] = [];
            }
            responseFields[field][statusName] = {
                fieldNodeRef: fieldNode.nodeRef.toString(),
                editableField: fieldNode.properties["lecm-stmeditor:editableField"]
            };
        }
    }

    var result = [];
    for (var fieldName in responseFields) {
        var statuses = [];
        for (var statusName in responseFields[fieldName]) {
            statuses.push({
                status: statusName,
                fieldNodeRef: responseFields[fieldName][statusName].fieldNodeRef,
                editableField: responseFields[fieldName][statusName].editableField
            });
        }
        result.push({
            field: fieldName,
            statuses: statuses
        })
    }

    model.result = jsonUtils.toJSONString(result);
}