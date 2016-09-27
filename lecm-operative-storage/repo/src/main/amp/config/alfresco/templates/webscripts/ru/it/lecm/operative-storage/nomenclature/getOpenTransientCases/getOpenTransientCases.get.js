(function() {
	var nomenclature = search.findNode(args['nodeRef']); //годовой раздел номенклатуры дел
	var path = nomenclature.qnamePath;
	var nodes = search.xpathSearch(path + "//*");
	var items = [];
	if (nodes != null) {
		for (var i = 0; i < nodes.length; i++) {
			var node = nodes[i];
			if (node.typeShort == "lecm-os:nomenclature-case" && node.properties["lecm-os:nomenclature-case-status"] == "OPEN" && node.properties["lecm-os:nomenclature-case-transient"]) {
				items.push(node.nodeRef.toString());
			}
		}
	}
	model.items = items;
})();
