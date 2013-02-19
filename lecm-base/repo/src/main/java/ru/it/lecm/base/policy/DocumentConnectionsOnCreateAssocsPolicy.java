package ru.it.lecm.base.policy;

import org.alfresco.repo.node.NodeServicePolicies;
import org.alfresco.repo.policy.JavaBehaviour;
import ru.it.lecm.base.beans.DocumentConnectionService;
import ru.it.lecm.base.policies.LogicECMAssociationPolicy;

/**
 * User: AIvkin
 * Date: 19.02.13
 * Time: 14:36
 */
public class DocumentConnectionsOnCreateAssocsPolicy extends LogicECMAssociationPolicy {
	@Override
	public final void init() {
		super.init();
		policyComponent.bindAssociationBehaviour(NodeServicePolicies.OnDeleteAssociationPolicy.QNAME,
				DocumentConnectionService.TYPE_CONNECTION, new JavaBehaviour(this, "onDeleteAssociation"));

		policyComponent.bindAssociationBehaviour(NodeServicePolicies.OnCreateAssociationPolicy.QNAME,
				DocumentConnectionService.TYPE_CONNECTION, new JavaBehaviour(this, "onCreateAssociation"));
	}
}
