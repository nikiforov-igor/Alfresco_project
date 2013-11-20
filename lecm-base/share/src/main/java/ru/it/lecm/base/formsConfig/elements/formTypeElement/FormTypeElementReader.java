/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ru.it.lecm.base.formsConfig.elements.formTypeElement;

import org.dom4j.Element;
import org.springframework.extensions.config.ConfigElement;
import org.springframework.extensions.config.xml.elementreader.ConfigElementReader;
import static ru.it.lecm.base.formsConfig.Constants.*;

/**
 * Ридер для элемента <form-type>
 *
 * @author ikhalikov
 */
public class FormTypeElementReader implements ConfigElementReader{

	@Override
	public ConfigElement parse(Element element) {

		if(element == null){
			return null;
		}

		FormTypeConfigElement result = new FormTypeConfigElement();
		result.setId(element.attributeValue(ATTR_ID));
		result.setEvaluatorType(element.attributeValue(ATTR_EVALUATOR_TYPE));
		result.setLocalName(element.attributeValue(ATTR_LOCAL_NAME));
		result.setFormId(element.attributeValue(ATTR_FORM_ID));

		return result;
	}

}
