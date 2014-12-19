/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.it.lecm.base.formsConfig.elements.fieldsElement;

import org.dom4j.Element;
import org.springframework.extensions.config.ConfigElement;
import org.springframework.extensions.config.xml.elementreader.ConfigElementReader;
import ru.it.lecm.base.formsConfig.elements.fieldElement.TypeConfigElement;
import ru.it.lecm.base.formsConfig.elements.fieldElement.TypeElementReader;
import static ru.it.lecm.base.formsConfig.Constants.*;


/**
 * Ридер для корневого элемента <field-types>
 *
 * @author ikhalikov
 */
public class FieldTypesReader implements ConfigElementReader {

	@Override
	public ConfigElement parse(Element element) {

		if (element == null) {
			return null;
		}

		FieldTypesConfigElement result = new FieldTypesConfigElement();

		TypeElementReader reader = new TypeElementReader();
		for (Object obj : element.elements(FIELD_TYPE_ELEMENT_ID)) {
			TypeConfigElement typeElement;
			typeElement = (TypeConfigElement) reader.parse((Element) obj);
			result.addType(typeElement);
		}
		return result;
	}

}
