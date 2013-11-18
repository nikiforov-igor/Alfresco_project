/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ru.it.lecm.base.formsConfig.elements.fieldsElement;

import java.util.HashMap;
import java.util.Map;
import org.springframework.extensions.config.ConfigElement;
import org.springframework.extensions.config.element.ConfigElementAdapter;
import ru.it.lecm.base.formsConfig.elements.fieldElement.TypeConfigElement;
import static ru.it.lecm.base.formsConfig.Constants.*;


/**
 *
 * @author ikhalikov
 */
public class FieldTypesConfigElement extends ConfigElementAdapter {

	private Map<String, TypeConfigElement> fieldTypesMap = new HashMap<String, TypeConfigElement>();

	public Map<String, TypeConfigElement> getFieldTypesMap() {
		return fieldTypesMap;
	}

	public void setFieldTypesMap(Map<String, TypeConfigElement> fieldTypesMap) {
		this.fieldTypesMap = fieldTypesMap;
	}

	public void addType(TypeConfigElement type){
		this.fieldTypesMap.put(type.getId(), type);
	}

	public FieldTypesConfigElement(String name) {
		super(name);
	}

	public FieldTypesConfigElement() {
		super(FIELD_TYPES_ELEMENT_ID);
	}


	@Override
	public ConfigElement combine(ConfigElement otherConfigElement) {
		FieldTypesConfigElement result = new FieldTypesConfigElement();
		FieldTypesConfigElement otherTypes = (FieldTypesConfigElement) otherConfigElement;

		Map<String, TypeConfigElement> otherElements = otherTypes.getFieldTypesMap();
		Map<String, TypeConfigElement> thisElements = this.getFieldTypesMap();

		result.setFieldTypesMap(thisElements);

		for (Map.Entry<String, TypeConfigElement> entry : otherElements.entrySet()) {
			String id = entry.getKey();
			TypeConfigElement otherElement = entry.getValue();
			TypeConfigElement thisElement = thisElements.get(id);
			if(thisElements.containsKey(id)){
				result.addType((TypeConfigElement) thisElement.combine(otherElement));
			} else {
				result.addType(otherElement);
			}
		}

		//result.fieldTypesMap.putAll(this.getFieldTypesMap());
		//result.fieldTypesMap.putAll(otherTypes.getFieldTypesMap());
		return result;
	}

}
