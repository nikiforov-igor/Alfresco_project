function createFormAttributes(persistedObject) {
	logger.log('################################################################################');
	logger.log('creating default attributes for ' + persistedObject);
	logger.log('################################################################################');
	var form = search.findNode(persistedObject);
	var typename = '' + form.getParent().name;
	typename = typename.replace('_', ':');
	var fillByDefault = form.properties['lecm-forms-editor:fill-by-default'];
	logger.log('typeof fillByDefault = ' + typeof fillByDefault);
	if (fillByDefault) {
		logger.log('тип данных ' + typename);
		logger.log('для указанной формы генерим атрибуты: проперти и ассоциации');
	}
}
