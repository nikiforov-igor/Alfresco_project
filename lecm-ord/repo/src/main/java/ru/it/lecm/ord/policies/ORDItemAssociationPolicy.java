package ru.it.lecm.ord.policies;

import org.alfresco.repo.node.NodeServicePolicies;
import org.alfresco.repo.policy.JavaBehaviour;
import ru.it.lecm.base.policies.LogicECMAssociationPolicy;
import ru.it.lecm.ord.api.ORDModel;

/**
 * Created by APanyukov on 28.02.2017.
 */
public class ORDItemAssociationPolicy extends LogicECMAssociationPolicy {

    @Override
    public final void init() {
        super.init();
        policyComponent.bindAssociationBehaviour(NodeServicePolicies.OnDeleteAssociationPolicy.QNAME,
                ORDModel.TYPE_ORD_TABLE_ITEM, new JavaBehaviour(this, "onDeleteAssociation"));

        policyComponent.bindAssociationBehaviour(NodeServicePolicies.OnCreateAssociationPolicy.QNAME,
                ORDModel.TYPE_ORD_TABLE_ITEM, new JavaBehaviour(this, "onCreateAssociation"));
    }
}