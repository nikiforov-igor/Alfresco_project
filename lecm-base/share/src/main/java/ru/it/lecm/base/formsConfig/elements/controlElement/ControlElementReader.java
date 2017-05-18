/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ru.it.lecm.base.formsConfig.elements.controlElement;

import org.dom4j.Element;
import org.springframework.extensions.config.ConfigElement;
import org.springframework.extensions.config.xml.elementreader.ConfigElementReader;
import static ru.it.lecm.base.formsConfig.Constants.*;


/**
 * Ридер для элемента <control>
 *
 * @author ikhalikov
 */
public class ControlElementReader implements ConfigElementReader{

	@Override
	public ConfigElement parse(Element element) {

		ControlConfigElement result = new ControlConfigElement();
		if (element == null){
			return null;
		}

		result.setId(element.attributeValue(ATTR_ID));
		result.setDisplayName(element.attributeValue(ATTR_LOCAL_NAME));
		result.setTemplatePath(element.attributeValue(ATTR_TEMPLATE));
		result.setDefaultControl(Boolean.parseBoolean(element.attributeValue(ATTR_DEFAULT)));

		for(Object obj : element.elements(PARAM_ELEMENT_ID)) {
			Element param = (Element) obj;
			ParamConfigElement paramObject = new ParamConfigElement();
			paramObject.setId(param.attributeValue(ATTR_ID));
			paramObject.setLocalName(param.attributeValue(ATTR_LOCAL_NAME));
			paramObject.setMandatory(Boolean.parseBoolean(param.attributeValue(ATTR_MANDATORY)));
			String visibleValue = param.attributeValue(ATTR_VISIBLE);
			paramObject.setVisible(visibleValue == null || Boolean.parseBoolean(visibleValue));
			paramObject.setValue((String) param.getData());
			paramObject.setDescription(param.attributeValue(ATTR_DESCRIPTION));
			result.addParam(paramObject);
		}

		return result;

	}

}
