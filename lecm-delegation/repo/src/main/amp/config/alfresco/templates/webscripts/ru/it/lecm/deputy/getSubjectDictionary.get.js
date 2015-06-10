(function() {
	var settings = deputyService.getSettings();
	var dictionaryNodeRef = settings.assocs['lecm-deputy:dictionary-assoc'][0];
	model.plane = dictionaryNodeRef.properties['lecm-dic:plane'];
	model.path = dictionaryNodeRef.qnamePath;
	model.itemsType = dictionaryNodeRef.properties['lecm-dic:type'];
	model.nodeRef = dictionaryNodeRef.nodeRef.toString();
})();