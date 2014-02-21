var actions = groupActions.getActiveGroupActions(json.get("items"));

var result = [];

for each (var action in actions) {
    result.push({
        id: action.properties["cm:name"],
        withForm: action.children.length > 0
    });
}


model.result = jsonUtils.toJSONString(result);