function createFormAttributes(persistedObject) {
	try {
		var form = search.findNode(persistedObject);
		var typename = '' + form.getParent().name;
		typename = typename.replace('_', ':');
		var fillByDefault = form.properties['lecm-forms-editor:fill-by-default'];
		if (fillByDefault) {
			//для найденного типа данных получить проперти и ассоциации которые описаны в модели, без учета наследования
			//для каждой проперти и ассоциации сгенерить lecm-forms-editor:attr заполнить обязательные поля attr-name и attr-title
			formsEditor.generateDefaultFormAttributes(form, typename);
		}
	} catch (error) {
		var msg = error.message;
		status.setCode(500, msg);

		if (logger.isLoggingEnabled()) {
			logger.log(msg);
			logger.log("Returning 500 status code");
		}
	}
}
