package ru.it.lecm.eds.scripts;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import org.alfresco.repo.jscript.ScriptNode;
import org.alfresco.service.cmr.repository.AssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.json.JSONException;
import org.json.JSONObject;
import org.mozilla.javascript.Scriptable;
import ru.it.lecm.base.beans.BaseWebScript;
import ru.it.lecm.eds.api.EDSDocumentService;
import ru.it.lecm.eds.api.EDSGlobalSettingsService;
import ru.it.lecm.orgstructure.beans.OrgstructureBean;

/**
 *
 * @author dbayandin
 */
public class EDSGlobalSettingsWebScriptBean extends BaseWebScript {

	private EDSGlobalSettingsService edsGlobalSettingsService;
	private OrgstructureBean orgstructureService;
	private NodeService nodeService;

	public void setNodeService(NodeService nodeService) {
		this.nodeService = nodeService;
	}

	public void setEdsGlobalSettingsService(EDSGlobalSettingsService edsGlobalSettingsService) {
		this.edsGlobalSettingsService = edsGlobalSettingsService;
	}

	public void setOrgstructureService(OrgstructureBean orgstructureService) {
		this.orgstructureService = orgstructureService;
	}

	public Scriptable getPotentialWorkers(String businessRoleId, String organizationElementStrRef) {
		if (organizationElementStrRef == null || !NodeRef.isNodeRef(organizationElementStrRef)
				|| businessRoleId == null || businessRoleId.isEmpty()) {
			return null;
		}
		NodeRef organizationElementRef = new NodeRef(organizationElementStrRef);
		Collection<NodeRef> result = edsGlobalSettingsService.getPotentialWorkers(businessRoleId, organizationElementRef);
		return createScriptable(new ArrayList(result));
	}

	public Scriptable getPotentialWorkers(ScriptNode businessRoleRef, ScriptNode organizationElementRef) {
		Collection<NodeRef> potentialWorkers = edsGlobalSettingsService.getPotentialWorkers(businessRoleRef.getNodeRef(), organizationElementRef.getNodeRef());
		return createScriptable(new ArrayList(potentialWorkers));
	}

	public Scriptable getPotentialWorkers(String businessRoleId, ScriptNode organizationElementRef) {
		Collection<NodeRef> potentialWorkers = edsGlobalSettingsService.getPotentialWorkers(businessRoleId, organizationElementRef.getNodeRef());
		return createScriptable(new ArrayList(potentialWorkers));
	}

	public Scriptable getRegistrars(final String businessRoleId, final ScriptNode ordRef) {
		//получаем текущего пользователя
		NodeRef currentEmployee = orgstructureService.getCurrentEmployee();
		// централизованная ли регистрация
		List<NodeRef> registrars = edsGlobalSettingsService.getRegistras(currentEmployee, businessRoleId);
		return createScriptable(registrars);
	}

	public void setRegistrar(final ScriptNode docNode, final ScriptNode employee) {
		NodeRef docRef = docNode.getNodeRef();
		NodeRef employeeRef = employee.getNodeRef();
		List<AssociationRef> assocs = nodeService.getTargetAssocs(docRef, EDSDocumentService.ASSOC_REGISTRARS);
		if (assocs.isEmpty()) {
			nodeService.createAssociation(docRef, employeeRef, EDSDocumentService.ASSOC_REGISTRARS);
		} else {
//			String presentString = (String) nodeService.getProperty(docRef, DocumentService.PROP_PRESENT_STRING);
//			String shortname = (String) nodeService.getProperty(employeeRef, OrgstructureBean.PROP_EMPLOYEE_SHORT_NAME);
//			String template = "Association between document %s[%s] and registrar %s[%s] already exists!";
//			logger.error(String.format(template, ordRef.toString(), presentString, employeeRef.toString(), shortname));
		}
	}

	public void savePotentialWorkers(String businessRoleId, JSONObject employeesJsonMap) throws JSONException {
		if (businessRoleId == null || employeesJsonMap == null) {
			return;
		}
		Iterator orgElementIterator = employeesJsonMap.keys();
		while (orgElementIterator.hasNext()) {
			String orgElementStrRef = (String) orgElementIterator.next();
			if (orgElementStrRef == null || !NodeRef.isNodeRef(orgElementStrRef)) {
				continue;
			}

			NodeRef orgElementRef = new NodeRef(orgElementStrRef);
			List<NodeRef> employeesRefs = new ArrayList<NodeRef>();
			JSONObject employees = employeesJsonMap.getJSONObject(orgElementStrRef);
			if (employees != null) {
				Iterator employeeIterator = employees.keys();
				while (employeeIterator.hasNext()) {
					String employeeStrRef = (String) employeeIterator.next();
					if (employeeStrRef == null || !NodeRef.isNodeRef(employeeStrRef)) {
						continue;
					}

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
