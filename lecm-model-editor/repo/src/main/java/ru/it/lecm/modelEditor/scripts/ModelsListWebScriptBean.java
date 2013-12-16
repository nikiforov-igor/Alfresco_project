package ru.it.lecm.modelEditor.scripts;

import org.json.JSONObject;
import ru.it.lecm.base.beans.BaseWebScript;
import ru.it.lecm.modelEditor.beans.ModelsListBeanImpl;

/**
 * User: AIvkin
 * Date: 16.12.13
 * Time: 9:50
 */
public class ModelsListWebScriptBean extends BaseWebScript {
	private ModelsListBeanImpl modelListService;

	public void setModelListService(ModelsListBeanImpl modelListService) {
		this.modelListService = modelListService;
	}

	public JSONObject getModelsList() {
		return modelListService.getModelsList();
	}
}
