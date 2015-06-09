/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.it.lecm.deputy.policy;

import java.util.List;
import org.alfresco.repo.node.NodeServicePolicies;
import org.alfresco.repo.policy.Behaviour;
import org.alfresco.repo.policy.JavaBehaviour;
import org.alfresco.repo.policy.PolicyComponent;
import org.alfresco.service.cmr.repository.AssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.util.PropertyCheck;
import ru.it.lecm.deputy.DeputyService;

/**
 *
 * @author ikhalikov
 */
public class DeputyPolicy implements NodeServicePolicies.OnCreateAssociationPolicy, NodeServicePolicies.OnDeleteAssociationPolicy {

	protected PolicyComponent policyComponent;
	protected NodeService nodeService;
	protected DeputyService deputyService;

	public void setDeputyService(DeputyService deputyService) {
		this.deputyService = deputyService;
	}

	public void setPolicyComponent(PolicyComponent policyComponent) {
		this.policyComponent = policyComponent;
	}

	public void setNodeService(NodeService nodeService) {
		this.nodeService = nodeService;
	}

	public void init() {
		PropertyCheck.mandatory(this, "policyComponent", policyComponent);
		PropertyCheck.mandatory(this, "nodeService", nodeService);

		policyComponent.bindAssociationBehaviour(NodeServicePolicies.OnDeleteAssociationPolicy.QNAME,
				DeputyService.TYPE_DEPUTY_SETTINGS, DeputyService.ASSOC_SETTINGS_DICTIONARY, new JavaBehaviour(this, "onDeleteSettingsSubjectAssoc", Behaviour.NotificationFrequency.TRANSACTION_COMMIT));

		policyComponent.bindAssociationBehaviour(NodeServicePolicies.OnCreateAssociationPolicy.QNAME,
				DeputyService.TYPE_DEPUTY_NODE, DeputyService.ASSOC_DEPUTY_SUBJECT, new JavaBehaviour(this, "onCreateAssociation"));

		policyComponent.bindAssociationBehaviour(NodeServicePolicies.OnDeleteAssociationPolicy.QNAME,
				DeputyService.TYPE_DEPUTY_NODE, DeputyService.ASSOC_DEPUTY_SUBJECT, new JavaBehaviour(this, "onDeleteAssociation"));
	}

	public void onDeleteSettingsSubjectAssoc(AssociationRef nodeAssocRef) {
		deputyService.deleteAllSubjectDeputies();
	}

	@Override
	public void onCreateAssociation(AssociationRef nodeAssocRef) {
		nodeService.setProperty(nodeAssocRef.getSourceRef(), DeputyService.PROP_DEPUTY_COMPLETE, false);
	}

	@Override
	public void onDeleteAssociation(AssociationRef nodeAssocRef) {
		NodeRef deputyRef = nodeAssocRef.getSourceRef();
		List<AssociationRef> deputySubjectAssocs = nodeService.getTargetAssocs(deputyRef, DeputyService.ASSOC_DEPUTY_SUBJECT);

		if(deputySubjectAssocs == null || deputySubjectAssocs.isEmpty()) {
			nodeService.setProperty(deputyRef, DeputyService.PROP_DEPUTY_COMPLETE, true);
		}
	}

}
