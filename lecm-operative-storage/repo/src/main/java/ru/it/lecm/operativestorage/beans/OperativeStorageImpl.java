/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.it.lecm.operativestorage.beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.alfresco.model.ContentModel;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.repo.transaction.RetryingTransactionHelper;
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
import ru.it.lecm.documents.beans.DocumentService;
import ru.it.lecm.eds.api.EDSDocumentService;
import ru.it.lecm.orgstructure.beans.OrgstructureBean;
import ru.it.lecm.security.LecmPermissionService;
import ru.it.lecm.security.Types;

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

	public void init() {
		if(getSettings() == null) {
			RetryingTransactionHelper transactionHelper = transactionService.getRetryingTransactionHelper();
			transactionHelper.doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<Object>() {

				@Override
				public Object execute() throws Throwable {
					AuthenticationUtil.runAsSystem(new AuthenticationUtil.RunAsWork<Object>() {

						@Override
						public Object doWork() throws Exception {
							PropertyMap props = new PropertyMap();
							props.put(PROP_OPERATIVE_STORAGE_CENRALIZED, true);
							createNode(getOperativeStorageFolder(), TYPE_OPERATIVE_STORAGE_SETTING, OPERATIVE_STORAGE_GLOBAL_SETTING_NAME, props);
							return null;
						}
					});
					return null;
				}
			}, false, true);
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
		NodeRef parent = nodeService.getParentAssocs(nodeRef).get(0).getParentRef();
		while(parent != null) {
			if(TYPE_NOMENCLATURE_YEAR_SECTION.equals(nodeService.getType(parent))) {
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

		grantPermToUnit(docFolder, mainOrgUnit);

		nodeService.createAssociation(nodeRef, mainOrgUnit, ASSOC_NOMENCLATURE_CASE_VISIBILITY_UNIT);
	}

	@Override
	public void grantPermToEmployee(NodeRef nodeRef, NodeRef employee) {
		LecmPermissionService.LecmPermissionGroup pgGranting = lecmPermissionService.findPermissionGroup(DEFAULT_GRANTED_ROLE);
		lecmPermissionService.grantAccess(pgGranting, nodeRef, employee);
	}

	@Override
	public void grantPermToUnit(NodeRef nodeRef, NodeRef unit) {
		LecmPermissionService.LecmPermissionGroup pgGranting = lecmPermissionService.findPermissionGroup(DEFAULT_GRANTED_ROLE);
		Types.SGPosition sgUnit = Types.SGKind.SG_OU.getSGPos(unit.getId());
		lecmPermissionService.grantAccessByPosition(pgGranting, nodeRef, sgUnit);
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
		NodeRef docFolder = getDocuemntsFolder(caseNodeRef);
		QName assocQName = QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, UUID.randomUUID().toString());
		nodeService.moveNode(docNodeRef, docFolder, ContentModel.ASSOC_CONTAINS, assocQName);

		nodeService.setProperty(docNodeRef, PROP_IN_CASE, true);
		nodeService.createAssociation(docNodeRef, caseNodeRef, ASSOC_NOMENCLATURE_CASE);

		nodeService.addAspect(docNodeRef, DocumentService.ASPECT_DONT_MOVE_TO_ARCHIVE_FOLDER, null);
	}

	@Override
	public void moveDocToNomenclatureCase(NodeRef docNodeRef) {
		List<AssociationRef> assocList = nodeService.getTargetAssocs(docNodeRef, EDSDocumentService.ASSOC_FILE_REGISTER);
		if(assocList != null && !assocList.isEmpty()) {
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
		LecmPermissionService.LecmPermissionGroup pgGranting = lecmPermissionService.findPermissionGroup(DEFAULT_GRANTED_ROLE);
		Types.SGPosition sgUnit = Types.SGKind.SG_OU.getSGPos(unit.getId());
		lecmPermissionService.revokeAccessByPosition(pgGranting, nodeRef, sgUnit);
	}

	@Override
	public void revokePermFromWG(NodeRef nodeRef, NodeRef group) {
		LecmPermissionService.LecmPermissionGroup pgGranting = lecmPermissionService.findPermissionGroup(DEFAULT_GRANTED_ROLE);
		Types.SGPosition sgWG = Types.SGKind.SG_WG.getSGPos(group.getId());
		lecmPermissionService.revokeAccessByPosition(pgGranting, nodeRef, sgWG);
	}

	@Override
	public void updatePermissions(NodeRef nodeRef) {
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
			grantPermToUnit(docFolder, unitAssociationRef.getTargetRef());
		}

		//рабочим группам
		List<AssociationRef> workGroups = nodeService.getTargetAssocs(nodeRef, ASSOC_NOMENCLATURE_CASE_VISIBILITY_WORK_GROUP);
		for (AssociationRef wgAssociationRef : workGroups) {
			grantPermToWG(docFolder, wgAssociationRef.getTargetRef());
		}
	}

	@Override
	public NodeRef getSettings() {
		return nodeService.getChildByName(getOperativeStorageFolder(), ContentModel.ASSOC_CONTAINS, OPERATIVE_STORAGE_GLOBAL_SETTING_NAME);
	}

	@Override
	public boolean orgUnitAssociationExists(NodeRef ndUnitRef, NodeRef orgUnitRef) {
		List<NodeRef> allUnits = new ArrayList<>();
		NodeRef yearSection = getYearSection(ndUnitRef);
		List<ChildAssociationRef> unitSections = nodeService.getChildAssocs(yearSection, ContentModel.ASSOC_CONTAINS, RegexQNamePattern.MATCH_ALL);

		for (ChildAssociationRef unit : unitSections) {
			allUnits.addAll(getAllChildren(unit.getChildRef()));
			for (NodeRef unitSection : allUnits) {
				if(hasOrgUnit(unitSection, orgUnitRef)) {
					return true;
				}
			}
		}

		return false;
	}

	public boolean hasOrgUnit(NodeRef ndUnitRef, NodeRef orgUnitRef) {
		List<AssociationRef> assocList = nodeService.getTargetAssocs(ndUnitRef, ASSOC_NOMENCLATURE_UNIT_TO_ORGUNIT);
		if(assocList != null && !assocList.isEmpty()) {
			return orgUnitRef.equals(assocList.get(0).getTargetRef());
		}
		return false;
	}

	private List<NodeRef> getAllChildren(NodeRef nodeRef) {
		List<NodeRef> result = new ArrayList<>();
		List<ChildAssociationRef> subRes = nodeService.getChildAssocs(nodeRef, ContentModel.ASSOC_CONTAINS, RegexQNamePattern.MATCH_ALL);
		for (ChildAssociationRef child : subRes) {
			if(TYPE_NOMENCLATURE_UNIT_SECTION.equals(nodeService.getType(child.getChildRef()))){
				result.add(child.getChildRef());
				result.addAll(getAllChildren(child.getChildRef()));
			}
		}

		return result;
	}

	@Override
	public boolean checkNDSectionAssociationExists(NodeRef orgUnitRef, NodeRef ndSectionRef) {
		NodeRef yearSection;
		if(TYPE_NOMENCLATURE_YEAR_SECTION.equals(nodeService.getType(ndSectionRef))) {
			yearSection = ndSectionRef;
		} else {
			yearSection = getYearSection(ndSectionRef);
		}

		List<AssociationRef> assocList = nodeService.getSourceAssocs(orgUnitRef, ASSOC_NOMENCLATURE_UNIT_TO_ORGUNIT);

		for (AssociationRef assoc : assocList) {
			NodeRef assocYearSection = getYearSection(assoc.getSourceRef());
			if(assocYearSection.equals(yearSection)) {
				return true;
			}
		}

		return false;


	}

}
