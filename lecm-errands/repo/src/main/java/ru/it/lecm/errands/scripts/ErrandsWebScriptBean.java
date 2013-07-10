package ru.it.lecm.errands.scripts;

import org.alfresco.repo.jscript.ScriptNode;
import ru.it.lecm.base.beans.BaseWebScript;
import ru.it.lecm.errands.ErrandsService;

/**
 * User: AIvkin
 * Date: 10.07.13
 * Time: 11:56
 */
public class ErrandsWebScriptBean extends BaseWebScript {
	ErrandsService errandsService;

	public void setErrandsService(ErrandsService errandsService) {
		this.errandsService = errandsService;
	}

	public ScriptNode getSettingsNode() {
		return new ScriptNode(errandsService.getSettingsNode(), serviceRegistry, getScope());
	}
}
