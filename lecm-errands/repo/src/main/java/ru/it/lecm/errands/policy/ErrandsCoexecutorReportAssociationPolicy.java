package ru.it.lecm.errands.policy;

import org.alfresco.repo.node.NodeServicePolicies;
import org.alfresco.repo.policy.JavaBehaviour;
import ru.it.lecm.base.policies.LogicECMAssociationPolicy;
import ru.it.lecm.documents.beans.DocumentConnectionService;
import ru.it.lecm.errands.ErrandsService;

/**
 * Created by APanyukov on 09.12.2016.
 */
public class ErrandsCoexecutorReportAssociationPolicy extends LogicECMAssociationPolicy {

    @Override
    public final void init() {
        super.init();
        policyComponent.bindAssociationBehaviour(NodeServicePolicies.OnDeleteAssociationPolicy.QNAME,
                ErrandsService.TYPE_ERRANDS_TS_COEXECUTOR_REPORT, new JavaBehaviour(this, "onDeleteAssociation"));

        policyComponent.bindAssociationBehaviour(NodeServicePolicies.OnCreateAssociationPolicy.QNAME,
                ErrandsService.TYPE_ERRANDS_TS_COEXECUTOR_REPORT, new JavaBehaviour(this, "onCreateAssociation"));
    }
}
