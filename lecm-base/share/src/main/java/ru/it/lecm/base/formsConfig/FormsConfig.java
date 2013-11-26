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
import org.springframework.extensions.config.Config;
import org.springframework.extensions.config.ConfigService;
import ru.it.lecm.base.formsConfig.elements.fieldsElement.FieldTypesConfigElement;
import static ru.it.lecm.base.formsConfig.Constants.*;
import ru.it.lecm.base.formsConfig.elements.controlElement.ControlConfigElement;
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
	private Map<String, TypeConfigElement> fullTypeControlsMap = new HashMap<String, TypeConfigElement>();
	private List<FormTypeConfigElement> fullFormsTypesList = new ArrayList<FormTypeConfigElement>();
	private List<FormLayoutConfigElement> fullFormsLayoutsList = new ArrayList<FormLayoutConfigElement>();
	private boolean initialized = false;

	public void setConfigService(ConfigService configService) {
		this.configService = configService;
	}

	/**
	 * Возвращает мапу объектов, представляющих типы полей
	 * @return
	 */
	public Map<String, TypeConfigElement> getFullTypeControlsMap() {
		if (!initialized) init();
		return fullTypeControlsMap;
	}

	/**
	 * Возвращает мапу объектов, представляющих типы форм
	 * @return
	 */
	public List<FormTypeConfigElement> getFullFormsTypesMap() {
		if (!initialized) init();
		return fullFormsTypesList;
	}

	/**
	 * Возвращает мапу объектов, представляющих правила отображения форм
	 * @return
	 */
	public List<FormLayoutConfigElement> getFullFormsLayoutsMap() {
		if (!initialized) init();
		return fullFormsLayoutsList;
	}

	public void init() {
		Config configResult = configService.getGlobalConfig();
		FieldTypesConfigElement root = (FieldTypesConfigElement) configResult.getConfigElement(FIELD_TYPES_ELEMENT_ID);
		if (root != null) {
			fullTypeControlsMap = root.getFieldTypesMap();
		} else {
			logger.info("Cannot find config for " + FIELD_TYPES_ELEMENT_ID);
		}

		FormsInfoConfigElement formsInfo = (FormsInfoConfigElement) configResult.getConfigElement(FORMS_INFO_ID);
		if (formsInfo != null) {
			fullFormsTypesList = formsInfo.getFormTypeElements();
			fullFormsLayoutsList = formsInfo.getFormLayoutElements();
		} else {
			logger.info("Cannot find config for " + FORMS_INFO_ID);
		}

		initialized = true;
	}

	/**
	 * Возвращает объект, содержащий конфиг для данного типа
	 * @param typeId
	 * @return
	 */
	public TypeConfigElement getTypeInfoById(String typeId) {
		if (!initialized) {
			init();
		}

		TypeConfigElement typeConfig = fullTypeControlsMap.get(typeId);
		TypeConfigElement assocConfig = fullTypeControlsMap.get(OBJECT_ELEMENT_ID);
		if(typeConfig == null) {
			return assocConfig;
		}
		return (TypeConfigElement) typeConfig.combine(assocConfig);
	}

}
