package ru.it.lecm.ord.policy;

import org.alfresco.repo.node.NodeServicePolicies;
import org.alfresco.repo.policy.JavaBehaviour;
import ru.it.lecm.base.policies.LogicECMAssociationPolicy;
import ru.it.lecm.eds.api.EDSGlobalSettingsService;
import ru.it.lecm.ord.api.ORDModel;

/**
 *
 * @author dbayandin
 */
public class AssociationHandlerPolicy extends LogicECMAssociationPolicy {
	@Override
	public final void init() {
		super.init();
		policyComponent.bindAssociationBehaviour(
			NodeServicePolicies.OnDeleteAssociationPolicy.QNAME,
			ORDModel.TYPE_ORD, 
			new JavaBehaviour(this, "onDeleteAssociation"));

		policyComponent.bindAssociationBehaviour(
			NodeServicePolicies.OnCreateAssociationPolicy.QNAME,
			ORDModel.TYPE_ORD, 
			new JavaBehaviour(this, "onCreateAssociation"));
	}
}
