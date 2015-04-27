<import resource="classpath:/alfresco/templates/webscripts/org/alfresco/slingshot/datalists/parse-args.lib.js">
<import resource="classpath:/alfresco/templates/webscripts/ru/it/lecm/search/evaluator.lib.js">
function main() {
	var pointRef = json.get("pointRef");
	protocolService.changePointStatus(pointRef,"EXECUTED_STATUS");
    node = search.findNode(pointRef);
	node.properties["lecm-protocol-ts:execution-date-real"] = new Date();
	node.save();
    var ctx = Packages.org.springframework.web.context.ContextLoader.getCurrentWebApplicationContext();
    model.item = Evaluator.run(node, [], null, ctx);
}

main();