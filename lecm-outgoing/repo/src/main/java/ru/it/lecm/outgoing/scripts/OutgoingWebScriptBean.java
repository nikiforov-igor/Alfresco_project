package ru.it.lecm.outgoing.scripts;

import java.util.*;
import org.alfresco.repo.jscript.ScriptNode;
import ru.it.lecm.base.beans.BaseWebScript;
import ru.it.lecm.outgoing.api.OutgoingService;

/**
 * User: dbayandin
 * Date: 30.01.14
 * Time: 11:56
 */
public class OutgoingWebScriptBean extends BaseWebScript {
    private OutgoingService outgoingService;
	
	public void setOutgoingService(OutgoingService outgoingService) {
		this.outgoingService = outgoingService;
	}
	
	public ScriptNode getSettingsNode() {
		return new ScriptNode(outgoingService.getSettingsNode(), serviceRegistry, getScope());
	}
}
