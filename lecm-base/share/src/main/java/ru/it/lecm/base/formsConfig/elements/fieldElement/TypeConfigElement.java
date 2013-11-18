/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ru.it.lecm.base.formsConfig.elements.fieldElement;

import java.util.HashMap;
import org.springframework.extensions.config.ConfigElement;
import org.springframework.extensions.config.element.ConfigElementAdapter;
import ru.it.lecm.base.formsConfig.elements.controlElement.ControlConfigElement;
import static ru.it.lecm.base.formsConfig.Constants.*;


/**
 *
 * @author ikhalikov
 */
public class TypeConfigElement extends ConfigElementAdapter{

	private String id;
	private String localName;
	private HashMap<String, ControlConfigElement> controlsMap = new HashMap<String, ControlConfigElement>();

	public void setLocalName(String localName) {
		this.localName = localName;
	}

	public String getLocalName() {
		return localName;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public TypeConfigElement(String name) {
		super(name);
	}

	public TypeConfigElement() {
		super(FIELD_TYPE_ELEMENT_ID);
	}

	public HashMap<String, ControlConfigElement> getControlsMap() {
		return controlsMap;
	}

	public void setControlsMap(HashMap<String, ControlConfigElement> controlsMap) {
		this.controlsMap = controlsMap;
	}

	public void addControl(ControlConfigElement control) {
		this.controlsMap.put(control.getId(), control);
	}

	@Override
	public ConfigElement combine(ConfigElement configElement) {
		TypeConfigElement otherElement = (TypeConfigElement) configElement;
		TypeConfigElement result = new TypeConfigElement();

		result.setId(otherElement.getId());
		result.setLocalName(otherElement.getLocalName());

		result.controlsMap.putAll(this.getControlsMap());
		result.controlsMap.putAll(otherElement.getControlsMap());
		return result;
	}

}
