(function() {
	var node = search.findNode(args['nodeRef']);

	var chiefEmployeeAssocs = node.sourceAssocs['lecm-deputy:deputy-assoc'];
	if(chiefEmployeeAssocs && chiefEmployeeAssocs.length > 0) {
		chiefEmployeeAssocs[0].removeAssociation(node, 'lecm-deputy:deputy-assoc');
	}

})();