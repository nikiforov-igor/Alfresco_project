package ru.it.lecm.contracts.policies;

import org.alfresco.repo.node.NodeServicePolicies;
import org.alfresco.repo.policy.JavaBehaviour;
import ru.it.lecm.base.policies.LogicECMAssociationPolicy;
import ru.it.lecm.contracts.beans.ContractsBeanImpl;

/**
 * User: dbashmakov
 * Date: 23.04.13
 * Time: 17:30
 */
public class AdditionalDocumentOnCreateAssocsPolicy extends LogicECMAssociationPolicy {
    @Override
    public final void init() {
        super.init();
        policyComponent.bindAssociationBehaviour(NodeServicePolicies.OnDeleteAssociationPolicy.QNAME,
                ContractsBeanImpl.TYPE_CONTRACTS_ADDICTIONAL_DOCUMENT, new JavaBehaviour(this, "onDeleteAssociation"));

        policyComponent.bindAssociationBehaviour(NodeServicePolicies.OnCreateAssociationPolicy.QNAME,
                ContractsBeanImpl.TYPE_CONTRACTS_ADDICTIONAL_DOCUMENT, new JavaBehaviour(this, "onCreateAssociation"));
    }
}
