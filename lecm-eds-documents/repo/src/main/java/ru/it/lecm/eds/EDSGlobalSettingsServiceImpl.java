package ru.it.lecm.eds;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.transaction.RetryingTransactionHelper;
import org.alfresco.service.cmr.repository.AssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.alfresco.util.GUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.it.lecm.base.beans.BaseBean;
import ru.it.lecm.base.beans.WriteTransactionNeededException;
import ru.it.lecm.dictionary.beans.DictionaryBean;
import ru.it.lecm.documents.beans.DocumentGlobalSettingsService;
import ru.it.lecm.eds.api.EDSGlobalSettingsService;
import ru.it.lecm.orgstructure.beans.OrgstructureBean;

import java.io.Serializable;
import java.util.*;

/**
 *
 * @author dbayandin
 */
public class EDSGlobalSettingsServiceImpl extends BaseBean implements EDSGlobalSettingsService {
	private static final transient Logger logger = LoggerFactory.getLogger(EDSGlobalSettingsServiceImpl.class);

	private Map<String, Map<String, NodeRef>> potentialRolesMap;

	private OrgstructureBean orgstructureService;
    private NamespaceService namespaceService;
	private DictionaryBean dictionaryService;
	private DocumentGlobalSettingsService documentGlobalSettingsService;

	public void setOrgstructureService(OrgstructureBean orgstructureService) {
        this.orgstructureService = orgstructureService;
    }

    public void setNamespaceService(NamespaceService namespaceService) {
        this.namespaceService = namespaceService;
    }

	public void setDictionaryService(DictionaryBean dictionaryService) {
        this.dictionaryService = dictionaryService;
    }

	public void setDocumentGlobalSettingsService(DocumentGlobalSettingsService documentGlobalSettingsService) {
		this.documentGlobalSettingsService = documentGlobalSettingsService;
	}
    
	@Override
	public NodeRef getServiceRootFolder() {
            return getFolder(EDS_GLOBAL_SETTINGS_FOLDER_ID);
	}

	public void init() {
		if (null == getSettingsNode()) {
			//TODO Уточнить про права. Нужно ли делать runAsSystem, при том что она и так создаётся?
			lecmTransactionHelper.doInRWTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<NodeRef>() {
				@Override
				public NodeRef execute() throws Throwable {
					return createSettingsNode();
				}
			});

		}
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
		if (businessRoleId == null || organizationElementStrRef == null || potentialRoleRef == null) {
			return;
		}

		Map<String, NodeRef> orgElementRoles = this.potentialRolesMap.containsKey(businessRoleId) ?
			this.potentialRolesMap.get(businessRoleId) :
			new HashMap<String, NodeRef>();
		orgElementRoles.put(organizationElementStrRef, potentialRoleRef);

