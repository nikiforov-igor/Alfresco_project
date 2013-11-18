/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ru.it.lecm.base.formsConfig.elements.controlElement;

import java.util.HashMap;
import java.util.Map;
import org.springframework.extensions.config.ConfigElement;
import org.springframework.extensions.config.element.ConfigElementAdapter;
import static ru.it.lecm.base.formsConfig.Constants.*;
/**
 *
 * @author ikhalikov
 */
public class ControlConfigElement extends ConfigElementAdapter{

	private String id;
	private String displayName;
	private String templatePath;
	private Map<String, ParamConfigElement> params = new HashMap<String, ParamConfigElement>();

	public String getDisplayName() {
		return displayName;
	}

	public String getTemplatePath() {
		return templatePath;
	}

	public void addParam(ParamConfigElement el){
		this.params.put(el.getId(), el);
	}

	public Map<String, ParamConfigElement> getParamsMap() {
		return params;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setTemplatePath(String templatePath) {
		this.templatePath = templatePath;
	}

	public void setParams(Map<String, ParamConfigElement> params) {
		this.params = params;
	}

	public ControlConfigElement() {
		super(CONTROL_ELEMENT_ID);
	}

	public ControlConfigElement(String name) {
		super(name);
	}

	@Override
	public ConfigElement combine(ConfigElement configElement) {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

}
