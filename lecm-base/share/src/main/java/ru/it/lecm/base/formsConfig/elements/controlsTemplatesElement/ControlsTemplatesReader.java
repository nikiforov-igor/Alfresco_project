package ru.it.lecm.base.formsConfig.elements.controlsTemplatesElement;

import org.dom4j.Element;
import org.springframework.extensions.config.ConfigElement;
import org.springframework.extensions.config.xml.elementreader.ConfigElementReader;
import static ru.it.lecm.base.formsConfig.Constants.CONTROL_ELEMENT_ID;
import ru.it.lecm.base.formsConfig.elements.controlElement.ControlConfigElement;
import ru.it.lecm.base.formsConfig.elements.controlElement.ControlElementReader;

/**
 *
 * @author vmalygin
 */
public class ControlsTemplatesReader implements ConfigElementReader {

	@Override
	public ConfigElement parse(Element element) {
		if (element == null) {
			return null;
		}

		ControlElementReader reader = new ControlElementReader();
		ControlsTemplatesElement result = new ControlsTemplatesElement();
		for (Object obj : element.elements(CONTROL_ELEMENT_ID)) {
			ControlConfigElement controlTemplate = (ControlConfigElement) reader.parse((Element) obj);
			result.addTemplate(controlTemplate);
		}
		return result;
	}
}
