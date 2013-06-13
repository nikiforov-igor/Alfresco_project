package ru.it.lecm.delegation.policies;

import org.alfresco.repo.node.NodeServicePolicies;
import org.alfresco.repo.policy.JavaBehaviour;
import ru.it.lecm.base.policies.LogicECMAssociationPolicy;
import ru.it.lecm.delegation.IDelegation;

/**
 *
 * @author VLadimir Malygin
 * @since 13.06.2013 13:43:08
 * @see <p>mailto: <a href="mailto:vmalygin@it.ru">vmalygin@it.ru</a></p>
 */
public class DelegationAssociationPolicy extends LogicECMAssociationPolicy {

	@Override
	public void init() {
		super.init();
		policyComponent.bindAssociationBehaviour(NodeServicePolicies.OnDeleteAssociationPolicy.QNAME,
				IDelegation.TYPE_DELEGATION_OPTS, new JavaBehaviour(this, "onDeleteAssociation"));

		policyComponent.bindAssociationBehaviour(NodeServicePolicies.OnCreateAssociationPolicy.QNAME,
				IDelegation.TYPE_DELEGATION_OPTS, new JavaBehaviour(this, "onCreateAssociation"));

		policyComponent.bindAssociationBehaviour(NodeServicePolicies.OnDeleteAssociationPolicy.QNAME,
				IDelegation.TYPE_PROCURACY, new JavaBehaviour(this, "onDeleteAssociation"));

		policyComponent.bindAssociationBehaviour(NodeServicePolicies.OnCreateAssociationPolicy.QNAME,
				IDelegation.TYPE_PROCURACY, new JavaBehaviour(this, "onCreateAssociation"));
	}

}
