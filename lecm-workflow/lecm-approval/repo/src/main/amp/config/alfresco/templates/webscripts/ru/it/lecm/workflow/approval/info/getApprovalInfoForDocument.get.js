<import resource="classpath:/alfresco/templates/webscripts/ru/it/lecm/search/evaluator.lib.js">

(function () {
	var docNodeRef = args["nodeRef"];
	var documentNode = search.findNode(docNodeRef);
	var stages = [];
	if (documentNode != null) {
		var currentIterationNode = routesService.getDocumentCurrentIteration(documentNode);
		var children = currentIterationNode.getChildren();
		if (children != null) {
			var ctx = Packages.org.springframework.web.context.ContextLoader.getCurrentWebApplicationContext();
			var dictionaryService = ctx.getBean("dictionaryService");
			var namespaceService = ctx.getBean("namespaceService");

			var decisionPropDefinition = getFieldDefinition("lecmApproveAspects:approvalDecision", dictionaryService, namespaceService);

			for (var i = 0; i < children.length; i++) {
				var stage = children[i];
				if (stage.typeShort == "lecmWorkflowRoutes:stage" && !stage.hasAspect("sys:temporary") && !stage.hasAspect("lecm-workflow:temp")) {
					var items = [];
					var stageChildren = stage.getChildren();
					if (stageChildren != null) {
						for (var j = 0; j < stageChildren.length; j++) {
							var item = stageChildren[j];
							if (item.typeShort == "lecmWorkflowRoutes:stageItem") {
								var employeeName = null;
								var employeeAssoc = item.associations["lecmWorkflowRoutes:stageItemEmployeeAssoc"];
								if (employeeAssoc != null && employeeAssoc.length > 0) {
									employeeName = employeeAssoc[0].properties["lecm-orgstr:employee-short-name"];
								}

								items.push({
									employee: employeeName,
									dueDate: item.properties["lecmWorkflowRoutes:stageItemDueDate"],
									state: Evaluator.translateField(decisionPropDefinition, item.properties["lecmApproveAspects:approvalDecision"], dictionaryService)
								});
							}
						}
					}
					stages.push({
						node: {
							nodeRef: stage.nodeRef.toString(),
							title: stage.properties["cm:title"],
							type: stage.properties["lecmWorkflowRoutes:stageWorkflowType"],
							decision: {
								value: stage.properties["lecmApproveAspects:approvalDecision"],
								displayValue: Evaluator.translateField(decisionPropDefinition, stage.properties["lecmApproveAspects:approvalDecision"], dictionaryService)
							},
							term: stage.properties["lecmWorkflowRoutes:stageWorkflowTerm"]
						},
						items: items
					});
				}
			}
		}
	}
	model.stages = stages;
})();

function getFieldDefinition(propName, dictionaryService, namespaceService) {
	var propQName = Packages.org.alfresco.service.namespace.QName.createQName(propName, namespaceService);
	return dictionaryService.getProperty(propQName);
}