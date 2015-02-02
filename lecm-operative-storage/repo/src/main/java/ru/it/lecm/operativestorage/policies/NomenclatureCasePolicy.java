/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.it.lecm.operativestorage.policies;

import java.util.List;
import org.alfresco.repo.node.NodeServicePolicies;
import org.alfresco.repo.policy.Behaviour;
import org.alfresco.repo.policy.JavaBehaviour;
import org.alfresco.repo.policy.PolicyComponent;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.service.cmr.repository.AssociationRef;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.util.PropertyCheck;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.it.lecm.operativestorage.beans.OperativeStorageService;

/**
 *
 * @author ikhalikov
 */
public class NomenclatureCasePolicy implements NodeServicePolicies.OnCreateNodePolicy, NodeServicePolicies.OnCreateAssociationPolicy, NodeServicePolicies.OnDeleteAssociationPolicy{

	private final static Logger logger = LoggerFactory.getLogger(NomenclatureCasePolicy.class);

	private PolicyComponent policyComponent;
	private NodeService nodeService;
	private OperativeStorageService operativeStorageService;

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
		policyComponent.bindClassBehaviour(NodeServicePolicies.OnCreateNodePolicy.QNAME, OperativeStorageService.TYPE_NOMENCLATURE_CASE, new JavaBehaviour(this, "onCreateNode", Behaviour.NotificationFrequency.FIRST_EVENT));
		policyComponent.bindAssociationBehaviour(NodeServicePolicies.OnCreateAssociationPolicy.QNAME, OperativeStorageService.TYPE_NOMENCLATURE_CASE, new JavaBehaviour(this, "onCreateAssociation", Behaviour.NotificationFrequency.TRANSACTION_COMMIT));
		policyComponent.bindAssociationBehaviour(NodeServicePolicies.OnDeleteAssociationPolicy.QNAME, OperativeStorageService.TYPE_NOMENCLATURE_CASE, new JavaBehaviour(this, "onDeleteAssociation", Behaviour.NotificationFrequency.TRANSACTION_COMMIT));
	}

	@Override
	public void onCreateNode(final ChildAssociationRef childAssocRef) {

		AuthenticationUtil.runAsSystem(new AuthenticationUtil.RunAsWork<Object>() {

			@Override
			public Object doWork() throws Exception {
				NodeRef nodeRef = childAssocRef.getChildRef();
				NodeRef yearSection = operativeStorageService.getYearSection(nodeRef);
				nodeService.createAssociation(nodeRef, yearSection, OperativeStorageService.ASSOC_NOMENCLATURE_CASE_YEAR);

				NodeRef defaultUnit = getDefaultOrgUnit(nodeRef);
				if(defaultUnit != null) {
					nodeService.createAssociation(nodeRef, defaultUnit, OperativeStorageService.ASSOC_NOMENCLATURE_CASE_VISIBILITY_UNIT);
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
		NodeRef docFolderRef = operativeStorageService.getDocuemntsFolder(nodeAssocRef.getSourceRef());
		if(docFolderRef == null) {
			docFolderRef = operativeStorageService.createDocsFolder(nodeAssocRef.getSourceRef());
		}
		NodeRef targetRef = nodeAssocRef.getTargetRef();

		if(nodeAssocRef.getTypeQName().equals(OperativeStorageService.ASSOC_NOMENCLATURE_CASE_VISIBILITY_EMPLOYEE)) {
			operativeStorageService.grantPermToEmployee(docFolderRef, targetRef);
		}

		if(nodeAssocRef.getTypeQName().equals(OperativeStorageService.ASSOC_NOMENCLATURE_CASE_VISIBILITY_UNIT)) {
			operativeStorageService.grantPermToUnit(docFolderRef, targetRef);
		}

		if(nodeAssocRef.getTypeQName().equals(OperativeStorageService.ASSOC_NOMENCLATURE_CASE_VISIBILITY_WORK_GROUP)) {
			operativeStorageService.grantPermToWG(docFolderRef, targetRef);
		}
	}

	@Override
	public void onDeleteAssociation(AssociationRef nodeAssocRef) {

		if (!nodeService.exists(nodeAssocRef.getSourceRef())) {
            return;
        }

		NodeRef docFolderRef = operativeStorageService.getDocuemntsFolder(nodeAssocRef.getSourceRef());

		NodeRef targetRef = nodeAssocRef.getTargetRef();

		if(nodeAssocRef.getTypeQName().equals(OperativeStorageService.ASSOC_NOMENCLATURE_CASE_VISIBILITY_EMPLOYEE)) {
			operativeStorageService.revokePermFromEmployee(docFolderRef, targetRef);
		}

		if(nodeAssocRef.getTypeQName().equals(OperativeStorageService.ASSOC_NOMENCLATURE_CASE_VISIBILITY_UNIT)) {
			operativeStorageService.revokePermFromUnit(docFolderRef, targetRef);
		}

		if(nodeAssocRef.getTypeQName().equals(OperativeStorageService.ASSOC_NOMENCLATURE_CASE_VISIBILITY_WORK_GROUP)) {
			operativeStorageService.revokePermFromWG(docFolderRef, targetRef);
		}

	}

}
