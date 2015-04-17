var items = json.get("items");
var group = json.has("group") ? json.get("group") == true : true;

var actions = groupActions.getActiveGroupActions(items, group);

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