package ru.it.lecm.workflow.review.policy;

import org.alfresco.repo.node.NodeServicePolicies;
import org.alfresco.repo.policy.JavaBehaviour;
import ru.it.lecm.base.policies.LogicECMAssociationPolicy;
import ru.it.lecm.workflow.review.ReviewServiceImpl;

/**
 *
 * @author vkuprin
 */
public class ReviewListAssociationPolicy extends LogicECMAssociationPolicy {
	
	@Override
    public final void init() {
        super.init();
        policyComponent.bindAssociationBehaviour(NodeServicePolicies.OnDeleteAssociationPolicy.QNAME,
                ReviewServiceImpl.TYPE_REVIEW_LIST_REWIEW_LIST_ITEM, new JavaBehaviour(this, "onDeleteAssociation"));

        policyComponent.bindAssociationBehaviour(NodeServicePolicies.OnCreateAssociationPolicy.QNAME,
                ReviewServiceImpl.TYPE_REVIEW_LIST_REWIEW_LIST_ITEM, new JavaBehaviour(this, "onCreateAssociation"));
    }
	
}