		this.potentialRolesMap.put(businessRoleId, orgElementRoles);
	}

	@Override
	public Collection<NodeRef> getPotentialWorkers(String businessRoleId, NodeRef organizationElementRef) {
		if (businessRoleId == null || organizationElementRef == null) {
			return new HashSet<NodeRef>();
		}
		NodeRef businessRoleRef = orgstructureService.getBusinessRoleByIdentifier(businessRoleId);
		return getPotentialWorkers(businessRoleRef, organizationElementRef);
	}

	@Override
	public Collection<NodeRef> getPotentialWorkers(NodeRef businessRoleRef, NodeRef organizationElementRef) {
		Set<NodeRef> result = new HashSet<NodeRef>();

		if (businessRoleRef == null || organizationElementRef == null) {
			return result;
		}

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

        NodeRef potentialRoleRef = orgElementRoles.get(orgElementRef.toString());
        if (potentialRoleRef != null && nodeService.exists(potentialRoleRef)) {
            updatePotentialRole(potentialRoleRef, employeesRefs);
		} else {
			createPotentialRole(businessRoleRef, orgElementRef, employeesRefs);
		}
	}

	@Override
	public NodeRef updatePotentialRole(NodeRef potentialRoleRef, List<NodeRef> employeesRefs) {
		if (potentialRoleRef == null) {
			return potentialRoleRef;
		}
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

	@Override
	public NodeRef createPotentialRole(NodeRef businessRoleRef, NodeRef orgElementRef, List<NodeRef> employeesRefs) {
		if (businessRoleRef == null || orgElementRef == null || employeesRefs == null ||
			employeesRefs.isEmpty() || nodeService.getType(orgElementRef).equals(orgstructureService.TYPE_ORGANIZATION)) {
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

	@Override
	public NodeRef getSettingsNode() {
//		TODO: Метод разделён, создание вынесено в createSettingsNode
            return nodeService.getChildByName(getServiceRootFolder(), ContentModel.ASSOC_CONTAINS, EDS_GLOBAL_SETTINGS_NODE_NAME);
        }

        /**
         * создание ноды с настройками. создаётся при инициализации бина
         */
        private NodeRef createSettingsNode() throws WriteTransactionNeededException {
    //		Проверим, открыта ли транзакция
            //проверяется в createNode
//            try {
//                lecmTransactionHelper.checkTransaction();
//            } catch (TransactionNeededException ex) {
//                throw new WriteTransactionNeededException("Can't create settings node");
//            }
//            NodeRef settingsRef = getSettingsNode();
//            if (settingsRef == null) {
//                QName assocTypeQName = ContentModel.ASSOC_CONTAINS;
//                QName assocQName = QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, EDS_GLOBAL_SETTINGS_NODE_NAME);
//                QName nodeTypeQName = TYPE_SETTINGS;
//
//                Map<QName, Serializable> properties = new HashMap<QName, Serializable>(1);
//                properties.put(ContentModel.PROP_NAME, EDS_GLOBAL_SETTINGS_NODE_NAME);
//                ChildAssociationRef associationRef = nodeService.createNode(rootFolder, assocTypeQName, assocQName, nodeTypeQName, properties);
//                settingsRef = associationRef.getChildRef();
                
                //settingsRef = createNode(getServiceRootFolder(), TYPE_SETTINGS, EDS_GLOBAL_SETTINGS_NODE_NAME, null);
//            }
//            return settingsRef;
            return createNode(getServiceRootFolder(), TYPE_SETTINGS, EDS_GLOBAL_SETTINGS_NODE_NAME, null);
        }

	@Override
	public boolean isRegistrationCenralized() {
        NodeRef settings = getSettingsNode();
        if (settings != null) {
        	Boolean isRegCenralized = (Boolean) nodeService.getProperty(settings, PROP_SETTINGS_CENTRALIZED_REGISTRATION);
            return Boolean.TRUE.equals(isRegCenralized);
        }
        return false;
    }

	@Deprecated
    @Override
    public Boolean isHideProperties() {
		return documentGlobalSettingsService.isHideProperties();
    }

	@Override
    public NodeRef getArmDashletNode() {
        NodeRef settings = getSettingsNode();
        if (settings != null) {
            return findNodeByAssociationRef(settings,ASSOC_SETTINGS_ARM_DASHLET_NODE, null, ASSOCIATION_TYPE.TARGET);
        }
        return null;
    }

    @Override
    public NodeRef getArm() {
        NodeRef settings = getSettingsNode();
        if (settings != null) {
            return findNodeByAssociationRef(settings,ASSOC_SETTINGS_ARM_DASHLET, null, ASSOCIATION_TYPE.TARGET);
        }
        return null;
    }

    @Override
	public List<NodeRef> getRegistras(NodeRef employeeRef, String businessRoleId) {
		List<NodeRef> registrars;
		if (isRegistrationCenralized()) {
			registrars = orgstructureService.getEmployeesByBusinessRole(businessRoleId);
		} else {
			registrars = new ArrayList<NodeRef>();
			//получаем основную должностную позицию
			NodeRef primaryStaff = orgstructureService.getEmployeePrimaryStaff(employeeRef);
			if (primaryStaff != null) {
				NodeRef unit = orgstructureService.getUnitByStaff(primaryStaff);
				registrars.addAll(getPotentialWorkers(businessRoleId, unit));
			}
		}
		return registrars;
	}

	@Deprecated
	@Override
	public String getLinksViewMode() {
		return documentGlobalSettingsService.getLinksViewMode();
	}
}
