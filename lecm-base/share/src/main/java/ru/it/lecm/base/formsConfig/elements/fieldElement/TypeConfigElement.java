/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ru.it.lecm.base.formsConfig.elements.fieldElement;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.jackson.annotate.JsonProperty;
import org.springframework.extensions.config.ConfigElement;
import org.springframework.extensions.config.element.ConfigElementAdapter;
import ru.it.lecm.base.formsConfig.elements.controlElement.ControlConfigElement;
import static ru.it.lecm.base.formsConfig.Constants.*;


/**
 * POJO для элементов <field-type>
 *
 * @author ikhalikov
 */
public class TypeConfigElement extends ConfigElementAdapter{

	private final static Log logger = LogFactory.getLog(TypeConfigElement.class);

	@JsonProperty
	private String id;
	@JsonProperty
	private String localName;
	private HashMap<String, ControlConfigElement> controlsMap = new HashMap<String, ControlConfigElement>();

	@JsonProperty("controls")
	public Collection<ControlConfigElement> getControlsAsList(){
		return this.controlsMap.values();
	}

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

		result.setId(this.getId());
		result.setLocalName(this.getLocalName());

		result.controlsMap.putAll(this.getControlsMap());
		Map<String, ControlConfigElement> thisControls = this.getControlsMap();
		Map<String, ControlConfigElement> otherControls = otherElement.getControlsMap();
		for (Map.Entry<String, ControlConfigElement> entry : otherControls.entrySet()) {
			String id = entry.getKey();
			ControlConfigElement controlConfigElement = entry.getValue();
			if(!thisControls.containsKey(id)) {
				result.addControl(controlConfigElement);
			} else {
				logger.warn("Founded duplicate control with id = " + id);
			}
		}

		return result;
	}

}
