package ru.it.lecm.errands.policy;

import org.alfresco.repo.node.NodeServicePolicies;
import org.alfresco.repo.policy.JavaBehaviour;
import ru.it.lecm.base.policies.LogicECMAssociationPolicy;
import ru.it.lecm.errands.ErrandsService;

/**
 * Created by APanyukov on 24.01.2017.
 */
public class ErrandsExecutionReportAssociationPolicy extends LogicECMAssociationPolicy {

    @Override
    public final void init() {
        super.init();
        policyComponent.bindAssociationBehaviour(NodeServicePolicies.OnDeleteAssociationPolicy.QNAME,
                ErrandsService.TYPE_ERRANDS_TS_EXECUTION_REPORT, new JavaBehaviour(this, "onDeleteAssociation"));

        policyComponent.bindAssociationBehaviour(NodeServicePolicies.OnCreateAssociationPolicy.QNAME,
                ErrandsService.TYPE_ERRANDS_TS_EXECUTION_REPORT, new JavaBehaviour(this, "onCreateAssociation"));
    }
}
