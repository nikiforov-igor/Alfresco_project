/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ru.it.lecm.base.formsConfig.elements.fieldElement;

import org.dom4j.Element;
import org.springframework.extensions.config.ConfigElement;
import org.springframework.extensions.config.xml.elementreader.ConfigElementReader;
import ru.it.lecm.base.formsConfig.elements.controlElement.ControlConfigElement;
import ru.it.lecm.base.formsConfig.elements.controlElement.ControlElementReader;
import static ru.it.lecm.base.formsConfig.Constants.*;

/**
 * Ридер для элементов <field-type>
 *
 * @author ikhalikov
 */
public class TypeElementReader implements ConfigElementReader{

	@Override
	public ConfigElement parse(Element element) {
		TypeConfigElement result = new TypeConfigElement();
		if (element == null){
			return null;
		}

		result.setId(element.attributeValue(ATTR_ID));
		result.setLocalName(element.attributeValue(ATTR_LOCAL_NAME));

		ControlElementReader reader = new ControlElementReader();
		for(Object obj : element.elements(CONTROL_ELEMENT_ID)){
			ControlConfigElement controlElement = new ControlConfigElement();
			controlElement = (ControlConfigElement) reader.parse((Element) obj);
			result.addControl(controlElement);
		}
		return result;
	}

}
