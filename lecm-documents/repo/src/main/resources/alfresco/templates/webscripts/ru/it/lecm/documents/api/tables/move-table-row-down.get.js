<import resource="classpath:/alfresco/templates/webscripts/org/alfresco/slingshot/datalists/parse-args.lib.js">
<import resource="classpath:/alfresco/templates/webscripts/ru/it/lecm/search/evaluator.lib.js">

var nodeRef = args['nodeRef'];
var secondNodeRef = documentTables.onMoveTableRowDown(nodeRef);

model.isMoveDown = (secondNodeRef !== null);
model.firstNodeRef = nodeRef;
model.secondNodeRef = secondNodeRef==null?"":secondNodeRef; 
model.secondItem = "";
if (secondNodeRef!==null) {
    var secondItem = search.findNode(secondNodeRef);
    var ctx = Packages.org.springframework.web.context.ContextLoader.getCurrentWebApplicationContext();
    model.secondItem = Evaluator.run(secondItem, [], null, ctx);
}    



