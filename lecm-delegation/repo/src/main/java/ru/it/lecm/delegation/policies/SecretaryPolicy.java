/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.it.lecm.delegation.policies;

import java.util.List;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.node.NodeServicePolicies;
import org.alfresco.repo.policy.Behaviour;
import org.alfresco.repo.policy.JavaBehaviour;
import org.alfresco.repo.policy.PolicyComponent;
import org.alfresco.service.cmr.repository.AssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.util.PropertyCheck;
import ru.it.lecm.arm.beans.ArmService;
import ru.it.lecm.orgstructure.beans.OrgstructureBean;
import ru.it.lecm.secretary.SecretaryService;

/**
 *
 * @author ikhalikov
 */
public class SecretaryPolicy implements NodeServicePolicies.OnCreateAssociationPolicy, NodeServicePolicies.OnDeleteAssociationPolicy {

	protected PolicyComponent policyComponent;
	protected NodeService nodeService;
	private ArmService armService;

	private final String SECRETARY_ARM_NODE_NAME = "Работа %s";

	public void setPolicyComponent(PolicyComponent policyComponent) {
		this.policyComponent = policyComponent;
	}

	public void setNodeService(NodeService nodeService) {
		this.nodeService = nodeService;
	}

	public void init() {
		PropertyCheck.mandatory(this, "policyComponent", policyComponent);
		policyComponent.bindAssociationBehaviour(NodeServicePolicies.OnDeleteAssociationPolicy.QNAME,
				OrgstructureBean.TYPE_EMPLOYEE, SecretaryService.ASSOC_CHIEF_ASSOC, new JavaBehaviour(this, "onDeleteAssociation", Behaviour.NotificationFrequency.TRANSACTION_COMMIT));

		policyComponent.bindAssociationBehaviour(NodeServicePolicies.OnCreateAssociationPolicy.QNAME,
				OrgstructureBean.TYPE_EMPLOYEE, SecretaryService.ASSOC_CHIEF_ASSOC, new JavaBehaviour(this, "onCreateAssociation", Behaviour.NotificationFrequency.TRANSACTION_COMMIT));
	}

	@Override
	public void onCreateAssociation(AssociationRef nodeAssocRef) {
		updateTextContent(nodeAssocRef);

		NodeRef chiefEmployee = nodeAssocRef.getTargetRef();
		String employeeName = getEmployeeShortName(chiefEmployee);
		String addedArmName = String.format(SECRETARY_ARM_NODE_NAME, employeeName.substring(0, employeeName.length() - 1));
		NodeRef arm = armService.getArmByCode("SED");
		if (arm != null) {
			NodeRef accordion = nodeService.getChildByName(arm, ContentModel.ASSOC_CONTAINS, addedArmName);
			if (accordion == null) {
				armService.createRunAsAccordion(nodeAssocRef.getTargetRef(), addedArmName, "Моя работа", "SED");
				armService.invalidateCache();
			}
		}
	}

	@Override
	public void onDeleteAssociation(AssociationRef nodeAssocRef) {
		updateTextContent(nodeAssocRef);

		NodeRef chiefEmployee = nodeAssocRef.getTargetRef();
		String employeeName = getEmployeeShortName(chiefEmployee);
		String deletedArmName = String.format(SECRETARY_ARM_NODE_NAME, employeeName.substring(0, employeeName.length() - 1));
		NodeRef arm = armService.getArmByCode("SED");

		List<AssociationRef> targetAssocs = nodeService.getSourceAssocs(chiefEmployee, SecretaryService.ASSOC_CHIEF_ASSOC);
		if (targetAssocs.isEmpty()) {
			if (arm != null) {
				NodeRef accordion = nodeService.getChildByName(arm, ContentModel.ASSOC_CONTAINS, deletedArmName);
				if (accordion != null) {
					nodeService.deleteNode(accordion);
				}
			}
		}
	}

	protected void updateTextContent(AssociationRef nodeAssocRef) {
		NodeRef chiefRef = nodeAssocRef.getTargetRef();
		NodeRef secretaryRef = nodeAssocRef.getSourceRef();

		StringBuilder builderText = new StringBuilder();
		StringBuilder builderRef = new StringBuilder();

		List<AssociationRef> targetAssocs = nodeService.getTargetAssocs(secretaryRef, SecretaryService.ASSOC_CHIEF_ASSOC);

		for (AssociationRef targetAssoc : targetAssocs) {
			NodeRef targetRef = targetAssoc.getTargetRef();
			builderText.append(getEmployeeShortName(targetRef)).append(";");
			builderRef.append(targetRef.toString()).append(";");
		}

		String textValue = "";
		if (builderText.length() > 0) {
			textValue = builderText.substring(0, builderText.length() - 1);
		}
		nodeService.setProperty(secretaryRef, SecretaryService.PROP_CHIEF_ASSOC_REF_TEXT_CONTENT, textValue);

		textValue = "";
		if (builderRef.length() > 0) {
			textValue = builderRef.substring(0, builderRef.length() - 1);
		}
		nodeService.setProperty(secretaryRef, SecretaryService.PROP_CHIEF_ASSOC_REF, textValue);

		builderText = new StringBuilder();
		builderRef = new StringBuilder();

		List<AssociationRef> sourceAssocs = nodeService.getSourceAssocs(chiefRef, SecretaryService.ASSOC_CHIEF_ASSOC);

		for (AssociationRef sourceAssoc : sourceAssocs) {
			NodeRef sourceRef = sourceAssoc.getSourceRef();
			builderText.append(getEmployeeShortName(sourceRef)).append(";");
			builderRef.append(sourceRef.toString()).append(";");
		}

		textValue = "";
		if (builderText.length() > 0) {
			textValue = builderText.substring(0, builderText.length() - 1);
		}
		nodeService.setProperty(chiefRef, SecretaryService.PROP_SECRETARY_ASSOC_REF_TEXT_CONTENT, textValue);

		textValue = "";
		if (builderRef.length() > 0) {
			textValue = builderRef.substring(0, builderRef.length() - 1);
		}
		nodeService.setProperty(chiefRef, SecretaryService.PROP_SECRETARY_ASSOC_REF, textValue);
	}

	private String getEmployeeShortName(NodeRef nodeRef) {
		return (String) nodeService.getProperty(nodeRef, OrgstructureBean.PROP_EMPLOYEE_SHORT_NAME);
	}

	public void setArmService(ArmService armService) {
		this.armService = armService;
	}
}
