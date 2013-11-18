/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.it.lecm.base.scripts;

import java.util.HashMap;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.extensions.webscripts.Cache;
import org.springframework.extensions.webscripts.DeclarativeWebScript;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.WebScriptRequest;
import ru.it.lecm.base.formsConfig.FormsConfig;
import ru.it.lecm.base.formsConfig.elements.controlElement.ControlConfigElement;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.extensions.webscripts.WebScriptException;
import ru.it.lecm.base.formsConfig.elements.controlElement.ParamConfigElement;

/**
 *
 * @author ikhalikov
 */
public class FormsConfigWebscript extends DeclarativeWebScript {

	private final static Log logger = LogFactory.getLog(FormsConfigWebscript.class);

	public void setFormsConfigService(FormsConfig formsConfigService) {
		this.formsConfigService = formsConfigService;
	}

	private FormsConfig formsConfigService;

	@Override
	protected Map<String, Object> executeImpl(WebScriptRequest req, Status status, Cache cache) {

		Map<String, Object> result = new HashMap<String, Object>();
		String typeId = req.getParameter("typeId");

		if (typeId == null) {
			logger.error("FormsConfigWebscript was called with empty parametr");
			throw new WebScriptException("FormsConfigWebscript was called with empty parametr");
		}
		try {
			result.put("result", executeGetControlsByTypeAction(typeId));
		} catch (Exception ex) {
			logger.error("Somethig goes wrong while processing method executeGetControlsByTypeAction", ex);
		}
		return result;
	}

	private JSONArray executeGetControlsByTypeAction(String typeId) throws Exception {
		Map<String, ControlConfigElement> controls = formsConfigService.getControlsByType(typeId);
		JSONArray jsonRes = new JSONArray();
		if(controls == null){
			return jsonRes;
		}
		JSONObject control = new JSONObject();
		for (Map.Entry<String, ControlConfigElement> entry : controls.entrySet()) {
			control = new JSONObject();
			String id = entry.getKey();
			ControlConfigElement controlConfigElement = entry.getValue();
			control.put("id", id);
			control.put("localName", controlConfigElement.getDisplayName());
			control.put("templatePath", controlConfigElement.getTemplatePath());

			Map<String, ParamConfigElement> params = controlConfigElement.getParamsMap();
			JSONArray jsonParamsArray = new JSONArray();
			for (Map.Entry<String, ParamConfigElement> param : params.entrySet()) {
				String paramId = param.getKey();
				ParamConfigElement paramConfigElement = param.getValue();
				JSONObject jsonParam = new JSONObject();
				jsonParam.put("id", paramId);
				jsonParam.put("localName", paramConfigElement.getLocalName());
				jsonParam.put("visible", paramConfigElement.isVisible());
				jsonParam.put("mandatory", paramConfigElement.isMandatory());
				jsonParam.put("value", paramConfigElement.getValue());
				jsonParamsArray.put(jsonParam);
			}
			control.put("params", jsonParamsArray);
			jsonRes.put(control);
		}


		return jsonRes;
	}
}
