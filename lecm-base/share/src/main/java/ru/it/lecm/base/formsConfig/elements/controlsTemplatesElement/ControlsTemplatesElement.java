package ru.it.lecm.base.formsConfig.elements.controlsTemplatesElement;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.extensions.config.ConfigElement;
import org.springframework.extensions.config.element.ConfigElementAdapter;
import static ru.it.lecm.base.formsConfig.Constants.CONTROLS_TEMPLATES_ELEMENT_ID;
import ru.it.lecm.base.formsConfig.elements.controlElement.ControlConfigElement;

/**
 *
 * @author vmalygin
 */
public class ControlsTemplatesElement extends ConfigElementAdapter {

	private final static Log logger = LogFactory.getLog(ControlsTemplatesElement.class);

	private final Map<String, ControlConfigElement> controls = new HashMap<>();

	public ControlsTemplatesElement() {
		this(CONTROLS_TEMPLATES_ELEMENT_ID);
	}

	public ControlsTemplatesElement(String name) {
		super(name);
	}

	public ControlConfigElement addTemplate(ControlConfigElement controlTemplate) {
		return controls.put(controlTemplate.getTemplatePath(), controlTemplate);
	}

	public Map<String, ControlConfigElement> getTemplates() {
		return controls;
	}

	public Collection<ControlConfigElement> getTemplatesCollection() {
		return controls.values();
	}

	public ControlConfigElement getTemplate(final String templatePath) {
		return controls.get(templatePath);
	}

	/*
	 Так как слияние контролов не планируется, то и метод пустой
	 */
	@Override
	public ConfigElement combine(ConfigElement configElement) {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

}
