/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ru.it.lecm.base.formsConfig.elements.formLayoutElement;

import org.dom4j.Element;
import org.springframework.extensions.config.ConfigElement;
import org.springframework.extensions.config.xml.elementreader.ConfigElementReader;
import static ru.it.lecm.base.formsConfig.Constants.*;

/**
 * Ридер для элемента <form-layout>
 *
 * @author ikhalikov
 */
public class FormLayoutElementReader implements ConfigElementReader{

	@Override
	public ConfigElement parse(Element element) {
		if(element == null){
			return null;
		}

		FormLayoutConfigElement result = new FormLayoutConfigElement();

		result.setLocalName(element.attributeValue(ATTR_LOCAL_NAME));
		result.setTemplate(element.attributeValue(ATTR_TEMPLATE));

		return result;
	}

}
