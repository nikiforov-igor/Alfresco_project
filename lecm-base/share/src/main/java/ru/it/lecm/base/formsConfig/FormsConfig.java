/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.it.lecm.base.formsConfig;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONArray;
import org.json.JSONException;
import org.springframework.extensions.config.Config;
import org.springframework.extensions.config.ConfigService;
import org.springframework.extensions.webscripts.ScriptRemote;
import org.springframework.extensions.webscripts.connector.Response;
import org.springframework.extensions.webscripts.connector.ResponseStatus;
import ru.it.lecm.base.formsConfig.elements.fieldsElement.FieldTypesConfigElement;
import static ru.it.lecm.base.formsConfig.Constants.*;
import ru.it.lecm.base.formsConfig.elements.controlsTemplatesElement.ControlsTemplatesElement;
import ru.it.lecm.base.formsConfig.elements.fieldElement.TypeConfigElement;
import ru.it.lecm.base.formsConfig.elements.formLayoutElement.FormLayoutConfigElement;
import ru.it.lecm.base.formsConfig.elements.formTypeElement.FormTypeConfigElement;
import ru.it.lecm.base.formsConfig.elements.formsInfoElement.FormsInfoConfigElement;

/**
 *
 * @author ikhalikov
 */
public class FormsConfig {

	private final static Log logger = LogFactory.getLog(FormsConfig.class);

	private ConfigService configService;
	private ScriptRemote scriptRemote;

	public void setScriptRemote(ScriptRemote scriptRemote) {
		this.scriptRemote = scriptRemote;
	}
	private Map<String, TypeConfigElement> fullTypeControlsMap;
	private List<FormTypeConfigElement> fullFormsTypesList;
	private List<FormLayoutConfigElement> fullFormsLayoutsList;
	private ControlsTemplatesElement controlsTemplates;
	private boolean initialized = false;

	public void setConfigService(ConfigService configService) {
		this.configService = configService;
	}

	/**
	 * Возвращает мапу объектов, представляющих типы полей
	 *
	 * @return
	 */
	public Map<String, TypeConfigElement> getFullTypeControlsMap() {
		if (!initialized) {
			init();
		}
		return fullTypeControlsMap;
	}

	/**
	 * Возвращает мапу объектов, представляющих типы форм
	 *
	 * @return
	 */
	public List<FormTypeConfigElement> getFullFormsTypesMap() {
		if (!initialized) {
			init();
		}
		return fullFormsTypesList;
	}

	/**
	 * Возвращает мапу объектов, представляющих правила отображения форм
	 *
	 * @return
	 */
	public List<FormLayoutConfigElement> getFullFormsLayoutsMap() {
		if (!initialized) {
			init();
		}
		return fullFormsLayoutsList;
	}

	public void init() {
		Config configResult = configService.getGlobalConfig();
		FieldTypesConfigElement root = (FieldTypesConfigElement) configResult.getConfigElement(FIELD_TYPES_ELEMENT_ID);
		if (root != null) {
			fullTypeControlsMap = root.getFieldTypesMap();
		} else {
			fullTypeControlsMap = new HashMap<>();
			logger.warn("Cannot find config for " + FIELD_TYPES_ELEMENT_ID);
		}

		FormsInfoConfigElement formsInfo = (FormsInfoConfigElement) configResult.getConfigElement(FORMS_INFO_ID);
		if (formsInfo != null) {
			fullFormsTypesList = formsInfo.getFormTypeElements();
			fullFormsLayoutsList = formsInfo.getFormLayoutElements();
		} else {
			fullFormsTypesList = new ArrayList<>();
			fullFormsLayoutsList = new ArrayList<>();
			logger.warn("Cannot find config for " + FORMS_INFO_ID);
		}

		controlsTemplates = (ControlsTemplatesElement)configResult.getConfigElement(CONTROLS_TEMPLATES_ELEMENT_ID);
		if (controlsTemplates == null) {
			controlsTemplates = new ControlsTemplatesElement();
			logger.warn("Cannot find config for " + CONTROLS_TEMPLATES_ELEMENT_ID);
		}

		initialized = true;
	}

	/**
	 * Возвращает объект, содержащий конфиг для шаблонов-контролов
	 * @return конфиг для шаблонов контролов. Или пустой или с коллекцией шаблонов
	 */
	public ControlsTemplatesElement getControlsTemplates() {
		if (!initialized) {
			init();
		}
		return controlsTemplates;
	}

	/**
	 * Возвращает объект, содержащий конфиг для данного типа
	 *
	 * @param typeId
	 * @return
	 */
	public TypeConfigElement getTypeInfoById(String typeId, boolean inherited) {
		if (!initialized) {
			init();
		}

		TypeConfigElement typeConfig = fullTypeControlsMap.get(typeId);
		if(typeConfig == null){
			return null;
		}
		if (inherited) {
			List<TypeConfigElement> parents = getParents(typeId);
			for (TypeConfigElement parent : parents) {
				typeConfig.inherit(parent);
			}
		}
		return typeConfig;
	}

	public List<TypeConfigElement> getParents(String typeName) {
		List<TypeConfigElement> result = new ArrayList<TypeConfigElement>();

		String url = "/lecm/formConfig/getParents?typeName=" + typeName;
		Response response = scriptRemote.connect("alfresco").get(url);
		try {
			if (response.getStatus().getCode() == ResponseStatus.STATUS_OK) {
				JSONArray jsonResponse = new JSONArray(response.getResponse());
				for (int i = 0; i < jsonResponse.length(); i++) {
					TypeConfigElement el = fullTypeControlsMap.get(jsonResponse.get(i));
					if (el != null) {
						result.add(el);
					}
				}
			} else {
				logger.warn("Cannot get result from server");
			}
		} catch (JSONException e) {
			logger.warn("Cannot get result from server", e);
		}
		return result;

	}

}
