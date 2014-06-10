var nodeRef = args["documentNodeRef"];
var node = search.findNode(nodeRef);

var ctx = Packages.org.springframework.web.context.ContextLoader.getCurrentWebApplicationContext();
var helper = ctx.getBean("stateMachineHelper");

var stateFields = helper.getStateFields(node.nodeRef);
var items = [];
var fields = stateFields.getFields().toArray();


for each (var field in fields) {
    items.push({
        name: field.getName(),
        editable: field.isEditable()
    });
}

var result = {
    hasStatemachine: stateFields.hasStatemachine(),
    fields: items
}

model.result = jsonUtils.toJSONString(result);