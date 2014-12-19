/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ru.it.lecm.base.formsConfig.elements.formsInfoElement;

import org.dom4j.Element;
import org.springframework.extensions.config.ConfigElement;
import org.springframework.extensions.config.xml.elementreader.ConfigElementReader;
import static ru.it.lecm.base.formsConfig.Constants.*;
import ru.it.lecm.base.formsConfig.elements.formLayoutElement.FormLayoutConfigElement;
import ru.it.lecm.base.formsConfig.elements.formLayoutElement.FormLayoutElementReader;
import ru.it.lecm.base.formsConfig.elements.formTypeElement.FormTypeConfigElement;
import ru.it.lecm.base.formsConfig.elements.formTypeElement.FormTypeElementReader;

/**
 * Ридер для корневого элемента <forms-info>
 *
 * @author ikhalikov
 */
public class FormsInfoElementReader implements ConfigElementReader{

	@Override
	public ConfigElement parse(Element element) {
		if(element == null) {
			return null;
		}

		FormsInfoConfigElement result = new FormsInfoConfigElement();
		FormTypeElementReader typeReader = new FormTypeElementReader();
		FormLayoutElementReader layoutReader = new FormLayoutElementReader();

		for(Object obj : element.elements(FORM_TYPE_ELEMENT_ID)) {
			FormTypeConfigElement type = (FormTypeConfigElement) typeReader.parse((Element) obj);
			result.addFormType(type);
		}
		for(Object obj : element.elements(FORM_LAYOUT_ID)){
			FormLayoutConfigElement layout = (FormLayoutConfigElement) layoutReader.parse((Element) obj);
			result.addFormLayout(layout);
		}

		return result;
	}

}
