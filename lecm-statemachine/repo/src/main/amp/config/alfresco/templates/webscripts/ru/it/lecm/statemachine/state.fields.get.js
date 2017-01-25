var nodeRef = args["documentNodeRef"];
var node = search.findNode(nodeRef);

//var ctx = Packages.org.springframework.web.context.ContextLoader.getCurrentWebApplicationContext();
var helper = statemachine;//ctx.getBean("stateMachineHelper");

var stateFields = helper.getStateFields(node);
var items = [];
var fields = stateFields.getFields().toArray();
var isDraft = helper.isDraft(node);
var writeAllFieldsPermission = lecmPermission.hasPermission(node, "_lecmPerm_WriteAllFields");

for each (var field in fields) {
    items.push({
        name: field.getName(),
        editable: writeAllFieldsPermission && !isDraft ? true : field.isEditable()
    });
}

var result = {
    hasStatemachine: stateFields.hasStatemachine(),
    fields: items
}

model.result = jsonUtils.toJSONString(result);