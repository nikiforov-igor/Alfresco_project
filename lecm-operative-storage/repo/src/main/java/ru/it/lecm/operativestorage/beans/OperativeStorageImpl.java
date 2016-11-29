/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.it.lecm.operativestorage.beans;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.policy.BehaviourFilter;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.service.cmr.repository.AssociationRef;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.security.AuthorityService;
import org.alfresco.service.cmr.security.AuthorityType;
import org.alfresco.service.cmr.security.PermissionService;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.alfresco.service.namespace.RegexQNamePattern;
import org.alfresco.util.PropertyMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.it.lecm.base.beans.BaseBean;
import ru.it.lecm.base.beans.TransactionNeededException;
import ru.it.lecm.base.beans.WriteTransactionNeededException;
import ru.it.lecm.businessjournal.beans.BusinessJournalRecord;
import ru.it.lecm.businessjournal.beans.BusinessJournalService;
import ru.it.lecm.businessjournal.beans.EventCategory;
import ru.it.lecm.documents.beans.DocumentAttachmentsService;
import ru.it.lecm.documents.beans.DocumentMembersService;
import ru.it.lecm.documents.beans.DocumentService;
import ru.it.lecm.eds.api.EDSDocumentService;
import ru.it.lecm.orgstructure.beans.OrgstructureBean;
import ru.it.lecm.security.LecmPermissionService;
import ru.it.lecm.security.Types;
import ru.it.lecm.statemachine.StatemachineModel;

import java.io.Serializable;
import java.util.*;

/**
 *
 * @author ikhalikov
 */
public class OperativeStorageImpl extends BaseBean implements OperativeStorageService {

	private final static Logger logger = LoggerFactory.getLogger(OperativeStorageImpl.class);

	private LecmPermissionService lecmPermissionService;
	private OrgstructureBean orgstructureService;
	private PermissionService permissionService;
	private AuthorityService authorityService;
	private BehaviourFilter behaviourFilter;
	private DocumentMembersService documentMembersService;
	private DocumentAttachmentsService documentAttachmentsService;
	private BusinessJournalService businessJournalService;
	
	private NodeRef settingsNode;


	private final static String DOCUMENT_TEMPLATE = "Документ #mainobject полностью удален из системы";
	private final static String CASE_TEMPLATE = "Номенклатурное дело #mainobject полностью удалено из системы";
	private final static String OS_UNIT_TEMPLATE = "Раздел номенклатуры #mainobject полностью удален из системы";
	private final static String OS_YEAR_TEMPLATE = "Номенклатура дел #mainobject полностью удалена из системы";

	public void setBusinessJournalService(BusinessJournalService businessJournalService) {
		this.businessJournalService = businessJournalService;
	}

	public void setDocumentAttachmentsService(DocumentAttachmentsService documentAttachmentsService) {
		this.documentAttachmentsService = documentAttachmentsService;
	}

	public void setBehaviourFilter(BehaviourFilter behaviourFilter) {
		this.behaviourFilter = behaviourFilter;
	}

	public void setDocumentMembersService(DocumentMembersService documentMembersService) {
		this.documentMembersService = documentMembersService;
	}


	public void setAuthorityService(AuthorityService authorityService) {
		this.authorityService = authorityService;
	}

	public void setLecmPermissionService(LecmPermissionService lecmPermissionService) {
		this.lecmPermissionService = lecmPermissionService;
	}

	public void setOrgstructureService(ru.it.lecm.orgstructure.beans.OrgstructureBean orgstructureService) {
		this.orgstructureService = orgstructureService;
	}

	public void setPermissionService(PermissionService permissionService) {
		this.permissionService = permissionService;
	}
	
	@Override
	public void initService() {
		super.initService();
		if (getSettings() == null) {
			PropertyMap props = new PropertyMap();
			props.put(PROP_OPERATIVE_STORAGE_CENRALIZED, true);
			settingsNode = createNode(getOperativeStorageFolder(), TYPE_OPERATIVE_STORAGE_SETTING, OPERATIVE_STORAGE_GLOBAL_SETTING_NAME, props);				
		}
	}

