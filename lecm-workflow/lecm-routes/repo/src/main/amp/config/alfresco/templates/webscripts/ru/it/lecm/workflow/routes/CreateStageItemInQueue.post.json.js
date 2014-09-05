(function() {
	var UUID = Packages.java.util.UUID;

	var interpolateMacrosArg = args['interpolateMacros'] === 'true';
	var destinationNodeStr = json.getString('alf_destination');
	var destinationNode = search.findNode(destinationNodeStr);
	var i, refs, length, interpolateMacros;
	var targetNode, stageItem, stageItems = [], assocTypeQName;
	var employeeAssocName = 'assoc_lecmWorkflowRoutes_stageItemEmployeeAssoc';
	var macrosAssocName = 'assoc_lecmWorkflowRoutes_stageItemMacrosAssoc';


	if (json.has(employeeAssocName)) {
		assocTypeQName = 'lecmWorkflowRoutes:stageItemEmployeeAssoc';
		refs = json.getString(employeeAssocName).split(',');
		interpolateMacros = false;
	} else if (json.has(macrosAssocName)) {
		assocTypeQName = 'lecmWorkflowRoutes:stageItemMacrosAssoc';
		refs = json.getString(macrosAssocName).split(',');
		interpolateMacros = interpolateMacrosArg;
	} else {
		logger.log('Error processing request');
		return;
	}

	for (i = 0, length = refs.length; i < length; ++i) {
		targetNode = search.findNode(refs[i]);
		stageItem = destinationNode.createNode(UUID.randomUUID().toString(), 'lecmWorkflowRoutes:stageItem');
		stageItem.createAssociation(targetNode, assocTypeQName);
		stageItems.push(stageItem);
		if (interpolateMacros) {
			routesService.resolveStageItemMacros(stageItem);
		}
	}

	model.stageItems = stageItems;
})();
