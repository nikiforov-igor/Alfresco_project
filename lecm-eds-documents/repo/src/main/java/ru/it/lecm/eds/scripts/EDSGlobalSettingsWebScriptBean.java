package ru.it.lecm.eds.scripts;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import org.alfresco.repo.jscript.ScriptNode;
import org.alfresco.service.cmr.repository.NodeRef;
import org.json.JSONException;
import org.json.JSONObject;
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
		if (organizationElementStrRef == null || !NodeRef.isNodeRef(organizationElementStrRef) || 
			businessRoleId == null || businessRoleId.isEmpty()) {
			return null;
		}
		NodeRef organizationElementRef = new NodeRef(organizationElementStrRef);
		Collection<NodeRef> result = edsGlobalSettingsService.getPotentialWorkers(businessRoleId, organizationElementRef);
		return createScriptable(new ArrayList(result));
	}
	
	public void savePotentialWorkers(String businessRoleId, JSONObject employeesJsonMap) throws JSONException {
		if (businessRoleId == null || employeesJsonMap == null) {
			return;
		}
		Iterator orgElementIterator = employeesJsonMap.keys();
		while (orgElementIterator.hasNext()) {
			String orgElementStrRef = (String)orgElementIterator.next();
			if (orgElementStrRef == null || !NodeRef.isNodeRef(orgElementStrRef)) continue;
			
			NodeRef orgElementRef = new NodeRef(orgElementStrRef);
			List<NodeRef> employeesRefs = new ArrayList<NodeRef>();
			JSONObject employees = employeesJsonMap.getJSONObject(orgElementStrRef);
			if (employees != null) {
				Iterator employeeIterator = employees.keys();
				while (employeeIterator.hasNext()) {
					String employeeStrRef = (String)employeeIterator.next();
					if (employeeStrRef == null || !NodeRef.isNodeRef(employeeStrRef)) continue;
					
					NodeRef employeeRef = new NodeRef(employeeStrRef);
					employeesRefs.add(employeeRef);
				}
			}
			edsGlobalSettingsService.savePotentialWorkers(businessRoleId, orgElementRef, employeesRefs);
		}
	}
	
	public ScriptNode getSettingsNode() {
		return new ScriptNode(edsGlobalSettingsService.getSettingsNode(), serviceRegistry, getScope());
	}
	
	public Boolean isRegistrationCenralized() {
		return edsGlobalSettingsService.isRegistrationCenralized();
	}

    public Boolean isHidePropsForRecipients() {
        return edsGlobalSettingsService.isHideProperties();
    }
}