	@Override
	public void cleanVisibilityList(NodeRef nodeRef) {
		if (!nodeService.exists(nodeRef)) {
			throw new RuntimeException(nodeRef.toString() + " does not exist.");
		}
		if (!TYPE_NOMENCLATURE_CASE.isMatch(nodeService.getType(nodeRef))) {
			throw new RuntimeException(nodeRef.toString() + " is not nomenclature case");
		}

		List<AssociationRef> employees = nodeService.getTargetAssocs(nodeRef, ASSOC_NOMENCLATURE_CASE_VISIBILITY_EMPLOYEE);
		for (AssociationRef employeeAssociationRef : employees) {
			nodeService.removeAssociation(employeeAssociationRef.getSourceRef(), employeeAssociationRef.getTargetRef(), ASSOC_NOMENCLATURE_CASE_VISIBILITY_EMPLOYEE);
		}

		List<AssociationRef> units = nodeService.getTargetAssocs(nodeRef, ASSOC_NOMENCLATURE_CASE_VISIBILITY_UNIT);
		for (AssociationRef unitAssociationRef : units) {
			nodeService.removeAssociation(unitAssociationRef.getSourceRef(), unitAssociationRef.getTargetRef(), ASSOC_NOMENCLATURE_CASE_VISIBILITY_UNIT);
		}

		List<AssociationRef> groups = nodeService.getTargetAssocs(nodeRef, ASSOC_NOMENCLATURE_CASE_VISIBILITY_WORK_GROUP);
		for (AssociationRef unitAssociationRef : groups) {
			nodeService.removeAssociation(unitAssociationRef.getSourceRef(), unitAssociationRef.getTargetRef(), ASSOC_NOMENCLATURE_CASE_VISIBILITY_WORK_GROUP);
		}
	}

