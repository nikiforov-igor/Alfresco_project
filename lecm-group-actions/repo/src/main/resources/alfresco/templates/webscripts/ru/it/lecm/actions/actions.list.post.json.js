var actions = groupActions.getActiveGroupActions(json.get("items"));

var result = [];

for each (var action in actions) {
    result.push(action.properties["cm:name"]);
}


model.result = jsonUtils.toJSONString(result);