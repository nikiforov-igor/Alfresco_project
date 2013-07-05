package ru.it.lecm.contractors.policies;

import org.alfresco.repo.node.NodeServicePolicies;
import org.alfresco.repo.policy.JavaBehaviour;
import ru.it.lecm.base.policies.LogicECMAssociationPolicy;
import ru.it.lecm.contractors.api.Contractors;

/**
 * User: AIvkin
 * Date: 05.07.13
 * Time: 14:37
 */
public class ContractorsOnCreateAssocsPolicy extends LogicECMAssociationPolicy {
	@Override
	public final void init() {
		super.init();
		policyComponent.bindAssociationBehaviour(NodeServicePolicies.OnDeleteAssociationPolicy.QNAME,
				Contractors.TYPE_CONTRACTOR, new JavaBehaviour(this, "onDeleteAssociation"));

		policyComponent.bindAssociationBehaviour(NodeServicePolicies.OnCreateAssociationPolicy.QNAME,
				Contractors.TYPE_CONTRACTOR, new JavaBehaviour(this, "onCreateAssociation"));
	}
}

