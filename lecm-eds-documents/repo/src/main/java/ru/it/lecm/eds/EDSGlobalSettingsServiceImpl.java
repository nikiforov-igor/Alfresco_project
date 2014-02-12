package ru.it.lecm.eds;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.alfresco.model.ContentModel;
import org.alfresco.service.cmr.repository.AssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.alfresco.util.GUID;
import ru.it.lecm.base.beans.BaseBean;
import ru.it.lecm.dictionary.beans.DictionaryBean;
import ru.it.lecm.eds.api.EDSGlobalSettingsService;
import ru.it.lecm.orgstructure.beans.OrgstructureBean;

/**
 *
 * @author dbayandin
 */
public class EDSGlobalSettingsServiceImpl extends BaseBean implements EDSGlobalSettingsService {

	private Map<String, Map<String, NodeRef>> potentialRolesMap;
		
	private OrgstructureBean orgstructureService;
    private NamespaceService namespaceService;
	private DictionaryBean dictionaryService;
	
	public void setOrgstructureService(OrgstructureBean orgstructureService) {
        this.orgstructureService = orgstructureService;
    }

    public void setNamespaceService(NamespaceService namespaceService) {
        this.namespaceService = namespaceService;
    }
	
	public void setDictionaryService(DictionaryBean dictionaryService) {
        this.dictionaryService = dictionaryService;
    }
	
	@Override
	public NodeRef getServiceRootFolder() {
		return getFolder(GLOBAL_EDS_SETTINGS_FOLDER_ID);
	}
	
	public void init() {
		this.potentialRolesMap = new HashMap<String, Map<String, NodeRef>>();
		
		NodeRef potentialRolesDictionary = dictionaryService.getDictionaryByName(POTENTIAL_ROLES_DICTIONARY_NAME);
		List<NodeRef> potentialRolesRefs = dictionaryService.getChildren(potentialRolesDictionary);
		for (NodeRef potentialRoleRef : potentialRolesRefs) {
			Serializable businessRole = nodeService.getProperty(potentialRoleRef, PROP_POTENTIAL_ROLE_BUSINESS_ROLE_REF);
			Serializable organizationElement = nodeService.getProperty(potentialRoleRef, PROP_POTENTIAL_ROLE_ORG_ELEMENT_REF);
			if (businessRole != null && organizationElement != null) {
				updatePotentialRolesMap(businessRole.toString(), organizationElement.toString(), potentialRoleRef);
			}
		}
	}
	
	private void updatePotentialRolesMap(String businessRoleId, String organizationElementStrRef, NodeRef potentialRoleRef) {
		Map<String, NodeRef> orgElementRoles = this.potentialRolesMap.containsKey(businessRoleId) ? 
			this.potentialRolesMap.get(businessRoleId) : 
			new HashMap<String, NodeRef>();
		orgElementRoles.put(organizationElementStrRef, potentialRoleRef);

		this.potentialRolesMap.put(businessRoleId, orgElementRoles);
	}
	
	public Collection<NodeRef> getPotentialWorkers(String businessRoleId, NodeRef organizationElementRef) {
		if (businessRoleId == null || organizationElementRef == null) {
			return null;
		}
		NodeRef businessRoleRef = orgstructureService.getBusinessRoleByIdentifier(businessRoleId);
		return getPotentialWorkers(businessRoleRef, organizationElementRef);
	}
	
	public Collection<NodeRef> getPotentialWorkers(NodeRef businessRoleRef, NodeRef organizationElementRef) {
		if (businessRoleRef == null || organizationElementRef == null) {
			return null;
		}
		Set<NodeRef> result = new HashSet<NodeRef>();
				
		Map<String, NodeRef> orgElementRoles = this.potentialRolesMap.containsKey(businessRoleRef.toString()) ? 
			this.potentialRolesMap.get(businessRoleRef.toString()) : 
			new HashMap<String, NodeRef>();
		
		if (orgElementRoles.containsKey(organizationElementRef.toString())) {
			NodeRef potentialRoleRef = orgElementRoles.get(organizationElementRef.toString());
			List<AssociationRef> employeeAssocRefs = nodeService.getTargetAssocs(potentialRoleRef, ASSOC_POTENTIAL_ROLE_EMPLOYEE);
			for (AssociationRef employeeAssocRef : employeeAssocRefs) {
				NodeRef employeeRef = employeeAssocRef.getTargetRef();
				if (employeeRef != null) result.add(employeeRef);
			}
		}
		return result;
	}

