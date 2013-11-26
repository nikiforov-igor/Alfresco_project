/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ru.it.lecm.base.formsConfig.elements.controlElement;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import org.codehaus.jackson.annotate.JsonProperty;
import org.springframework.extensions.config.ConfigElement;
import org.springframework.extensions.config.element.ConfigElementAdapter;
import static ru.it.lecm.base.formsConfig.Constants.*;
/**
 * POJO для элемента <control>
 *
 * @author ikhalikov
 */
public class ControlConfigElement extends ConfigElementAdapter{
	@JsonProperty
	private String id;
	@JsonProperty
	private String parent;

	public String getParent() {
		return parent;
	}

	public void setParent(String parent) {
		this.parent = parent;
	}
	@JsonProperty
	private String displayName;
	@JsonProperty
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
	@JsonProperty("params")
	public Collection<ParamConfigElement> getParamsList(){
		return this.params.values();
	}

	public Map<String, ParamConfigElement> getParams() {
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

	/*
	Так как слияние контролов не планируется, то и метод пустой
	*/
	@Override
	public ConfigElement combine(ConfigElement configElement) {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

}
