package ru.it.lecm.subscriptions.policy;

import org.alfresco.repo.node.NodeServicePolicies;
import org.alfresco.repo.policy.JavaBehaviour;
import ru.it.lecm.base.policies.LogicECMAssociationPolicy;
import ru.it.lecm.subscriptions.beans.SubscriptionsService;

/**
 * User: AIvkin
 * Date: 05.02.13
 * Time: 17:18
 */
public class SubscriptionsToTypeOnCreateAssocsPolicy extends LogicECMAssociationPolicy {
	@Override
	public final void init() {
		super.init();
		policyComponent.bindAssociationBehaviour(NodeServicePolicies.OnDeleteAssociationPolicy.QNAME,
				SubscriptionsService.TYPE_SUBSCRIPTION_TO_TYPE, new JavaBehaviour(this, "onDeleteAssociation"));

		policyComponent.bindAssociationBehaviour(NodeServicePolicies.OnCreateAssociationPolicy.QNAME,
				SubscriptionsService.TYPE_SUBSCRIPTION_TO_TYPE, new JavaBehaviour(this, "onCreateAssociation"));

		policyComponent.bindAssociationBehaviour(NodeServicePolicies.OnDeleteAssociationPolicy.QNAME,
				SubscriptionsService.TYPE_SUBSCRIPTION_TO_OBJECT, new JavaBehaviour(this, "onDeleteAssociation"));

		policyComponent.bindAssociationBehaviour(NodeServicePolicies.OnCreateAssociationPolicy.QNAME,
				SubscriptionsService.TYPE_SUBSCRIPTION_TO_OBJECT, new JavaBehaviour(this, "onCreateAssociation"));
	}
}
