package ru.it.lecm.eds.policy;

import org.alfresco.repo.node.NodeServicePolicies;
import org.alfresco.repo.policy.JavaBehaviour;
import ru.it.lecm.base.policies.LogicECMAssociationPolicy;
import ru.it.lecm.eds.api.EDSGlobalSettingsService;

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
			EDSGlobalSettingsService.TYPE_POTENTIAL_ROLE, 
			new JavaBehaviour(this, "onDeleteAssociation"));

		policyComponent.bindAssociationBehaviour(
			NodeServicePolicies.OnCreateAssociationPolicy.QNAME,
			EDSGlobalSettingsService.TYPE_POTENTIAL_ROLE, 
			new JavaBehaviour(this, "onCreateAssociation"));
	}
}
