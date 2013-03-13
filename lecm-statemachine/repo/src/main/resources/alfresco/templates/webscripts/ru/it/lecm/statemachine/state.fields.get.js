var nodeRef = args["documentNodeRef"];
var node = search.findNode(nodeRef);

var ctx = Packages.org.springframework.web.context.ContextLoader.getCurrentWebApplicationContext();
var helper = ctx.getBean("stateMachineHelper");

var fields = helper.getStateFields(node.nodeRef).toArray();
var result = [];

for each (var field in fields) {
    result.push({
        name: field.getName(),
        editable: field.isEditable()
    });
}

model.result = jsonUtils.toJSONString(result);