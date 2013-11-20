/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ru.it.lecm.base.formsConfig.elements.formTypeElement;

import org.codehaus.jackson.annotate.JsonProperty;
import org.springframework.extensions.config.ConfigElement;
import org.springframework.extensions.config.element.ConfigElementAdapter;
import static ru.it.lecm.base.formsConfig.Constants.*;

/**
 * POJO для элемента <form-type>
 * 
 * @author ikhalikov
 */
public class FormTypeConfigElement extends ConfigElementAdapter{
	@JsonProperty
	private String id;
	@JsonProperty
	private String evaluatorType;
	@JsonProperty
	private String localName;
	@JsonProperty
	private String formId;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getEvaluatorType() {
		return evaluatorType;
	}

	public void setEvaluatorType(String evaluatorType) {
		this.evaluatorType = evaluatorType;
	}

	public String getLocalName() {
		return localName;
	}

	public void setLocalName(String localName) {
		this.localName = localName;
	}

	public String getFormId() {
		return formId;
	}

	public void setFormId(String formId) {
		this.formId = formId;
	}

	public FormTypeConfigElement() {
		super(FORM_TYPE_ELEMENT_ID);
	}

	public FormTypeConfigElement(String name) {
		super(name);
	}

	@Override
	public ConfigElement combine(ConfigElement configElement) {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

}
