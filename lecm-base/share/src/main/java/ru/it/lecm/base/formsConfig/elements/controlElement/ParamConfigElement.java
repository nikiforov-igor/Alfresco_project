/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ru.it.lecm.base.formsConfig.elements.controlElement;

import org.codehaus.jackson.annotate.JsonProperty;
import org.springframework.extensions.config.ConfigElement;
import org.springframework.extensions.config.element.ConfigElementAdapter;

/**
 * Класс для элемента <param>
 * @author ikhalikov
 */
public class ParamConfigElement extends ConfigElementAdapter{

	private static final String PARAM_ID = "param";
	@JsonProperty
	private String id;

	public void setId(String id) {
		this.id = id;
	}

	public String getId() {
		return id;
	}
	@JsonProperty
	private String localName;
	@JsonProperty
	private boolean mandatory;
	@JsonProperty
	private boolean visible;

	@JsonProperty
	private String description;

	public ParamConfigElement(String name) {
		super(name);
	}

	public ParamConfigElement() {
		super(PARAM_ID);
	}

	@Override
	public void setValue(String value) {
		this.value = value;
	}

	@Override
	@JsonProperty
	public String getValue(){
		return this.value;
	}

	public String getLocalName() {
		return localName;
	}

	public boolean isMandatory() {
		return mandatory;
	}

	public boolean isVisible() {
		return visible;
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setLocalName(String localName) {
		this.localName = localName;
	}

	public void setMandatory(boolean mandatory) {
		this.mandatory = mandatory;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	/*
	Так как слияние параметров не планируется, то и метод пустой
	*/
	@Override
	public ConfigElement combine(ConfigElement configElement) {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}


}
