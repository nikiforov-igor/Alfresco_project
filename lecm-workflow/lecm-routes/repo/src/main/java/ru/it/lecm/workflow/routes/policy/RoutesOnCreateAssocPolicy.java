package ru.it.lecm.workflow.routes.policy;

import org.alfresco.repo.node.NodeServicePolicies;
import org.alfresco.repo.policy.JavaBehaviour;
import ru.it.lecm.base.policies.LogicECMAssociationPolicy;
import ru.it.lecm.workflow.routes.api.RoutesModel;


/**
 * Created by APanyukov on 28.09.2016.
 */
public class RoutesOnCreateAssocPolicy extends LogicECMAssociationPolicy{
    @Override
    public void init() {
        super.init();
        policyComponent.bindAssociationBehaviour(NodeServicePolicies.OnDeleteAssociationPolicy.QNAME,
                RoutesModel.TYPE_ROUTE, new JavaBehaviour(this, "onDeleteAssociation"));

        policyComponent.bindAssociationBehaviour(NodeServicePolicies.OnCreateAssociationPolicy.QNAME,
                RoutesModel.TYPE_ROUTE, new JavaBehaviour(this, "onCreateAssociation"));
    }
}
