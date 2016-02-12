<import resource="classpath:/alfresco/templates/webscripts/org/alfresco/slingshot/datalists/parse-args.lib.js">
<import resource="classpath:/alfresco/templates/webscripts/ru/it/lecm/search/evaluator.lib.js">

(function () {
	var ctx = Packages.org.springframework.web.context.ContextLoader.getCurrentWebApplicationContext();
	var direction = '' + args['direction'];
	var fields = ('' + json.getString('fields')).split(',');
	var nameSubstituteStrings = ('' + json.getString('nameSubstituteStrings')).split(',');
	var firstNode = search.findNode(json.getString('nodeRef'));
	var secondNode = null;
	var stage = firstNode.parent;
	var order = firstNode.properties['lecmWorkflowRoutes:stageItemOrder'];
	var newOrder = order;
	var children = null;
	switch (direction.toUpperCase()) {
		case 'UP': newOrder--; break;
		case 'DOWN': newOrder++; break;
	}
	children = stage.childrenByXPath('*//.[@lecmWorkflowRoutes:stageItemOrder=' + newOrder + ']');
	if (children && children.length) {
		secondNode = children[0];
		secondNode.properties['lecmWorkflowRoutes:stageItemOrder'] = order;
		secondNode.save();
		model.secondItem = Evaluator.run(secondNode, fields, nameSubstituteStrings, ctx);
		model.secondNodeRef = secondNode.nodeRef.toString();
	}

	firstNode.properties['lecmWorkflowRoutes:stageItemOrder'] = newOrder;
	firstNode.save();
	model.firstItem = Evaluator.run(firstNode, fields, nameSubstituteStrings, ctx);
	model.firstNodeRef = firstNode.nodeRef.toString();
})();
