var actions = groupActions.getActiveGroupActions(json.get("items"));

var result = [];

for each (var action in actions) {
    result.push({
        id: action.properties["cm:name"],
        wide: "".equals(action.properties["lecm-group-actions:type"]),
        type: action.getTypeShort(),
        withForm: action.children.length > 0,
        workflowId: action.properties["lecm-group-actions:workflow"]
    });
}


model.result = jsonUtils.toJSONString(result);