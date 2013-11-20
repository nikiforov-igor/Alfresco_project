/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ru.it.lecm.base.formsConfig.elements.formsInfoElement;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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

	private List<FormTypeConfigElement> formTypeElements = new ArrayList<FormTypeConfigElement>();
	private List<FormLayoutConfigElement> formLayoutElements = new ArrayList<FormLayoutConfigElement>();

	public List<FormTypeConfigElement> getFormTypeElements() {
		return formTypeElements;
	}

	public void setFormTypeElements(List<FormTypeConfigElement> formTypeElements) {
		this.formTypeElements = formTypeElements;
	}

	public List<FormLayoutConfigElement> getFormLayoutElements() {
		return formLayoutElements;
	}

	public void setFormLayoutElements(List<FormLayoutConfigElement> formLayoutElements) {
		this.formLayoutElements = formLayoutElements;
	}

	public FormsInfoConfigElement(String name) {
		super(name);
	}

	public FormsInfoConfigElement() {
		super(FORMS_INFO_ID);
	}

	public void addFormLayout(FormLayoutConfigElement el) {
		this.formLayoutElements.add(el);
	}

	public void addFormType(FormTypeConfigElement el) {
		this.formTypeElements.add(el);
	}

	@Override
	public ConfigElement combine(ConfigElement configElement) {
		FormsInfoConfigElement otherFormsInfo = (FormsInfoConfigElement) configElement;
		FormsInfoConfigElement result = new FormsInfoConfigElement();

		List<FormTypeConfigElement> thisTypeElements = this.getFormTypeElements();
		List<FormLayoutConfigElement> thisLayoutElements = this.getFormLayoutElements();

		List<FormTypeConfigElement> otherTypeElements = otherFormsInfo.getFormTypeElements();
		List<FormLayoutConfigElement> otherLayoutElements = otherFormsInfo.getFormLayoutElements();

		thisTypeElements.addAll(otherTypeElements);
		thisLayoutElements.addAll(otherLayoutElements);

		result.setFormLayoutElements(thisLayoutElements);
		result.setFormTypeElements(thisTypeElements);

		return result;
	}

}
