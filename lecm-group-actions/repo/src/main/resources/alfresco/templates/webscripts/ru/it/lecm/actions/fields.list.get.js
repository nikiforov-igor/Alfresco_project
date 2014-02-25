var home = groupActions.getHomeRef();

var actionNode = home.childByNamePath(args["actionId"]);
var result = [];

for each (var action in actionNode.children) {
    result.push({
        name: action.properties["cm:name"],
        id: action.properties["lecm-group-actions:field-id"],
        type: action.properties["lecm-group-actions:field-type"]
    });
}


model.result = jsonUtils.toJSONString(result);