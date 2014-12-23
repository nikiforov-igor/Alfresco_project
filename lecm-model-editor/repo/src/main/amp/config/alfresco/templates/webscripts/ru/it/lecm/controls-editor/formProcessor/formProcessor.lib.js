function getParamById(controlConfig, paramId) {
	var i;
	for (i in controlConfig.params) {
		if (paramId == controlConfig.params[i].id) {
			return controlConfig.params[i];
		}
	}
}

function createControlParamsFromFormData(persistedObject) {
	logger.warn('Not implemented yet!');
}

function createControlParamsFromJSON(persistedObject) {

	var prefix = 'param_',
		UUID = Packages.java.util.UUID,
		control = search.findNode(persistedObject),
		controlConfig = jsonUtils.toObject(json.get('controlConfig')),
		jsonKeys = json.keys(),
		nextKey, paramConfig, param;

		control.properties['lecm-controls-editor:control-id'] = controlConfig.id + '-' + UUID.randomUUID().toString();
		control.save();

	while (jsonKeys.hasNext()) {
		nextKey = jsonKeys.next();
		if (nextKey.indexOf(prefix) == 0) {
			paramConfig = getParamById(controlConfig, nextKey.slice(prefix.length));
			param = control.createNode(null, 'lecm-controls-editor:control-param');
			param.properties['lecm-controls-editor:param-value'] = json.get(nextKey);
			param.properties['lecm-controls-editor:param-visible'] = true;
			if (paramConfig) {
				param.properties['cm:title'] = paramConfig.localName;
				param.properties['cm:description'] = paramConfig.description;
				param.properties['lecm-controls-editor:param-id'] = paramConfig.id;
				param.properties['lecm-controls-editor:param-mandatory'] = paramConfig.mandatory;
			}
			param.save();
		}
	}
}

function createControlParams(persistedObject) {
	try {
		if (typeof formdata !== 'undefined') {
			createControlParamsFromFormData(persistedObject);
			return;
		}
		if (typeof json !== 'undefined') {
			createControlParamsFromJSON(persistedObject);
			return;
		}
		logger.warn('formdata object was undefined.');
		logger.warn('json object was undefined.');
		status.setCode(501, 'formdata object and json object are undefined');
	} catch (error) {
		var msg = error.message;
		status.setCode(500, msg);

		if (logger.isLoggingEnabled()) {
			logger.log(msg);
			logger.log('Returning 500 status code');
		}
	}
}