	@Override
	public void savePotentialWorkers(String businessRoleId, NodeRef orgElementRef, List<NodeRef> employeesRefs) {
		if (businessRoleId == null || orgElementRef == null) {
			return;
		}
		NodeRef businessRoleRef = orgstructureService.getBusinessRoleByIdentifier(businessRoleId);
		savePotentialWorkers(businessRoleRef, orgElementRef, employeesRefs);
	}
	
	@Override
	public void savePotentialWorkers(NodeRef businessRoleRef, NodeRef orgElementRef, List<NodeRef> employeesRefs) {
		if (businessRoleRef == null || orgElementRef == null) {
			return;
		}
		Map<String, NodeRef> orgElementRoles = this.potentialRolesMap.containsKey(businessRoleRef.toString()) ? 
			this.potentialRolesMap.get(businessRoleRef.toString()) : 
			new HashMap<String, NodeRef>();
		
		if (orgElementRoles.containsKey(orgElementRef.toString())) {
			updatePotentialRole(orgElementRoles.get(orgElementRef.toString()), employeesRefs);
		} else {
			createPotentialRole(businessRoleRef, orgElementRef, employeesRefs);
		}
	}
	
	public NodeRef updatePotentialRole(NodeRef potentialRoleRef, List<NodeRef> employeesRefs) {
		List<NodeRef> unchangedEmployees = new ArrayList<NodeRef>();
		List<AssociationRef> employeeAssocRefs = nodeService.getTargetAssocs(potentialRoleRef, ASSOC_POTENTIAL_ROLE_EMPLOYEE);
		for (AssociationRef employeeAssocRef : employeeAssocRefs) {
			NodeRef employeeRef = employeeAssocRef.getTargetRef();
			//check for removing
			if (employeeRef != null) {
				if (!employeesRefs.contains(employeeRef)) {
					//removing
					nodeService.removeAssociation(potentialRoleRef, employeeRef, ASSOC_POTENTIAL_ROLE_EMPLOYEE);
				} else {
					unchangedEmployees.add(employeeRef);
				}
			}
		}
		for (NodeRef employeeRef : employeesRefs) {
			//check for adding
			if (!unchangedEmployees.contains(employeeRef)) {
				//adding
				nodeService.createAssociation(potentialRoleRef, employeeRef, ASSOC_POTENTIAL_ROLE_EMPLOYEE);
			}
		}
		return potentialRoleRef;
	}
	
	public NodeRef createPotentialRole(NodeRef businessRoleRef, NodeRef orgElementRef, List<NodeRef> employeesRefs) {
		if (employeesRefs.isEmpty() || nodeService.getType(orgElementRef).equals(orgstructureService.TYPE_ORGANIZATION)) {
			return null;
		}
		NodeRef potentialRolesDictionary = dictionaryService.getDictionaryByName(POTENTIAL_ROLES_DICTIONARY_NAME);
		NodeRef potentialRoleRef = nodeService.createNode(
			potentialRolesDictionary, 
			ContentModel.ASSOC_CONTAINS,
			QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, GUID.generate()), 
			TYPE_POTENTIAL_ROLE).getChildRef();
		
		nodeService.createAssociation(potentialRoleRef, businessRoleRef, ASSOC_POTENTIAL_ROLE_BUSINESS_ROLE);
		nodeService.createAssociation(potentialRoleRef, orgElementRef, ASSOC_POTENTIAL_ROLE_ORGANIZATION_ELEMENT);
		
		for (NodeRef employeeRef : employeesRefs) { 
			nodeService.createAssociation(potentialRoleRef, employeeRef, ASSOC_POTENTIAL_ROLE_EMPLOYEE);
		}
		
		updatePotentialRolesMap(businessRoleRef.toString(), orgElementRef.toString(), potentialRoleRef);
		return potentialRoleRef;
	}
}
