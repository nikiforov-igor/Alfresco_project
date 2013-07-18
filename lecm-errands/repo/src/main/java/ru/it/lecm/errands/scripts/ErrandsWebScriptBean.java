package ru.it.lecm.errands.scripts;

import org.alfresco.repo.jscript.ScriptNode;
import org.alfresco.service.cmr.repository.NodeRef;
import ru.it.lecm.base.beans.BaseWebScript;
import ru.it.lecm.errands.ErrandsService;

import java.util.List;

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

	public ScriptNode getCurrentUserSettingsNode() {
		return new ScriptNode(errandsService.getCurrentUserSettingsNode(), serviceRegistry, getScope());
	}

	public List<NodeRef> getAvailableInitiators() {
		return  errandsService.getAvailableInitiators();
	}

	public boolean isDefaultWithoutInitiatorApproval() {
		return  errandsService.isDefaultWithoutInitiatorApproval();
	}

	public NodeRef getDefaultInitiator() {
		return  errandsService.getDefaultInitiator();
	}

	public NodeRef getDefaultSubject() {
		return  errandsService.getDefaultSubject();
	}

	public void requestDueDateChange() {
		errandsService.requestDueDateChange();
	}
}
