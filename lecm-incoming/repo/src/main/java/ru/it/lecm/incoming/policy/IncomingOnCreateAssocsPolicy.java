package ru.it.lecm.incoming.policy;

import org.alfresco.repo.node.NodeServicePolicies;
import org.alfresco.repo.policy.JavaBehaviour;
import ru.it.lecm.base.policies.LogicECMAssociationPolicy;
import ru.it.lecm.incoming.beans.IncomingServiceImpl;

public class IncomingOnCreateAssocsPolicy extends LogicECMAssociationPolicy {
    @Override
    public final void init() {
        super.init();
        policyComponent.bindAssociationBehaviour(NodeServicePolicies.OnDeleteAssociationPolicy.QNAME,
                IncomingServiceImpl.TYPE_INCOMING, new JavaBehaviour(this, "onDeleteAssociation"));

        policyComponent.bindAssociationBehaviour(NodeServicePolicies.OnCreateAssociationPolicy.QNAME,
                IncomingServiceImpl.TYPE_INCOMING, new JavaBehaviour(this, "onCreateAssociation"));
    }
}
