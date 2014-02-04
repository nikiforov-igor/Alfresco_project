package ru.it.lecm.arm.scripts;

import org.alfresco.repo.jscript.ScriptNode;
import org.alfresco.service.cmr.repository.NodeRef;
import ru.it.lecm.arm.beans.ArmService;
import ru.it.lecm.base.beans.BaseWebScript;

/**
 * User: AIvkin
 * Date: 04.02.14
 * Time: 10:35
 */
public class ArmWebScriptBean extends BaseWebScript {
	private ArmService armService;

	public void setArmService(ArmService armService) {
		this.armService = armService;
	}

	public ScriptNode getDictionaryArmSettings() {
		NodeRef dictionary = armService.getDictionaryArmSettings();

		return (dictionary == null) ? null : new ScriptNode(dictionary, serviceRegistry, getScope());
	}
}
