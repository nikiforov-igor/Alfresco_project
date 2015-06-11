/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.it.lecm.operativestorage.policies;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.alfresco.repo.node.NodeServicePolicies.OnAddAspectPolicy;
import org.alfresco.repo.node.NodeServicePolicies.OnCreateAssociationPolicy;
import org.alfresco.repo.node.NodeServicePolicies.OnCreateNodePolicy;
import org.alfresco.repo.node.NodeServicePolicies.OnDeleteAssociationPolicy;
import org.alfresco.repo.node.NodeServicePolicies.OnUpdatePropertiesPolicy;
import org.alfresco.repo.policy.Behaviour;
import org.alfresco.repo.policy.JavaBehaviour;
import org.alfresco.repo.policy.PolicyComponent;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.service.cmr.repository.AssociationRef;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.namespace.QName;
import org.alfresco.util.PropertyCheck;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.it.lecm.operativestorage.beans.OperativeStorageService;
import ru.it.lecm.orgstructure.beans.OrgstructureBean;

/**
 *
 * @author ikhalikov
 */
public class NomenclatureCasePolicy implements OnCreateNodePolicy,
		OnCreateAssociationPolicy, OnDeleteAssociationPolicy, OnUpdatePropertiesPolicy , OnAddAspectPolicy {

	private final static Logger logger = LoggerFactory.getLogger(NomenclatureCasePolicy.class);

	private PolicyComponent policyComponent;
	private NodeService nodeService;
	private OperativeStorageService operativeStorageService;
	private OrgstructureBean orgstructureService;

	public void setOrgstructureService(OrgstructureBean orgstructureService) {
		this.orgstructureService = orgstructureService;
	}

	public void setOperativeStorageService(OperativeStorageService operativeStorageService) {
		this.operativeStorageService = operativeStorageService;
	}

	public void setPolicyComponent(PolicyComponent policyComponent) {
		this.policyComponent = policyComponent;
	}

	public void setNodeService(NodeService nodeService) {
		this.nodeService = nodeService;
	}

	public void init() {
		PropertyCheck.mandatory(this, "policyComponent", policyComponent);

		policyComponent.bindClassBehaviour(OnCreateNodePolicy.QNAME, OperativeStorageService.TYPE_NOMENCLATURE_CASE,
				new JavaBehaviour(this, "onCreateNode", Behaviour.NotificationFrequency.TRANSACTION_COMMIT));

		policyComponent.bindClassBehaviour(OnUpdatePropertiesPolicy.QNAME, OperativeStorageService.TYPE_NOMENCLATURE_CASE,
				new JavaBehaviour(this, "onUpdateProperties", Behaviour.NotificationFrequency.TRANSACTION_COMMIT));

		policyComponent.bindAssociationBehaviour(OnCreateAssociationPolicy.QNAME, OperativeStorageService.TYPE_NOMENCLATURE_CASE,
				new JavaBehaviour(this, "onCreateAssociation", Behaviour.NotificationFrequency.TRANSACTION_COMMIT));

		policyComponent.bindAssociationBehaviour(OnDeleteAssociationPolicy.QNAME, OperativeStorageService.TYPE_NOMENCLATURE_CASE,
				new JavaBehaviour(this, "onDeleteAssociation", Behaviour.NotificationFrequency.TRANSACTION_COMMIT));

		policyComponent.bindClassBehaviour(OnAddAspectPolicy.QNAME, OperativeStorageService.ASPECT_MOVE_TO_CASE,
				new JavaBehaviour(this, "onAddAspect", Behaviour.NotificationFrequency.TRANSACTION_COMMIT));
	}

	@Override
	public void onCreateNode(final ChildAssociationRef childAssocRef) {

		AuthenticationUtil.runAsSystem(new AuthenticationUtil.RunAsWork<Object>() {

			@Override
			public Object doWork() throws Exception {
				NodeRef nodeRef = childAssocRef.getChildRef();
				NodeRef yearSection = operativeStorageService.getYearSection(nodeRef);
				nodeService.createAssociation(nodeRef, yearSection, OperativeStorageService.ASSOC_NOMENCLATURE_CASE_YEAR);

				List<AssociationRef> unitAssocs = nodeService.getTargetAssocs(nodeRef, OperativeStorageService.ASSOC_NOMENCLATURE_CASE_VISIBILITY_UNIT);
				if(unitAssocs == null || unitAssocs.isEmpty()) {
					NodeRef defaultUnit = getDefaultOrgUnit(nodeRef);
					if(defaultUnit != null) {
						nodeService.createAssociation(nodeRef, defaultUnit, OperativeStorageService.ASSOC_NOMENCLATURE_CASE_VISIBILITY_UNIT);
					}
				}

				operativeStorageService.createDocsFolder(nodeRef);
				operativeStorageService.createReferencesFolder(nodeRef);
				return null;
			}
		});

	}

	private NodeRef getDefaultOrgUnit(NodeRef caseNodeRef) {
		NodeRef sectionRef = nodeService.getPrimaryParent(caseNodeRef).getParentRef();
		List<AssociationRef> unitList = nodeService.getTargetAssocs(sectionRef, OperativeStorageService.ASSOC_NOMENCLATURE_UNIT_TO_ORGUNIT);
		if(unitList != null) {
			return unitList.get(0).getTargetRef();
		}

		return null;
	}

	@Override
	public void onCreateAssociation(AssociationRef nodeAssocRef) {
		NodeRef caseNodeRef = nodeAssocRef.getSourceRef();
		boolean isShared = (boolean) nodeService.getProperty(caseNodeRef, operativeStorageService.PROP_NOMENCLATURE_CASE_IS_SHARED);
		NodeRef docFolderRef = operativeStorageService.getDocuemntsFolder(caseNodeRef);

		if(docFolderRef == null) {
			docFolderRef = operativeStorageService.createDocsFolder(nodeAssocRef.getSourceRef());
		}
		NodeRef targetRef = nodeAssocRef.getTargetRef();

		if(nodeAssocRef.getTypeQName().equals(OperativeStorageService.ASSOC_NOMENCLATURE_CASE_VISIBILITY_EMPLOYEE)) {
			operativeStorageService.grantPermToEmployee(docFolderRef, targetRef);
		}

		if(nodeAssocRef.getTypeQName().equals(OperativeStorageService.ASSOC_NOMENCLATURE_CASE_VISIBILITY_UNIT)) {
			List<NodeRef> units = new ArrayList<>();

			units.add(targetRef);

			if(isShared) {
				units.addAll(orgstructureService.getSubUnits(targetRef, true, true, false));
			}

			for (NodeRef unit : units) {
				operativeStorageService.grantPermToUnit(docFolderRef, unit, isShared);
			}

		}

		if(nodeAssocRef.getTypeQName().equals(OperativeStorageService.ASSOC_NOMENCLATURE_CASE_VISIBILITY_WORK_GROUP)) {
			operativeStorageService.grantPermToWG(docFolderRef, targetRef);
		}

		if(nodeAssocRef.getTypeQName().equals(OperativeStorageService.ASSOC_NOMENCLATURE_CASE_YEAR)) {
			String yearStatus = (String) nodeService.getProperty(targetRef, OperativeStorageService.PROP_NOMENCLATURE_YEAR_SECTION_STATUS);
			nodeService.setProperty(caseNodeRef, OperativeStorageService.PROP_NOMENCLATURE_CASE_YEAR_STATUS, yearStatus);
		}

	}

	@Override
	public void onDeleteAssociation(AssociationRef nodeAssocRef) {
		NodeRef caseNodeRef = nodeAssocRef.getSourceRef();

		if (!nodeService.exists(caseNodeRef)) {
            return;
        }

		boolean isShared = (boolean) nodeService.getProperty(caseNodeRef, operativeStorageService.PROP_NOMENCLATURE_CASE_IS_SHARED);


		NodeRef docFolderRef = operativeStorageService.getDocuemntsFolder(nodeAssocRef.getSourceRef());

		NodeRef targetRef = nodeAssocRef.getTargetRef();


		if(nodeAssocRef.getTypeQName().equals(OperativeStorageService.ASSOC_NOMENCLATURE_CASE_VISIBILITY_EMPLOYEE)) {
			operativeStorageService.revokePermFromEmployee(docFolderRef, targetRef);
		}

		if(nodeAssocRef.getTypeQName().equals(OperativeStorageService.ASSOC_NOMENCLATURE_CASE_VISIBILITY_UNIT)) {
			List<NodeRef> units = new ArrayList<>();
			units.add(targetRef);

			if(isShared) {
				units.addAll(orgstructureService.getSubUnits(targetRef, true, true, false));
			}

			for (NodeRef unit : units) {
				operativeStorageService.revokePermFromUnit(docFolderRef, unit);
			}

		}

		if(nodeAssocRef.getTypeQName().equals(OperativeStorageService.ASSOC_NOMENCLATURE_CASE_VISIBILITY_WORK_GROUP)) {
			operativeStorageService.revokePermFromWG(docFolderRef, targetRef);
		}

	}

	@Override
	public void onUpdateProperties(NodeRef nodeRef, Map<QName, Serializable> before, Map<QName, Serializable> after) {
		Boolean sharedFlagBefore = (Boolean) before.get(OperativeStorageService.PROP_NOMENCLATURE_CASE_IS_SHARED);
		Boolean sharedFlagAfter = (Boolean) after.get(OperativeStorageService.PROP_NOMENCLATURE_CASE_IS_SHARED);
		if(sharedFlagBefore == null || sharedFlagAfter == null) {
			return;
		}
		if(!sharedFlagAfter.equals(sharedFlagBefore)) {

			NodeRef docFolder = operativeStorageService.getDocuemntsFolder(nodeRef);

			List<AssociationRef> assocs = nodeService.getTargetAssocs(nodeRef, OperativeStorageService.ASSOC_NOMENCLATURE_CASE_VISIBILITY_UNIT);
			if(assocs != null) {
				for (AssociationRef assoc : assocs) {
					operativeStorageService.revokePermFromUnit(docFolder, assoc.getTargetRef(), sharedFlagBefore);
					operativeStorageService.grantPermToUnit(docFolder, assoc.getTargetRef(), sharedFlagAfter);
				}
			}
		}
	}

	@Override
	public void onAddAspect(NodeRef nodeRef, QName aspectTypeQName) {
		operativeStorageService.grantPermissionToArchivist(nodeRef);
	}

}
