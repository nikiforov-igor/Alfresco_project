<import resource="classpath:/alfresco/templates/webscripts/ru/it/lecm/search/evaluator.lib.js">

(function () {
	var docNodeRef = args["nodeRef"];
	var documentNode = search.findNode(docNodeRef);
	var stages = [];
	if (documentNode != null) {
		var currentIterationNode = routesService.getDocumentCurrentIteration(documentNode);
		if (currentIterationNode != null) {
			var children = currentIterationNode.getChildren();
			if (children != null) {
				var decisionPropDefinition = base.getProperty("lecmApproveAspects:approvalDecision");
				var statePropDefinition = base.getProperty("lecmApproveAspects:approvalState");

				children.sort(function(a, b)
				{
					return (a.properties["cm:created"] < b.properties["cm:created"]) ? -1 : (a.properties["cm:created"] > b.properties["cm:created"]) ? 1 : 0;
				});

				for (var i = 0; i < children.length; i++) {
					var stage = children[i];
					if (stage.typeShort == "lecmWorkflowRoutes:stage" && !stage.hasAspect("sys:temporary") && !stage.hasAspect("lecm-workflow:temp")) {
						var items = [];
						var stageChildren = stage.getChildAssocsByType('lecmWorkflowRoutes:stageItem');
						stageChildren.sort(function (left, right) {
							return left.properties['lecmWorkflowRoutes:stageItemOrder'] - right.properties['lecmWorkflowRoutes:stageItemOrder'];
						});
						if (stageChildren != null) {
							for (var j = 0; j < stageChildren.length; j++) {
								var item = stageChildren[j];
								var employeeName = null;
								var employeeAssoc = item.associations["lecmWorkflowRoutes:stageItemEmployeeAssoc"];
								if (employeeAssoc != null && employeeAssoc.length > 0) {
									employeeName = employeeAssoc[0].properties["lecm-orgstr:employee-short-name"];
								}

								items.push({
									employee: employeeName,
									dueDate: item.properties["lecmWorkflowRoutes:stageItemDueDate"],
									decision: {
										value: item.properties["lecmApproveAspects:approvalDecision"],
										displayValue: Evaluator.translateField(decisionPropDefinition, item.properties["lecmApproveAspects:approvalDecision"])
									},
									state: {
										value: item.properties["lecmApproveAspects:approvalState"],
										displayValue: Evaluator.translateField(statePropDefinition, item.properties["lecmApproveAspects:approvalState"])
									}
								});
							}
						}
						stages.push({
							node: {
								nodeRef: stage.nodeRef.toString(),
								title: stage.properties["cm:title"],
								type: stage.properties["lecmWorkflowRoutes:stageWorkflowType"],
								decision: {
									value: stage.properties["lecmApproveAspects:approvalDecision"],
									displayValue: Evaluator.translateField(decisionPropDefinition, stage.properties["lecmApproveAspects:approvalDecision"])
								},
								state: {
									value: stage.properties["lecmApproveAspects:approvalState"],
									displayValue: Evaluator.translateField(statePropDefinition, stage.properties["lecmApproveAspects:approvalState"])
								},
								term: stage.properties["lecmWorkflowRoutes:stageWorkflowTerm"]
							},
							items: items
						});
					}
				}
			}
		}
	}
	model.stages = stages;
})();