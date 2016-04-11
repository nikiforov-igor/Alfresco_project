package ru.it.lecm.workflow.review.policy;

import org.alfresco.repo.node.NodeServicePolicies;
import org.alfresco.repo.policy.JavaBehaviour;
import ru.it.lecm.base.policies.LogicECMAssociationPolicy;
import ru.it.lecm.workflow.review.api.ReviewService;

/**
 *
 * @author vkuprin
 */
public class ReviewListAssociationPolicy extends LogicECMAssociationPolicy {
	
	@Override
    public final void init() {
        super.init();
        policyComponent.bindAssociationBehaviour(NodeServicePolicies.OnDeleteAssociationPolicy.QNAME,
                ReviewService.TYPE_REVIEW_LIST_REVIEW_LIST_ITEM, new JavaBehaviour(this, "onDeleteAssociation"));

        policyComponent.bindAssociationBehaviour(NodeServicePolicies.OnCreateAssociationPolicy.QNAME,
                ReviewService.TYPE_REVIEW_LIST_REVIEW_LIST_ITEM, new JavaBehaviour(this, "onCreateAssociation"));
    }
	
}
