package ru.it.lecm.notifications.policies;

import org.alfresco.repo.node.NodeServicePolicies;
import org.alfresco.repo.policy.JavaBehaviour;
import ru.it.lecm.base.policies.LogicECMAssociationPolicy;
import ru.it.lecm.notifications.beans.NotificationsService;

/**
 * User: dbashmakov
 * Date: 19.05.2017
 * Time: 16:49
 */
public class TemplatesOnCreateAssociationPolicy extends LogicECMAssociationPolicy {
    @Override
    public final void init() {
        super.init();
        policyComponent.bindAssociationBehaviour(NodeServicePolicies.OnDeleteAssociationPolicy.QNAME,
                NotificationsService.TYPE_NOTIFICATION_TEMPLATE, new JavaBehaviour(this, "onDeleteAssociation"));

        policyComponent.bindAssociationBehaviour(NodeServicePolicies.OnCreateAssociationPolicy.QNAME,
                NotificationsService.TYPE_NOTIFICATION_TEMPLATE, new JavaBehaviour(this, "onCreateAssociation"));
    }
}
