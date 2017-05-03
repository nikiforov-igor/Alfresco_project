var home = groupActions.getHomeRef();

var actionNode = home.childByNamePath(args["actionId"]);
var result = [];

if (actionNode != null && actionNode.children != null) {

    for each(var action in actionNode.children) {
        result.push({
            name: action.properties["cm:name"],
            id: action.properties["lecm-group-actions:field-id"],
            type: action.properties["lecm-group-actions:field-type"],
            value: action.properties["lecm-group-actions:field-default-value"],
            order: action.properties["lecm-group-actions:field-priority"],
            mandatory: action.properties["lecm-group-actions:field-is-mandatory"],
            control: action.properties["lecm-group-actions:field-control"]
        });
    }

    result.sort(function (a, b) {
        return a.order - b.order
    });
}

model.result = jsonUtils.toJSONString(result);