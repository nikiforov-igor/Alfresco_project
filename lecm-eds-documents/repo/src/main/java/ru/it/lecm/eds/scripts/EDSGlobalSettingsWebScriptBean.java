package ru.it.lecm.eds.scripts;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.alfresco.service.cmr.repository.NodeRef;
import org.mozilla.javascript.Scriptable;
import ru.it.lecm.base.beans.BaseWebScript;
import ru.it.lecm.eds.api.EDSGlobalSettingsService;


/**
 *
 * @author dbayandin
 */
public class EDSGlobalSettingsWebScriptBean extends BaseWebScript{
	
	private EDSGlobalSettingsService edsGlobalSettingsService;

    public void setEdsGlobalSettingsService(EDSGlobalSettingsService edsGlobalSettingsService) {
        this.edsGlobalSettingsService = edsGlobalSettingsService;
    }
	
	public Scriptable getPotentialWorkers(String businessRoleId, String organizationElementStrRef) {
		if (organizationElementStrRef == null || businessRoleId == null) {
			return null;
		}
		NodeRef organizationElementRef = new NodeRef(organizationElementStrRef);
		Collection<NodeRef> result = edsGlobalSettingsService.getPotentialWorkers(businessRoleId, organizationElementRef);
		return createScriptable(new ArrayList(result));
	}
}
