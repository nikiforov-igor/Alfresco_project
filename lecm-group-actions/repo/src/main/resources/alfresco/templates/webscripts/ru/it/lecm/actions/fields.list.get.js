var home = groupActions.getHomeRef();

var actionNode = home.childByNamePath(args["actionId"]);
var result = [];

for each (var action in actionNode.children) {
    result.push({
        name: action.properties["cm:name"],
        id: action.properties["lecm-group-actions:field-id"],
        type: action.properties["lecm-group-actions:field-type"],
        order: action.properties["lecm-group-actions:field-priority"]
    });
}

result.sort(function(a, b) {return a.order - b.order});

model.result = jsonUtils.toJSONString(result);