	@Override
	public NodeRef createDocsFolder(NodeRef caseNodeRef) {
		NodeRef docFolder = getDocuemntsFolder(caseNodeRef);
		if (docFolder != null) {
			return docFolder;
		}
		QName assocQName = QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, UUID.randomUUID().toString());
		Map<QName, Serializable> properties = new HashMap<QName, Serializable>();
		properties.put(ContentModel.PROP_NAME, NOMENCLATURE_DOCS_FOLDER_NAME);
		docFolder = nodeService.createNode(caseNodeRef, ContentModel.ASSOC_CONTAINS, assocQName, ContentModel.TYPE_FOLDER, properties).getChildRef();
		permissionService.setInheritParentPermissions(docFolder, false);
		return docFolder;
	}

	@Override
	public NodeRef createReferencesFolder(NodeRef caseNodeRef) {
		NodeRef referencesFolder = getReferencesFolder(caseNodeRef);
		if (referencesFolder != null) {
			return referencesFolder;
		}
		QName assocQName = QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, UUID.randomUUID().toString());
		Map<QName, Serializable> properties = new HashMap<QName, Serializable>();
		properties.put(ContentModel.PROP_NAME, NOMENCLATURE_REFERENCES_FOLDER_NAME);
		referencesFolder = nodeService.createNode(caseNodeRef, ContentModel.ASSOC_CONTAINS, assocQName, ContentModel.TYPE_FOLDER, properties).getChildRef();
		return referencesFolder;
	}

	@Override
	public NodeRef getDocuemntsFolder(NodeRef caseNodeRef) {
		return nodeService.getChildByName(caseNodeRef, ContentModel.ASSOC_CONTAINS, NOMENCLATURE_DOCS_FOLDER_NAME);
	}

	@Override
	public NodeRef getNomenclatureFolder() {
		return nodeService.getChildByName(getOperativeStorageFolder(), ContentModel.ASSOC_CONTAINS, NOMENCLATURE_FOLDER_NAME);
	}

	@Override
	public NodeRef getOperativeStorageFolder() {
		return getFolder(OPERATIVE_STORAGE_FOLDER_ID);
	}

	@Override
	public NodeRef getReferenceTemplate(NodeRef caseNodeRef) {
		NodeRef service = getServiceRootFolder();
		return nodeService.getChildByName(service, ContentModel.ASSOC_CONTAINS, NOMENCLATURE_REFERENCE_TEMPLATE_NAME);
	}

	@Override
	public NodeRef getReferencesFolder(NodeRef caseNodeRef) {
		return nodeService.getChildByName(caseNodeRef, ContentModel.ASSOC_CONTAINS, NOMENCLATURE_REFERENCES_FOLDER_NAME);
	}

	@Override
	public NodeRef getServiceRootFolder() {
		return getOperativeStorageFolder();
	}

	@Override
	public NodeRef getYearSection(NodeRef nodeRef) {
		NodeRef parent = nodeRef;
		while (parent != null) {
			if (TYPE_NOMENCLATURE_YEAR_SECTION.equals(nodeService.getType(parent))) {
				return parent;
			}
			parent = nodeService.getParentAssocs(parent).get(0).getParentRef();
		}
		return null;
	}

	@Override
	public void grantAll(NodeRef nodeRef) {
		NodeRef mainOrgUnit = orgstructureService.getRootUnit();

		if (!nodeService.exists(nodeRef)) {
			throw new RuntimeException(nodeRef.toString() + " does not exist.");
		}
		if (!TYPE_NOMENCLATURE_CASE.isMatch(nodeService.getType(nodeRef))) {
			throw new RuntimeException(nodeRef.toString() + " is not nomenclature case");
		}

		NodeRef docFolder = getDocuemntsFolder(nodeRef);

//		grantPermToUnit(docFolder, mainOrgUnit);

		nodeService.createAssociation(nodeRef, mainOrgUnit, ASSOC_NOMENCLATURE_CASE_VISIBILITY_UNIT);
	}

	public boolean isShared(NodeRef docFolderRef) {
		List<ChildAssociationRef> assocList = nodeService.getParentAssocs(docFolderRef);
		if (assocList != null && !assocList.isEmpty()) {
			NodeRef caseRef = assocList.get(0).getParentRef();
			return (boolean) nodeService.getProperty(caseRef, PROP_NOMENCLATURE_CASE_IS_SHARED);
		}
		return false;
	}

	@Override
	public void grantPermToEmployee(NodeRef nodeRef, NodeRef employee) {
		LecmPermissionService.LecmPermissionGroup pgGranting = lecmPermissionService.findPermissionGroup(DEFAULT_GRANTED_ROLE);
		lecmPermissionService.grantAccess(pgGranting, nodeRef, employee);
	}

	@Override
	public void grantPermToUnit(NodeRef nodeRef, NodeRef unit, boolean isShared) {
		String auth = orgstructureService.getOrgstructureUnitAuthority(unit, isShared);
		permissionService.setPermission(nodeRef, auth, DEFAULT_GRANTED_ROLE, true);
	}

	@Override
	public void grantPermToWG(NodeRef nodeRef, NodeRef group) {
		LecmPermissionService.LecmPermissionGroup pgGranting = lecmPermissionService.findPermissionGroup(DEFAULT_GRANTED_ROLE);
		Types.SGPosition sgWG = Types.SGKind.SG_WG.getSGPos(group.getId());
		lecmPermissionService.grantAccessByPosition(pgGranting, nodeRef, sgWG);
	}

	@Override
	public void grantPermissionsToAllArchivists(NodeRef nodeRef) {
		if (!nodeService.exists(nodeRef)) {
			throw new RuntimeException(nodeRef.toString() + " does not exist.");
		}
		if (!TYPE_NOMENCLATURE_CASE.isMatch(nodeService.getType(nodeRef))) {
			throw new RuntimeException(nodeRef.toString() + " is not nomenclature case");
		}

		NodeRef docFolder = getDocuemntsFolder(nodeRef);

		final Types.SGPosition sgBusinessRole = Types.SGKind.SG_BR.getSGPos(BR_ARCHIVIST);
		String shortName = sgBusinessRole.getAlfrescoSuffix();

		String authority = authorityService.getName(AuthorityType.GROUP, shortName);
		permissionService.setPermission(nodeRef, authority, "Collaborator", true);
	}

	@Override
	public void moveDocToNomenclatureCase(NodeRef docNodeRef, NodeRef caseNodeRef) {
		List<String> folderPath = getDateFolderPath(new Date());

		Boolean isTransient = (Boolean) nodeService.getProperty(caseNodeRef, PROP_NOMENCLATURE_CASE_TRANSIENT);
		if (!Boolean.TRUE.equals(isTransient)) {
			folderPath.remove(0);
		}

		NodeRef docFolder = getDocuemntsFolder(caseNodeRef);
		try {
			docFolder = createPath(docFolder, folderPath);
		} catch (TransactionNeededException ex) {
			logger.error("Can't create folders:  " + folderPath + " in case "+ caseNodeRef +" for document " + docNodeRef);
		}

		QName assocQName = QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, UUID.randomUUID().toString());
		nodeService.moveNode(docNodeRef, docFolder, ContentModel.ASSOC_CONTAINS, assocQName);

		nodeService.setProperty(docNodeRef, PROP_IN_CASE, true);
		nodeService.createAssociation(docNodeRef, caseNodeRef, ASSOC_NOMENCLATURE_CASE);

		nodeService.addAspect(docNodeRef, DocumentService.ASPECT_DONT_MOVE_TO_ARCHIVE_FOLDER, null);
	}

	@Override
	public void moveDocToNomenclatureCase(NodeRef docNodeRef) {
		List<AssociationRef> assocList = nodeService.getTargetAssocs(docNodeRef, EDSDocumentService.ASSOC_FILE_REGISTER);
		if (assocList != null && !assocList.isEmpty()) {
			NodeRef caseRef = assocList.get(0).getTargetRef();

			NodeRef docFolder = getDocuemntsFolder(caseRef);
			QName assocQName = QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, UUID.randomUUID().toString());
			nodeService.moveNode(docNodeRef, docFolder, ContentModel.ASSOC_CONTAINS, assocQName);

			nodeService.setProperty(docNodeRef, PROP_IN_CASE, true);
			nodeService.createAssociation(docNodeRef, caseRef, ASSOC_NOMENCLATURE_CASE);

			nodeService.addAspect(docNodeRef, DocumentService.ASPECT_DONT_MOVE_TO_ARCHIVE_FOLDER, null);
		}
	}

	@Override
	public void revokeAll(NodeRef nodeRef) {
		if (!nodeService.exists(nodeRef)) {
			throw new RuntimeException(nodeRef.toString() + " does not exist.");
		}
		if (!TYPE_NOMENCLATURE_CASE.isMatch(nodeService.getType(nodeRef))) {
			throw new RuntimeException(nodeRef.toString() + " is not nomenclature case");
		}

		NodeRef docFolder = getDocuemntsFolder(nodeRef);

		List<AssociationRef> employees = nodeService.getTargetAssocs(nodeRef, ASSOC_NOMENCLATURE_CASE_VISIBILITY_EMPLOYEE);
		for (AssociationRef employeeAssociationRef : employees) {
			revokePermFromEmployee(docFolder, employeeAssociationRef.getTargetRef());
		}

		List<AssociationRef> units = nodeService.getTargetAssocs(nodeRef, ASSOC_NOMENCLATURE_CASE_VISIBILITY_UNIT);
		for (AssociationRef unitAssociationRef : units) {
			revokePermFromUnit(docFolder, unitAssociationRef.getTargetRef());
		}

		List<AssociationRef> groups = nodeService.getTargetAssocs(nodeRef, ASSOC_NOMENCLATURE_CASE_VISIBILITY_UNIT);
		for (AssociationRef unitAssociationRef : units) {
			revokePermFromWG(docFolder, unitAssociationRef.getTargetRef());
		}
	}

	@Override
	public void revokePermFromEmployee(NodeRef nodeRef, NodeRef employee) {
		LecmPermissionService.LecmPermissionGroup pgGranting = lecmPermissionService.findPermissionGroup(DEFAULT_GRANTED_ROLE);
		lecmPermissionService.revokeAccess(pgGranting, nodeRef, employee);
	}

	@Override
	public void revokePermFromUnit(NodeRef nodeRef, NodeRef unit) {
		revokePermFromUnit(nodeRef, unit, isShared(nodeRef));
	}

	@Override
	public void revokePermFromUnit(NodeRef nodeRef, NodeRef unit, boolean isShared) {
		String auth = orgstructureService.getOrgstructureUnitAuthority(unit, isShared);
		permissionService.deletePermission(nodeRef, auth, DEFAULT_GRANTED_ROLE);
	}

	@Override
	public void revokePermFromWG(NodeRef nodeRef, NodeRef group) {
		LecmPermissionService.LecmPermissionGroup pgGranting = lecmPermissionService.findPermissionGroup(DEFAULT_GRANTED_ROLE);
		Types.SGPosition sgWG = Types.SGKind.SG_WG.getSGPos(group.getId());
		lecmPermissionService.revokeAccessByPosition(pgGranting, nodeRef, sgWG);
	}

	@Override
	public void updatePermissions(NodeRef nodeRef) {

		boolean isShared = (boolean) nodeService.getProperty(nodeRef, PROP_NOMENCLATURE_CASE_IS_SHARED);

		if (!nodeService.exists(nodeRef)) {
			throw new RuntimeException(nodeRef.toString() + " does not exist.");
		}
		if (!TYPE_NOMENCLATURE_CASE.isMatch(nodeService.getType(nodeRef))) {
			throw new RuntimeException(nodeRef.toString() + " is not nomenclature case");
		}

		NodeRef docFolder = getDocuemntsFolder(nodeRef);

		//дадим права непосредственно людям
		List<AssociationRef> employees = nodeService.getTargetAssocs(nodeRef, ASSOC_NOMENCLATURE_CASE_VISIBILITY_EMPLOYEE);
		for (AssociationRef employeeAssociationRef : employees) {
			grantPermToEmployee(docFolder, employeeAssociationRef.getTargetRef());
		}

		//подразделениям
		List<AssociationRef> units = nodeService.getTargetAssocs(nodeRef, ASSOC_NOMENCLATURE_CASE_VISIBILITY_UNIT);
		for (AssociationRef unitAssociationRef : units) {
			grantPermToUnit(docFolder, unitAssociationRef.getTargetRef(), isShared);
		}

		//рабочим группам
		List<AssociationRef> workGroups = nodeService.getTargetAssocs(nodeRef, ASSOC_NOMENCLATURE_CASE_VISIBILITY_WORK_GROUP);
		for (AssociationRef wgAssociationRef : workGroups) {
			grantPermToWG(docFolder, wgAssociationRef.getTargetRef());
		}
	}

	@Override
	public NodeRef getSettings() {
		if (settingsNode == null) {
			settingsNode = nodeService.getChildByName(getOperativeStorageFolder(), ContentModel.ASSOC_CONTAINS, OPERATIVE_STORAGE_GLOBAL_SETTING_NAME);
		}
		return settingsNode;
	}

	@Override
	public boolean orgUnitAssociationExists(NodeRef ndUnitRef, NodeRef orgUnitRef) {
		List<NodeRef> allUnits = new ArrayList<>();
		NodeRef yearSection = getYearSection(ndUnitRef);
		List<ChildAssociationRef> unitSections = nodeService.getChildAssocs(yearSection, ContentModel.ASSOC_CONTAINS, RegexQNamePattern.MATCH_ALL);

		for (ChildAssociationRef unit : unitSections) {
			allUnits.addAll(getAllChildren(unit.getChildRef()));
			for (NodeRef unitSection : allUnits) {
				if (hasOrgUnit(unitSection, orgUnitRef)) {
					return true;
				}
			}
		}

		return false;
	}

	public boolean hasOrgUnit(NodeRef ndUnitRef, NodeRef orgUnitRef) {
		List<AssociationRef> assocList = nodeService.getTargetAssocs(ndUnitRef, ASSOC_NOMENCLATURE_UNIT_TO_ORGUNIT);
		if (assocList != null && !assocList.isEmpty()) {
			return orgUnitRef.equals(assocList.get(0).getTargetRef());
		}
		return false;
	}

	private List<NodeRef> getAllChildren(NodeRef nodeRef) {
		List<NodeRef> result = new ArrayList<>();
		List<ChildAssociationRef> subRes = nodeService.getChildAssocs(nodeRef, ContentModel.ASSOC_CONTAINS, RegexQNamePattern.MATCH_ALL);
		for (ChildAssociationRef child : subRes) {
			if (TYPE_NOMENCLATURE_UNIT_SECTION.equals(nodeService.getType(child.getChildRef()))) {
				result.add(child.getChildRef());
				result.addAll(getAllChildren(child.getChildRef()));
			}
		}

		return result;
	}

	@Override
	public boolean checkNDSectionAssociationExists(NodeRef orgUnitRef, NodeRef ndSectionRef) {
		NodeRef yearSection;
		if (TYPE_NOMENCLATURE_YEAR_SECTION.equals(nodeService.getType(ndSectionRef))) {
			yearSection = ndSectionRef;
		} else {
			yearSection = getYearSection(ndSectionRef);
		}

		List<AssociationRef> assocList = nodeService.getSourceAssocs(orgUnitRef, ASSOC_NOMENCLATURE_UNIT_TO_ORGUNIT);

		for (AssociationRef assoc : assocList) {
			NodeRef nomenclatureUnit = assoc.getSourceRef();
			if(Boolean.TRUE.equals(nodeService.getProperty(nomenclatureUnit, IS_ACTIVE))) {
				NodeRef assocYearSection = getYearSection(assoc.getSourceRef());
				if (assocYearSection.equals(yearSection)) {
					return true;
				}
			}
		}

		return false;

	}

	@Override
	public List<NodeRef> getOrganizationsYearSections(NodeRef organizationRef) {
		List<NodeRef> result = new ArrayList<>();
		List<NodeRef> organizations = new ArrayList<>();

		if (organizationRef != null) {
			organizations.add(organizationRef);
		} else {
			//Если не передали организацию, то возьмём все организации текущего сотрудника
			organizations = orgstructureService.getCurrentEmployeeHighestUnits();
		}

		for (NodeRef organization : organizations) {
			List<AssociationRef> assocList = nodeService.getSourceAssocs(organization, ASSOC_NOMENCLATURE_LINKED_ORG);

			for (AssociationRef assoc : assocList) {
				result.add(assoc.getSourceRef());
			}

		}

		return result;

	}

	@Override
	public void createSectionByUnit(NodeRef unitRef, NodeRef root, boolean fuckingDeepCopy) {
		Map<QName, Serializable> props = new HashMap<>();
		Map<QName, Serializable> unitProps = nodeService.getProperties(unitRef);

		props.put(ContentModel.PROP_TITLE, unitProps.get(OrgstructureBean.PROP_ORG_ELEMENT_FULL_NAME));
		props.put(PROP_NOMENCLATURE_UNIT_SECTION_INDEX, unitProps.get(OrgstructureBean.PROP_UNIT_CODE));
		props.put(PROP_NOMENCLATURE_COMMON_INDEX, unitProps.get(OrgstructureBean.PROP_UNIT_CODE));
		props.put(PROP_NOMENCLATURE_UNIT_SECTION_COMMENT, "Создано автоматически");

		NodeRef newUnit;
		try {
			newUnit = createNode(root, TYPE_NOMENCLATURE_UNIT_SECTION, UUID.randomUUID().toString(), props);
			nodeService.createAssociation(newUnit, unitRef, ASSOC_NOMENCLATURE_UNIT_TO_ORGUNIT);

			if(fuckingDeepCopy) {
				List<NodeRef> unitChildren = orgstructureService.getSubUnits(unitRef, true, false, false);
				for (NodeRef unitChild : unitChildren) {
					createSectionByUnit(unitChild, newUnit, fuckingDeepCopy);
				}
			}
		} catch (WriteTransactionNeededException ex) {
			logger.error("For creating unit needed write transaction", ex);
		}



	}

	public void grantPermissionToArchivist(NodeRef docNodeRef) {
		final Types.SGPosition sgBusinessRole = Types.SGKind.SG_BR.getSGPos(BR_ARCHIVIST);
		String shortName = sgBusinessRole.getAlfrescoSuffix();

		String authority = authorityService.getName(AuthorityType.GROUP, shortName);
		permissionService.setPermission(docNodeRef, authority, "LECM_BASIC_PG_Reader", true);
	}

	@Deprecated
	@Override
	public void createTreeByOrgUnits(NodeRef yearSectionRef) {
		NodeRef organizationRef = nodeService.getTargetAssocs(yearSectionRef, ASSOC_NOMENCLATURE_LINKED_ORG).get(0).getTargetRef();
	}

	@Override
	public boolean caseHasDocumentsVolumes(NodeRef caseRef) {
		return caseHasDocumentsVolumes(caseRef, true);
	}

	@Override
	public boolean caseHasDocumentsVolumes(NodeRef caseRef, boolean checkVolumes) {
		NodeRef docFolder = getDocuemntsFolder(caseRef);

		List<ChildAssociationRef> docs = nodeService.getChildAssocs(docFolder);
		List<ChildAssociationRef> volumes = null;
		if (checkVolumes) {
			volumes = nodeService.getChildAssocs(caseRef, new HashSet<>(Arrays.asList(TYPE_NOMENCLATURE_VOLUME)));
		}

		return (docs != null && docs.size() > 0) || (checkVolumes && (volumes != null && volumes.size() > 0));
	}

	private List<NodeRef> getAllOrgUnitsAssocs(NodeRef sectionNodeRef) {
		List<NodeRef> results = new ArrayList<>();

		if(Boolean.FALSE.equals(nodeService.getProperty(sectionNodeRef, IS_ACTIVE))) {
			return results;
		}

		List<AssociationRef> orgUnitAssocs = nodeService.getTargetAssocs(sectionNodeRef, ASSOC_NOMENCLATURE_UNIT_TO_ORGUNIT);
		if(orgUnitAssocs != null && orgUnitAssocs.size() > 0) {
			NodeRef unit = orgUnitAssocs.get(0).getTargetRef();
			if(Boolean.TRUE.equals(nodeService.getProperty(unit, IS_ACTIVE))) {
				results.add(orgUnitAssocs.get(0).getTargetRef());
			}
		}

		List<ChildAssociationRef> sections = nodeService.getChildAssocs(sectionNodeRef, new HashSet<>(Arrays.asList(TYPE_NOMENCLATURE_UNIT_SECTION)));
		for (ChildAssociationRef section : sections) {
			NodeRef sectionNode = section.getChildRef();
			results.addAll(getAllOrgUnitsAssocs(sectionNode));
		}

		return results;
	}

	@Override
	public boolean canCopyUnits(List<NodeRef> units, NodeRef dest) {
		List<NodeRef> orgUnits = getAllOrgUnitsAssocs(dest);

		for (NodeRef unit : units) {
			List<AssociationRef> orgUnitsAssocs = nodeService.getTargetAssocs(unit, ASSOC_NOMENCLATURE_UNIT_TO_ORGUNIT);
			if(orgUnitsAssocs != null && orgUnitsAssocs.size() > 0) {
				NodeRef orgUnit = orgUnitsAssocs.get(0).getTargetRef();
				if(orgUnits.contains(orgUnit)) {
					return false;
				}
			}
		}

		return true;
	}

	@Override
	public boolean isCetralized() {
		NodeRef settings = getSettings();

		if(settings != null) {
			return Boolean.TRUE.equals(nodeService.getProperty(settings, PROP_OPERATIVE_STORAGE_CENRALIZED));
		}

		return true;
	}

	/*
		Лайт-версия метода удаления из RemovalService
		Т.к документ находится в деле, то можно ограничиться удалением участников,
		чисткой пермиссий, и удалением связей. Всё остальное удалиться само
	*/

	private void clearDir(NodeRef dirNode) {
		List<ChildAssociationRef> childAssocs = nodeService.getChildAssocs(dirNode);
		for (ChildAssociationRef childAssoc : childAssocs) {
			NodeRef parentRef = childAssoc.getParentRef(),
					childRef = childAssoc.getChildRef();
			if (!nodeService.getChildAssocs(childRef).isEmpty()) {
				clearDir(childRef);
			}
			behaviourFilter.disableBehaviour(parentRef);
			cruellyDeleteNode(childRef);
		}
	}

	private void cruellyDeleteNode(NodeRef node) {
		behaviourFilter.disableBehaviour(node);
		nodeService.addAspect(node, ContentModel.ASPECT_TEMPORARY, null);
		nodeService.deleteNode(node);
	}

	private void removeDocument(NodeRef docNodeRef) {
		// Проверим, находится ли документ в деле
		logger.info("Going to delete document " + docNodeRef);

		ChildAssociationRef potentialDocsFolder = nodeService.getPrimaryParent(docNodeRef);
		if(potentialDocsFolder != null) {
			ChildAssociationRef potentialCaseRef = nodeService.getPrimaryParent(potentialDocsFolder.getParentRef());
			if(potentialCaseRef != null) {
				if(!OperativeStorageService.TYPE_NOMENCLATURE_CASE.equals(nodeService.getType(potentialCaseRef.getParentRef()))) {
					logger.warn("Document " + docNodeRef + " isn't child of nomenclature case, aborting delete!");
					return;
				}
			}
		}

		behaviourFilter.disableBehaviour(docNodeRef);
		logger.debug("All policies for document {} are deactivated!", docNodeRef);

		//лишаем участников документа всех прав связанных с этим документом
		List<NodeRef> members = documentMembersService.getDocumentMembers(docNodeRef);
		List<String> users = new ArrayList<>();
		for (NodeRef member : members) {
			behaviourFilter.disableBehaviour(member);
			List<AssociationRef> assocs = nodeService.getTargetAssocs(member, DocumentMembersService.ASSOC_MEMBER_EMPLOYEE);
			NodeRef employeeRef = assocs.get(0).getTargetRef();
			String login = orgstructureService.getEmployeeLogin(employeeRef);
			String shortName = (String) nodeService.getProperty(employeeRef, OrgstructureBean.PROP_EMPLOYEE_SHORT_NAME);
			users.add(String.format("%s(%s)", shortName, login));
			LecmPermissionService.LecmPermissionGroup permissionGroup = documentMembersService.getMemberPermissionGroup(docNodeRef);
			lecmPermissionService.revokeAccess(permissionGroup, docNodeRef, employeeRef);
			List<String> roles = lecmPermissionService.getEmployeeRoles(docNodeRef, employeeRef);
			for (String role : roles) {
				lecmPermissionService.revokeDynamicRole(role, docNodeRef, employeeRef.getId());
			}
			documentMembersService.deleteMember(docNodeRef, employeeRef);
		}

		try {
			//получаем все вложения отключаем их policy и удаляем
			List<NodeRef> categories = documentAttachmentsService.getCategories(docNodeRef);
			for (NodeRef categoryRef : categories) {
                List<ChildAssociationRef> attachments = nodeService.getChildAssocs(categoryRef);
                for (ChildAssociationRef attachRef : attachments) {
                    cruellyDeleteNode(attachRef.getChildRef());
                }
			}
		} catch (Exception ex) {
			// что-то сломалось при попытке получить категории вложений. это не повод прекращать удаление документа
			String msg = "Error during deleting document %s";
			logger.warn(String.format(msg, docNodeRef), ex);
		}

		logger.debug("Members {} are deleted and access is revoked for document {}", users, docNodeRef);

		String user = AuthenticationUtil.getFullyAuthenticatedUser();

		BusinessJournalRecord deleteRecord = businessJournalService.createBusinessJournalRecord(user, docNodeRef, EventCategory.DELETE, DOCUMENT_TEMPLATE);

		cruellyDeleteNode(docNodeRef);

		businessJournalService.sendRecord(deleteRecord);
	}

	@Override
	public void removeYearSection(NodeRef yearSection) {
		List<ChildAssociationRef> units = nodeService.getChildAssocs(yearSection, new HashSet<>(Arrays.asList(OperativeStorageService.TYPE_NOMENCLATURE_UNIT_SECTION)));
		for (ChildAssociationRef unit : units) {
			removeUnitSection(unit.getChildRef());
		}

		String user = AuthenticationUtil.getFullyAuthenticatedUser();

		BusinessJournalRecord deleteRecord = businessJournalService.createBusinessJournalRecord(user, yearSection, EventCategory.DELETE, OS_YEAR_TEMPLATE);

		cruellyDeleteNode(yearSection);

		businessJournalService.sendRecord(deleteRecord);
	}

	@Override
	public void removeUnitSection(NodeRef unitSection) {
		List<ChildAssociationRef> cases = nodeService.getChildAssocs(unitSection, new HashSet<>(Arrays.asList(OperativeStorageService.TYPE_NOMENCLATURE_CASE)));
		List<ChildAssociationRef> units = nodeService.getChildAssocs(unitSection, new HashSet<>(Arrays.asList(OperativeStorageService.TYPE_NOMENCLATURE_UNIT_SECTION)));

		for (ChildAssociationRef caseAssoc : cases) {
			NodeRef caseRef = caseAssoc.getChildRef();
			removeCase(caseRef);
		}

		for (ChildAssociationRef unit : units) {
			removeUnitSection(unit.getChildRef());
		}

		String user = AuthenticationUtil.getFullyAuthenticatedUser();

		BusinessJournalRecord deleteRecord = businessJournalService.createBusinessJournalRecord(user, unitSection, EventCategory.DELETE, OS_UNIT_TEMPLATE);

		cruellyDeleteNode(unitSection);

		businessJournalService.sendRecord(deleteRecord);
	}

	@Override
	public void removeCase(NodeRef caseRef) {
		NodeRef documentsFolder = getDocuemntsFolder(caseRef);
		if(documentsFolder != null) {
			List<ChildAssociationRef> documents = nodeService.getChildAssocs(documentsFolder);
			if(documents != null && !documents.isEmpty()) {
				for (ChildAssociationRef document : documents) {
					removeDocument(document.getChildRef());

				}
			}
		}

		String user = AuthenticationUtil.getFullyAuthenticatedUser();

		BusinessJournalRecord deleteRecord = businessJournalService.createBusinessJournalRecord(user, caseRef, EventCategory.DELETE, CASE_TEMPLATE);

		cruellyDeleteNode(caseRef);

		businessJournalService.sendRecord(deleteRecord);
	}

	@Override
	public void sendToArchiveAction(NodeRef caseRef) {
		boolean noPermChange = Boolean.TRUE.equals(nodeService.getProperty(caseRef, PROP_NO_PERM_CHANGE));
		List<NodeRef> docs = new ArrayList<>();

		NodeRef documentsFolder = getDocuemntsFolder(caseRef);
		if(documentsFolder != null) {
			List<ChildAssociationRef> documents = nodeService.getChildAssocs(documentsFolder);
			if(documents != null && !documents.isEmpty()) {
				for (ChildAssociationRef document : documents) {
					nodeService.setProperty(document.getChildRef(), StatemachineModel.PROP_STATUS, "В архиве");
				}
			}
		}

		if(!noPermChange) {
			nodeService.setProperty(caseRef, PROP_NOMENCLATURE_CASE_IS_SHARED, true);
			cleanVisibilityList(caseRef);
			grantAll(caseRef);
		}

		nodeService.setProperty(caseRef, PROP_NOMENCLATURE_CASE_STATUS, "ARCHIVE");
		grantPermissionsToAllArchivists(caseRef);


	}

}
