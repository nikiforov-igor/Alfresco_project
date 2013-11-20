/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ru.it.lecm.base.formsConfig.elements.formLayoutElement;

import org.codehaus.jackson.annotate.JsonProperty;
import org.springframework.extensions.config.ConfigElement;
import org.springframework.extensions.config.element.ConfigElementAdapter;
import static ru.it.lecm.base.formsConfig.Constants.*;

/**
 * POJO для элемента <form-layout>
 * @author ikhalikov
 */
public class FormLayoutConfigElement extends ConfigElementAdapter{
	@JsonProperty
	private String localName;
	@JsonProperty
	private String template;

	public String getLocalName() {
		return localName;
	}

	public void setLocalName(String localName) {
		this.localName = localName;
	}

	public String getTemplate() {
		return template;
	}

	public void setTemplate(String template) {
		this.template = template;
	}

	public FormLayoutConfigElement(String name) {
		super(name);
	}

	public FormLayoutConfigElement() {
		super(FORM_LAYOUT_ID);
	}

	@Override
	public ConfigElement combine(ConfigElement configElement) {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

}
