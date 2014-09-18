(function() {
	var UUID = Packages.java.util.UUID;

	var resolveMacrosArg = args['resolveMacros'] === 'true';
	var destinationNodeStr = json.getString('alf_destination');
	var stageNode = search.findNode(destinationNodeStr);
	var i, refs, length, resolveMacros, documentNode, macrosResolved;
	var targetNode, stageItem, stageItems = [], assocTypeQName;
	var employeeAssocName = 'assoc_lecmWorkflowRoutes_stageItemEmployeeAssoc';
	var macrosAssocName = 'assoc_lecmWorkflowRoutes_stageItemMacrosAssoc';


	if (json.has(employeeAssocName)) {
		assocTypeQName = 'lecmWorkflowRoutes:stageItemEmployeeAssoc';
		refs = json.getString(employeeAssocName).split(',');
		resolveMacros = false;
	} else if (json.has(macrosAssocName)) {
		assocTypeQName = 'lecmWorkflowRoutes:stageItemMacrosAssoc';
		refs = json.getString(macrosAssocName).split(',');
		resolveMacros = resolveMacrosArg;
	} else {
		logger.log('Error processing request');
		return;
	}

	for (i = 0, length = refs.length; i < length; ++i) {
		targetNode = search.findNode(refs[i]);
		stageItem = stageNode.createNode(UUID.randomUUID().toString(), 'lecmWorkflowRoutes:stageItem');
		stageItem.createAssociation(targetNode, assocTypeQName);
		if (resolveMacros) {
			documentNode = routesService.getDocumentByStage(stageNode);
			macrosResolved = routesService.resolveStageItemMacros(stageItem, documentNode);
		}

		if (!resolveMacros || macrosResolved) {
			stageItems.push(stageItem);
		}
	}

	model.stageItems = stageItems;
})();
