var nodeRef = args["nodeRef"];
var workflowId = args["workflowId"];

var node = search.findNode(nodeRef);
var props = new Array(1);
props["lecm-workflow:status"] = "NEW";
node.addAspect("lecm-workflow:documentStatus", props);
node.save();

var workflow = actions.create("start-workflow");
workflow.parameters.workflowName = "activiti$" + workflowId;
workflow.parameters["bpm:assignee"] = people.getPerson("workflow");
workflow.execute(node);