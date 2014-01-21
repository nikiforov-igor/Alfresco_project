package ru.it.lecm.incoming.policy;

import org.alfresco.repo.node.NodeServicePolicies;
import org.alfresco.repo.policy.JavaBehaviour;
import org.alfresco.service.cmr.repository.NodeRef;
import ru.it.lecm.base.beans.SubstitudeBean;
import ru.it.lecm.base.policies.LogicECMAssociationPolicy;
import ru.it.lecm.incoming.beans.IncomingServiceImpl;

import java.io.Serializable;

public class IncomingOnCreateAssocsPolicy extends LogicECMAssociationPolicy {
    private SubstitudeBean substitute;

    @Override
    public final void init() {
        super.init();
        policyComponent.bindAssociationBehaviour(NodeServicePolicies.OnDeleteAssociationPolicy.QNAME,
                IncomingServiceImpl.TYPE_INCOMING, new JavaBehaviour(this, "onDeleteAssociation"));

        policyComponent.bindAssociationBehaviour(NodeServicePolicies.OnCreateAssociationPolicy.QNAME,
                IncomingServiceImpl.TYPE_INCOMING, new JavaBehaviour(this, "onCreateAssociation"));
    }

    @Override
    protected Serializable getSerializable(final NodeRef node){
        return substitute.getObjectDescription(node);
    }

    public void setSubstitute(SubstitudeBean substitute) {
        this.substitute = substitute;
    }
}
