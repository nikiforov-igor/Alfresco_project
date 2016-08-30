/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.it.lecm.nd.policy;

import org.alfresco.repo.node.NodeServicePolicies;
import org.alfresco.repo.policy.Behaviour;
import org.alfresco.repo.policy.JavaBehaviour;
import org.alfresco.repo.policy.PolicyComponent;
import org.alfresco.service.cmr.repository.AssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.util.PropertyCheck;
import ru.it.lecm.documents.beans.DocumentService;
import ru.it.lecm.nd.api.NDModel;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author ikhalikov
 */
public class NDCreatePolicy implements NodeServicePolicies.OnCreateAssociationPolicy {

	private PolicyComponent policyComponent;
	private NodeService nodeService;

	public void setPolicyComponent(PolicyComponent policyComponent) {
		this.policyComponent = policyComponent;
	}

	public void setNodeService(NodeService nodeService) {
		this.nodeService = nodeService;
	}

	public final void init() {
		PropertyCheck.mandatory(this, "policyComponent", policyComponent);
		PropertyCheck.mandatory(this, "nodeService", nodeService);

		policyComponent.bindAssociationBehaviour(NodeServicePolicies.OnCreateAssociationPolicy.QNAME,
				NDModel.TYPE_ND, DocumentService.ASSOC_ADDITIONAL_ORGANIZATION_UNIT_ASSOC,
				new JavaBehaviour(this, "onCreateAssociation", Behaviour.NotificationFrequency.TRANSACTION_COMMIT));
	}

	@Override
	public void onCreateAssociation(AssociationRef nodeAssocRef) {
		NodeRef doc = nodeAssocRef.getSourceRef();
		NodeRef orgUnit = nodeAssocRef.getTargetRef();

		List<NodeRef> units = new ArrayList<>();
		units.add(orgUnit);
		nodeService.setAssociations(doc, DocumentService.ASSOC_ORGANIZATION_UNIT_ASSOC, units);
	}
}
