/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ru.it.lecm.base.formsConfig;

import java.util.HashMap;
import java.util.Map;
import org.springframework.extensions.config.Config;
import org.springframework.extensions.config.ConfigService;
import ru.it.lecm.base.formsConfig.elements.fieldsElement.FieldTypesConfigElement;
import static ru.it.lecm.base.formsConfig.Constants.*;
import ru.it.lecm.base.formsConfig.elements.controlElement.ControlConfigElement;
import ru.it.lecm.base.formsConfig.elements.fieldElement.TypeConfigElement;

/**
 *
 * @author ikhalikov
 */
public class FormsConfig {

	private ConfigService configService;
	private Map<String, TypeConfigElement> fullTypeControlsMap;

	public void setConfigService(ConfigService configService) {
		this.configService = configService;
	}

	public void init() {
		Config configResult = configService.getGlobalConfig();
		FieldTypesConfigElement root = (FieldTypesConfigElement) configResult.getConfigElement(FIELD_TYPES_ELEMENT_ID);
		fullTypeControlsMap = root.getFieldTypesMap();
	}

	public Map<String, ControlConfigElement> getControlsByType(String typeId) {
		if(fullTypeControlsMap == null) {
			init();
		}
		Map<String, ControlConfigElement> result = new HashMap<String, ControlConfigElement>();
		TypeConfigElement typeConfig = fullTypeControlsMap.get(typeId);
		if(typeConfig != null) {
			return typeConfig.getControlsMap();
		}
		return null;
	}

}
