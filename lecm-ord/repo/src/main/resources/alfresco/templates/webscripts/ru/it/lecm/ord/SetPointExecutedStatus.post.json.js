<import resource="classpath:/alfresco/templates/webscripts/org/alfresco/slingshot/datalists/parse-args.lib.js">
<import resource="classpath:/alfresco/templates/webscripts/ru/it/lecm/search/evaluator.lib.js">
function main() {
	var pointRef = json.get("pointRef");
	ordStatemachine.changePointStatus(pointRef,"EXECUTED_STATUS");
    node = search.findNode(pointRef);
    
    model.item = Evaluator.run(node, [], null);
}

main();