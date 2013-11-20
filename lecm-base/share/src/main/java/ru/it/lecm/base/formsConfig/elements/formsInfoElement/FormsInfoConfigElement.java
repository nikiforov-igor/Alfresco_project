/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ru.it.lecm.base.formsConfig.elements.formsInfoElement;

import java.util.HashMap;
import java.util.Map;
import org.springframework.extensions.config.ConfigElement;
import org.springframework.extensions.config.element.ConfigElementAdapter;
import static ru.it.lecm.base.formsConfig.Constants.*;
import ru.it.lecm.base.formsConfig.elements.formLayoutElement.FormLayoutConfigElement;
import ru.it.lecm.base.formsConfig.elements.formTypeElement.FormTypeConfigElement;

/**
 * POJO для корневого элемента <forms-info>
 *
 * @author ikhalikov
 */
public class FormsInfoConfigElement extends ConfigElementAdapter{

	private Map<String, FormTypeConfigElement> formTypeElements = new HashMap<String, FormTypeConfigElement>();
	private Map<String, FormLayoutConfigElement> formLayoutElements = new HashMap<String, FormLayoutConfigElement>();

	public Map<String, FormTypeConfigElement> getFormTypeElements() {
		return formTypeElements;
	}

	public void setFormTypeElements(Map<String, FormTypeConfigElement> formTypeElements) {
		this.formTypeElements = formTypeElements;
	}

	public Map<String, FormLayoutConfigElement> getFormLayoutElements() {
		return formLayoutElements;
	}

	public void setFormLayoutElements(Map<String, FormLayoutConfigElement> formLayoutElements) {
		this.formLayoutElements = formLayoutElements;
	}

	public FormsInfoConfigElement(String name) {
		super(name);
	}

	public FormsInfoConfigElement() {
		super(FORMS_INFO_ID);
	}

	public void addFormLayout(FormLayoutConfigElement el) {
		this.formLayoutElements.put(el.getId(), el);
	}

	public void addFormType(FormTypeConfigElement el) {
		this.formTypeElements.put(el.getId(), el);
	}

	@Override
	public ConfigElement combine(ConfigElement configElement) {
		FormsInfoConfigElement otherFormsInfo = (FormsInfoConfigElement) configElement;
		FormsInfoConfigElement result = new FormsInfoConfigElement();

		Map<String, FormTypeConfigElement> thisTypeElements = this.getFormTypeElements();
		Map<String, FormLayoutConfigElement> thisLayoutElements = this.getFormLayoutElements();

		result.setFormLayoutElements(thisLayoutElements);
		result.setFormTypeElements(thisTypeElements);

		Map<String, FormTypeConfigElement> otherTypeElements = otherFormsInfo.getFormTypeElements();
		Map<String, FormLayoutConfigElement> otherLayoutElements = otherFormsInfo.getFormLayoutElements();

		for (Map.Entry<String, FormLayoutConfigElement> entry : otherLayoutElements.entrySet()) {
			String id = entry.getKey();
			FormLayoutConfigElement layoutConfigElement = entry.getValue();
			if(!thisLayoutElements.containsKey(id)) {
				result.addFormLayout(layoutConfigElement);
			}
		}

		for (Map.Entry<String, FormTypeConfigElement> entry : otherTypeElements.entrySet()) {
			String id = entry.getKey();
			FormTypeConfigElement typeConfigElement = entry.getValue();
			if(!thisTypeElements.containsKey(id)) {
				result.addFormType(typeConfigElement);
			}
		}

		return result;
	}

}
