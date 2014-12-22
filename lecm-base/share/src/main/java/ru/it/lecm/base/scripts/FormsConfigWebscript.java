/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.it.lecm.base.scripts;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
import org.springframework.extensions.webscripts.Cache;
import org.springframework.extensions.webscripts.DeclarativeWebScript;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.WebScriptRequest;
import ru.it.lecm.base.formsConfig.FormsConfig;
import org.springframework.extensions.webscripts.WebScriptException;
import ru.it.lecm.base.formsConfig.elements.fieldElement.TypeConfigElement;
import ru.it.lecm.base.formsConfig.elements.formLayoutElement.FormLayoutConfigElement;
import ru.it.lecm.base.formsConfig.elements.formTypeElement.FormTypeConfigElement;

/**
 *
 * @author ikhalikov
 */
public class FormsConfigWebscript extends DeclarativeWebScript {

	private final static Log logger = LogFactory.getLog(FormsConfigWebscript.class);
	private final ObjectMapper jsonMapper = new ObjectMapper();
	private FormsConfig formsConfigService;

	public void setFormsConfigService(FormsConfig formsConfigService) {
		this.formsConfigService = formsConfigService;
	}

	@Override
	protected Map<String, Object> executeImpl(WebScriptRequest req, Status status, Cache cache) {

		Map<String, Object> result = new HashMap<String, Object>();
		String action = req.getParameter("action");

		if (action == null) {
			logger.error("FormsConfigWebscript was called with empty parametr");
			throw new WebScriptException("FormsConfigWebscript was called with empty parametr");
		}

		jsonMapper.configure(SerializationConfig.Feature.AUTO_DETECT_FIELDS, false);
		jsonMapper.configure(SerializationConfig.Feature.AUTO_DETECT_GETTERS, false);
		jsonMapper.configure(SerializationConfig.Feature.DEFAULT_VIEW_INCLUSION, false);
		jsonMapper.configure(SerializationConfig.Feature.WRITE_NULL_MAP_VALUES, true);

		try {
			if (action.equals("getControlsById")) {
				result.put("result", executeGetControlsByTypeAction(req));
			} else if (action.equals("getFormLayouts")) {
				result.put("result", executeGetFormsLayoutsAction());
			} else if (action.equals("getFormTypes")) {
				result.put("result", executeGetFormsTypesAction());
			} else if (action.equals("getControlsTemplates")) {
				result.put("result", executeGetControlsTemplatesAction());
			}
		} catch (Exception ex) {
			logger.error("Somethig goes wrong while executing FormsConfigWebscript");
		}

		return result;
	}

	private String executeGetControlsTemplatesAction() throws IOException {
		return jsonMapper.writeValueAsString(formsConfigService.getControlsTemplates().getTemplates());
	}

	private String executeGetControlsByTypeAction(WebScriptRequest req) throws Exception {
		String typeId = req.getParameter("typeId");
		if (typeId == null) {
			logger.error("FormsConfigWebscript was called with empty parametr");
			throw new WebScriptException("FormsConfigWebscript was called with empty parametr");
		}
		TypeConfigElement typeInfo = formsConfigService.getTypeInfoById(typeId, true);
		if (typeInfo != null) {
			return jsonMapper.writeValueAsString(typeInfo.getControlsAsList());
		} else {
			return "[]";
		}
	}

	private String executeGetFormsLayoutsAction() throws IOException {
		List<FormLayoutConfigElement> layouts = formsConfigService.getFullFormsLayoutsMap();
		return jsonMapper.writeValueAsString(layouts);
	}

	private String executeGetFormsTypesAction() throws IOException {
		List<FormTypeConfigElement> types = formsConfigService.getFullFormsTypesMap();
		return jsonMapper.writeValueAsString(types);
	}
}
