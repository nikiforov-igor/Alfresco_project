(function () {
	var dictionary,
		resp = remote.connect('alfresco').get('/lecm/dictionary/api/getDictionary?dicName=' + encodeURIComponent('Списки ознакомления'));
	if (200 == resp.status) {
		dictionary = jsonUtils.toObject(resp);
		model.title = dictionary.title;
		model.type = dictionary.type;
		model.nodeRef = dictionary.nodeRef;
		model.description = dictionary.description;
		model.itemType = dictionary.itemType;
		model.attributeForShow = dictionary.attributeForShow;
		model.plane = dictionary.plane;
	}
})();
