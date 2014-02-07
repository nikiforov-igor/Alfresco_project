package ru.it.lecm.eds;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.alfresco.service.cmr.repository.AssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.NamespaceService;
import ru.it.lecm.base.beans.BaseBean;
import ru.it.lecm.dictionary.beans.DictionaryBean;
import ru.it.lecm.eds.api.EDSGlobalSettingsService;
import ru.it.lecm.orgstructure.beans.OrgstructureBean;

/**
 *
 * @author dbayandin
 */
public class EDSGlobalSettingsServiceImpl extends BaseBean implements EDSGlobalSettingsService {

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
		
		NodeRef potentialRolesDictionary = dictionaryService.getDictionaryByName(POTENTIAL_ROLES_DICTIONARY_NAME);
		List<NodeRef> potentialRolesRefs = dictionaryService.getChildren(potentialRolesDictionary);
		for (NodeRef potentialRoleRef : potentialRolesRefs) {
			Serializable businessRole = nodeService.getProperty(potentialRoleRef, PROP_POTENTIAL_ROLE_BUSINESS_ROLE_REF);
			Serializable organizationElement = nodeService.getProperty(potentialRoleRef, PROP_POTENTIAL_ROLE_ORG_ELEMENT_REF);
			if (businessRole != null && businessRole.toString().contains(businessRoleRef.toString()) && 
				organizationElement != null && organizationElement.toString().contains(organizationElementRef.toString())) {
				List<AssociationRef> employeeAssocRefs = nodeService.getTargetAssocs(potentialRoleRef, ASSOC_POTENTIAL_ROLE_EMPLOYEE);
				for (AssociationRef employeeAssocRef : employeeAssocRefs) {
					NodeRef employeeRef = employeeAssocRef.getTargetRef();
					if (employeeRef != null) result.add(employeeRef);
				}
			}
		}
		return result;
	}
	
	
}
