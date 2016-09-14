package ru.it.lecm.events.policy;

import org.alfresco.repo.node.NodeServicePolicies;
import org.alfresco.repo.policy.JavaBehaviour;
import ru.it.lecm.base.policies.LogicECMAssociationPolicy;
import ru.it.lecm.events.beans.EventsService;

/**
 *
 * @author dbashmakov
 */
public class EventsAssociationHandlerPolicy extends LogicECMAssociationPolicy {
	@Override
	public final void init() {
		super.init();
		policyComponent.bindAssociationBehaviour(
			NodeServicePolicies.OnDeleteAssociationPolicy.QNAME,
			EventsService.TYPE_EVENT_LOCATION,
			new JavaBehaviour(this, "onDeleteAssociation"));

		policyComponent.bindAssociationBehaviour(
			NodeServicePolicies.OnCreateAssociationPolicy.QNAME,
				EventsService.TYPE_EVENT_LOCATION,
			new JavaBehaviour(this, "onCreateAssociation"));

		policyComponent.bindAssociationBehaviour(
				NodeServicePolicies.OnDeleteAssociationPolicy.QNAME,
				EventsService.TYPE_EVENT_RESOURCE,
				new JavaBehaviour(this, "onDeleteAssociation"));

		policyComponent.bindAssociationBehaviour(
				NodeServicePolicies.OnCreateAssociationPolicy.QNAME,
				EventsService.TYPE_EVENT_RESOURCE,
				new JavaBehaviour(this, "onCreateAssociation"));
	}
}
